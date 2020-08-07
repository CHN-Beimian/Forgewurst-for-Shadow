package net.wurstclient.forge.hacks.misc;

import java.text.Format;
import org.objectweb.asm.tree.analysis.Value;

import net.minecraft.client.resources.I18n;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.wurstclient.fmlevents.WUpdateEvent;
import net.wurstclient.forge.Category;
import net.wurstclient.forge.ForgeWurst;
import net.wurstclient.forge.Hack;
import net.wurstclient.forge.KeybindList;
import net.wurstclient.forge.settings.SliderSetting;
import net.wurstclient.forge.settings.SliderSetting.ValueDisplay;

public class AutoTextHack extends Hack {
	private final SliderSetting time = new SliderSetting("time", 5, 1, 20, 1, ValueDisplay.DECIMAL);
	int a = 0;

	public AutoTextHack() {
		super("AutoText", "Automatically send your behavior message");
		setCategory(Category.MISC);
		addSetting(time);
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

		if (mc.player == null)
			return;
		if (mc.gameSettings.keyBindForward.isPressed()) {
			a = a + 1;
			if (a % time.getValue() == 0) {
				send("");
				a = 0;
			}

		}
		if (mc.gameSettings.keyBindJump.isPressed()) {
			a = a + 1;
			if (a % time.getValue() == 0) {
				send("");
				a = 0;
			}

		}
		if (mc.gameSettings.keyBindLeft.isPressed()) {
			a = a + 1;
			if (a % time.getValue() == 0) {
				send("");
				a = 0;
			}

		}
		if (mc.gameSettings.keyBindRight.isPressed()) {
			a = a + 1;
			if (a % time.getValue() == 0) {
				send("");
				a = 0;
			}

		}

	}

	private void send(String string) {
		mc.player.sendChatMessage(I18n.format(string));
	}

}
