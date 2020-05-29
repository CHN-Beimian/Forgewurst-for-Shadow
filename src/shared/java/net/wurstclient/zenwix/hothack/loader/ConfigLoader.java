package net.wurstclient.zenwix.hothack.loader;

import java.io.File;
import java.nio.file.Path;

import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;

public class ConfigLoader {
	private static Configuration configuration;
	
	 public ConfigLoader(){
	        configuration = new Configuration();
	        configuration.load();
	        load();
	    }
	public static void load(){
		
	}
}
