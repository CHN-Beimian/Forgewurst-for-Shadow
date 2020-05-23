package net.wurstclient.hothack;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import net.minecraft.client.Minecraft;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.wurstclient.forge.CommandList;
import net.wurstclient.forge.CommandProcessor;
import net.wurstclient.forge.HackList;
import net.wurstclient.forge.IJHotHack;
import net.wurstclient.forge.IngameHUD;
import net.wurstclient.forge.KeybindList;
import net.wurstclient.forge.KeybindProcessor;
import net.wurstclient.forge.clickgui.ClickGui;
import net.wurstclient.hothack.loader.EventLoader;
import net.wurstclient.hothack.loader.HackKeyLoader;
import net.wurstclient.hothack.loader.HackLoader;

public class IJHack {
public static IJHack ij;
public Path configFolder;
	public IJHack() {
		ij=this;
		
		/*
		 * configFolder = Minecraft.getMinecraft().mcDataDir.toPath().resolve("Shadow");
		 * try { Files.createDirectories(configFolder); }catch(IOException e) { throw
		 * new RuntimeException(e); }
		 * 
		 * 
		 * HackLoader.instance(); new HackKeyLoader();
		 * 
		 * HackList hax=new HackList(configFolder.resolve("enabled-hacks.json"),
		 * configFolder.resolve("settings.json"));
		 * 
		 * 
		 * hax.loadEnabledHacks(); hax.loadSettings(); CommandList cmd=new
		 * CommandList(); KeybindList keybinds=new
		 * KeybindList(configFolder.resolve("keybinds.json")); keybinds.init();
		 * 
		 * 
		 * ClickGui gui =new ClickGui(configFolder.resolve("windows.json"));
		 * gui.init(hax); IngameHUD hud = new IngameHUD(hax, gui);
		 * MinecraftForge.EVENT_BUS.register(hud);
		 * 
		 * 
		 * CommandProcessor cmdProcessor = new CommandProcessor(cmd);
		 * MinecraftForge.EVENT_BUS.register(cmdProcessor);
		 * 
		 * KeybindProcessor keybindProcessor = new KeybindProcessor(hax, keybinds,
		 * cmdProcessor); MinecraftForge.EVENT_BUS.register(keybindProcessor);
		 */
		
		
		HackLoader.instance();
		FMLCommonHandler.instance().bus().register(new EventLoader());
		MinecraftForge.EVENT_BUS.register(new EventLoader());
		
		MinecraftForge.EVENT_BUS.register(this);
		
		
	}
	public static void inject() {
		if(instance()==null) {
			ij =new IJHack();
		}
	}
	
	public static IJHack instance() {
		return ij;
	}
	
}
