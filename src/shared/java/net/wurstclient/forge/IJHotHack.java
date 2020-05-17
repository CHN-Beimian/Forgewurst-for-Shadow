package net.wurstclient.forge;

import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.wurstclient.hothack.loader.EventLoader;
import net.wurstclient.hothack.loader.HackKeyLoader;
import net.wurstclient.hothack.loader.HackLoader;

public class IJHotHack {
	public static IJHotHack hothack;
	
	public IJHotHack() {
		 HackLoader.instance();
		 new HackKeyLoader();
		FMLCommonHandler.instance().bus().register(new EventLoader());
		MinecraftForge.EVENT_BUS.register(new EventLoader());
		
		MinecraftForge.EVENT_BUS.register(this);
		
		
	}
	public static IJHotHack instance() {
		return hothack;
	}
	public static void inject() {
		if(instance()==null) {
			hothack =new IJHotHack();
		}
	}
	
}
