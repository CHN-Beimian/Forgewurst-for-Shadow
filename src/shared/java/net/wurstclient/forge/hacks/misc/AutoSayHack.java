package net.wurstclient.forge.hacks.misc;

import java.awt.dnd.Autoscroll;

import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.wurstclient.fmlevents.WUpdateEvent;
import net.wurstclient.forge.Category;
import net.wurstclient.forge.Hack;
import net.wurstclient.forge.compatibility.WMinecraft;
import net.wurstclient.forge.loader.ModSigmaSayLoader;
import net.wurstclient.forge.settings.SliderSetting;
import net.wurstclient.forge.settings.SliderSetting.ValueDisplay;

public class AutoSayHack extends Hack{
	private final SliderSetting interval = new SliderSetting("interval", 100, 20, 500, 10, ValueDisplay.DECIMAL);
	public int nu;
	String lastMeme = "";
    String lastMeme2 = "";
    String lastMeme3 = "";
	public AutoSayHack() {
		super("AutoSay","");
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
		nu++;
		if (nu >= interval.getValueI()) {
			nu = 0;
		String phrase = ModSigmaSayLoader.phrases.get((int) (Math.random() * ModSigmaSayLoader.phrases.size()));
		while(phrase.equalsIgnoreCase(lastMeme) || phrase.equalsIgnoreCase(lastMeme2) || phrase.equalsIgnoreCase(lastMeme3)){
			phrase = ModSigmaSayLoader.phrases.get((int) (Math.random() * ModSigmaSayLoader.phrases.size()));
		}
		WMinecraft.getPlayer().sendChatMessage(phrase);
		lastMeme = lastMeme2;
		lastMeme2 = lastMeme3;
		lastMeme3 = phrase;
		}
	}
	
}
