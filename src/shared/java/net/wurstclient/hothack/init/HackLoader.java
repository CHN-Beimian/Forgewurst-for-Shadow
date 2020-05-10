package net.wurstclient.hothack.init;

import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.client.settings.KeyBinding;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import net.wurstclient.hothack.hacks.Fly;

public class HackLoader {
	private static HackLoader instance;
	public final Minecraft mc=Minecraft.getMinecraft();
	public final EntityPlayerSP player=mc.player;
	
	public Fly fly=new Fly();
	
	/*
	 * public void update() { if(on(HotHackKey.fly)) { fly.onEnable(); }else {
	 * fly.onDisable(); } }
	 */
	

	public void onTick(TickEvent.PlayerTickEvent event){
		if(on(HotHackKey.fly)) {
			fly.onEnable();
			fly.onUpdate(event);
		}else if(on(HotHackKey.fly)==false){
			fly.onDisable();
		}
		
		
	}
	
	public static boolean on(KeyBinding key) {
		int time = 0;
		if (key.isPressed()) {
			time = time + 1;
		}
		if (time % 2 != 0) {
			return true;
		} else {
			return false;
		}
	}
	public static HackLoader getinstance() {
		if(instance==null) {
			instance=new HackLoader();
		}
		return instance;
	}
}
