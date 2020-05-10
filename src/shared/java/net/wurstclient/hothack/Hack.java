package net.wurstclient.hothack;

import net.minecraft.client.Minecraft;
import net.minecraft.client.settings.KeyBinding;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;

public class Hack {
	public static final Minecraft mc=Minecraft.getMinecraft();
	public static int time=0;
	public void onEnable()
	{
		
	}
	public void onDisable() 
	{
		
	}
	@SubscribeEvent
	public void onTick(TickEvent.PlayerTickEvent event) {
		
	}
	
	public static boolean on(KeyBinding key) {
		boolean isOn = false;
		if (key.isPressed()) {
			time = time + 1;
		}
		if (time % 2 != 0) {
			isOn= true;
		} else if(time%2==0){
			isOn= false;
		}
		return isOn;
	}
}
