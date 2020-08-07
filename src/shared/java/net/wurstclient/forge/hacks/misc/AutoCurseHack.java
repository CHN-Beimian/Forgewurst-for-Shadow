package net.wurstclient.forge.hacks.misc;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import com.google.common.eventbus.Subscribe;

import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.client.FMLClientHandler;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.wurstclient.fmlevents.WUpdateEvent;
import net.wurstclient.forge.Category;
import net.wurstclient.forge.Hack;
import net.wurstclient.forge.compatibility.WMinecraft;
import net.wurstclient.forge.loader.ModMessageLoader;
import net.wurstclient.forge.settings.CheckboxSetting;
import net.wurstclient.forge.settings.SliderSetting;
import net.wurstclient.forge.settings.SliderSetting.ValueDisplay;

public class AutoCurseHack extends Hack {
	private CheckboxSetting all = new CheckboxSetting("All", true);
	private static final Random random = new Random();
	private final SliderSetting interval = new SliderSetting("interval", 100, 20, 500, 10, ValueDisplay.DECIMAL);
	public int nu;

	public AutoCurseHack() {
		super("AutoCurse", "Send curse message automatically");

		setCategory(Category.MISC);
		addSetting(interval);
	}

	@Override
	protected void onEnable() {
		MinecraftForge.EVENT_BUS.register(this);
	}

	@Override
	protected void onDisable() {
		MinecraftForge.EVENT_BUS.unregister(this);
	}

	@SubscribeEvent
	public void onUpdate(WUpdateEvent event) {
		List<String> list = ModMessageLoader.sentences;
		/*
		 * List<String> list =new ArrayList<String>(); list.add("you’re a jerk"); list.
		 * add("Preachers can talk but never teach, Unless theypractise what they preach."
		 * ); list.add("One cannot make a silk purse out of a sow's ear.");
		 * list.add("Birdlike,not arrogant"); list.add("You’re son of bitch!");
		 * list.add("You damned (disgusting) bastard!");
		 * list.add("It is too late to lock the stable door when the steedis stolen.");
		 * list.add("Don'ttalktomelikethat!"); list.add("You make me sick!");
		 * list.add("What’s wrong with you?"); list.add("savage?");
		 * list.add("Who do you think you are?"); list.add("scamp?"); list.add("scum?");
		 * list.add("scumbag?"); list.add("What the hell did you see that?");
		 * list.add("What the hell is wrong with you?"); list.add("Knock it off");
		 * list.add("Get out of my face"); list.add("Leave me alone.");
		 * list.add("Take a hike!"); list.add("Cut it out");
		 */
		nu++;
		if (nu >= interval.getValueI() && list.size() > 0) {
			nu = 0;
			if (all.isChecked()) {
				WMinecraft.getPlayer().sendChatMessage("@"+list.get(random.nextInt(list.size())));
			} else {
				FMLClientHandler.instance().getClientPlayerEntity()
						.sendChatMessage(list.get(random.nextInt(list.size())));
			}

		}

	}
}
