package net.wurstclient.hothack;

import net.minecraft.client.settings.KeyBinding;
import net.minecraftforge.common.MinecraftForge;
import net.wurstclient.hothack.init.HotHackKey;

public class HotHack {
	public static HotHack hothack;
	
	public HotHack() {
		new HotHackKey();
		MinecraftForge.EVENT_BUS.register(this);
	}
	public static void inject() {
		if(instance()==null) {
			hothack=new HotHack();
		}
	}
	public static HotHack instance() {
		return hothack;
	}
	
	public static boolean on(KeyBinding key) {
		int time=0;
		if(key.isPressed()) {
			time=time+1;
		}
		if(time%2!=0) {
			return true;
		}else {
			return false;
		}
	}
	
}
