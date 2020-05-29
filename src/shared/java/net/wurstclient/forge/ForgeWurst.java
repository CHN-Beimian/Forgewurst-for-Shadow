/*
 * Copyright (C) 2017 - 2019 | Wurst-Imperium | All rights reserved.
 *
 * This source code is subject to the terms of the GNU General Public
 * License, version 3. If a copy of the GPL was not distributed with this
 * file, You can obtain one at: https://www.gnu.org/licenses/gpl-3.0.txt
 */
package net.wurstclient.forge;

import java.awt.Dimension;
import java.io.PrintStream;
import java.lang.reflect.Field;
import java.net.URISyntaxException;
import java.util.List;

import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.LoadController;
import net.minecraftforge.fml.common.Loader;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.ModContainer;
import net.minecraftforge.fml.common.event.FMLLoadCompleteEvent;
import org.lwjgl.opengl.Display;
import java.awt.Toolkit;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.logging.Logger;

import org.lwjgl.opengl.Display;

import net.minecraft.client.Minecraft;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.LoadController;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.Mod.Instance;
import net.minecraftforge.fml.common.asm.transformers.deobf.FMLDeobfuscatingRemapper;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLLoadCompleteEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.wurstclient.fmlevents.listener.EventManager;
import net.wurstclient.forge.analytics.JGoogleAnalyticsTracker;
import net.wurstclient.forge.clickgui.ClickGui;
import net.wurstclient.forge.compatibility.WMinecraft;
import net.wurstclient.forge.hacks.ClickGuiHack.InventoryButtonAdder;
import net.wurstclient.forge.loader.ModEnemyLoader;
import net.wurstclient.forge.loader.ModFriendsLoader;
import net.wurstclient.forge.loader.ModKeyLoader;
import net.wurstclient.forge.loader.ModMessageLoader;
import net.wurstclient.forge.loader.ModNoticeLoader;
import net.wurstclient.forge.update.WurstUpdater;
import net.wurstclient.forge.utils.management.FontManager;
import net.wurstclient.zenwix.hothack.loader.EventLoader;

@Mod(modid = ForgeWurst.MODID,
	version = ForgeWurst.VERSION,
	updateJSON = "https://afdian.net/@beimian")
public  class ForgeWurst
{
	public static final String MODID = "hurricane";
	public static final String VERSION = "1.1.0";
	public static Logger log = Logger.getLogger("GraphRevo");
	
	
	
	@Instance(MODID)
	public static ForgeWurst forgeWurst;

	public boolean enabled = true;
	public boolean obfuscated;
	
	public Path configFolder;
	
	public HackList hax;
	public CommandList cmds;
	public KeybindList keybinds;
	public ClickGui gui;
	public GoogleAnalytics analytics;
	public Friends friends;


	public IngameHUD hud;
	public CommandProcessor cmdProcessor;
	public KeybindProcessor keybindProcessor;
	public WurstUpdater updater;
	

	@EventHandler
	public void init(FMLInitializationEvent event)
	{
	
		log.info("Starting Shadow " + ForgeWurst.VERSION); //启动信息
        log.info("Copyright (c) Zenwix, 2020-2021");
		/* FontManager fm= new FontManager(); */
       
        
        new ModNoticeLoader();
        new ModEnemyLoader();
        new ModFriendsLoader();
		new ModMessageLoader();
		new ModKeyLoader();
		EventManager eventManager = new EventManager(this);
		if(event.getSide() == Side.SERVER)
			return;
		String mcClassName = Minecraft.class.getName().replace(".", "/");
		FMLDeobfuscatingRemapper remapper = FMLDeobfuscatingRemapper.INSTANCE;
		obfuscated = !mcClassName.equals(remapper.unmap(mcClassName));
		
		configFolder =
			Minecraft.getMinecraft().mcDataDir.toPath().resolve("Shadow");
		try
		{
			Files.createDirectories(configFolder);
		}catch(IOException e)
		{
			throw new RuntimeException(e);
		}
		friends=new Friends(configFolder.resolve("friends.json"));
		friends.init();
		hax = new HackList(configFolder.resolve("enabled-hacks.json"),
			configFolder.resolve("settings.json"));
		hax.loadEnabledHacks();
		hax.loadSettings();
		
		cmds = new CommandList();
		
		keybinds = new KeybindList(configFolder.resolve("keybinds.json"));
		keybinds.init();
		
		gui = new ClickGui(configFolder.resolve("windows.json"));
		gui.init(hax);
		
		JGoogleAnalyticsTracker.setProxy(System.getenv("http_proxy"));
		analytics = new GoogleAnalytics("UA-52838431-17",
			"client.forge.wurstclient.net",
			configFolder.resolve("analytics.json"));
		analytics.loadConfig();
		
		Dimension screen = Toolkit.getDefaultToolkit().getScreenSize();
		analytics.getConfigData()
			.setScreenResolution(screen.width + "x" + screen.height);
		
		hud = new IngameHUD(hax, gui);
		MinecraftForge.EVENT_BUS.register(hud);
		
		
		cmdProcessor = new CommandProcessor(cmds);
		MinecraftForge.EVENT_BUS.register(cmdProcessor);
		
		keybindProcessor = new KeybindProcessor(hax, keybinds, cmdProcessor);
		MinecraftForge.EVENT_BUS.register(keybindProcessor);
		
		updater = new WurstUpdater();
		MinecraftForge.EVENT_BUS.register(updater);
		analytics.trackPageView("/mc" + WMinecraft.VERSION + "/v" + VERSION,
			"Hurricane " + VERSION + " MC" + WMinecraft.VERSION);
	}
	public static ForgeWurst getForgeWurst()
	{
		return forgeWurst;
	}
	
	public boolean isObfuscated()
	{
		return obfuscated;
	}
	
	public HackList getHax()
	{
		return hax;
	}
	
	public CommandList getCmds()
	{
		return cmds;
	}
	
	public KeybindList getKeybinds()
	{
		return keybinds;
	}
	
	public ClickGui getGui()
	{
		return gui;
	}
	public Path getWurstFolder()
	{
		return configFolder;
	}

	public boolean isEnabled() {
		// TODO 自动生成的方法存根
		return enabled;
	}
	public Friends getFriends() {
		return friends;
	}
	public IngameHUD getHud() {
		return hud;
	}

	/*
	 * net.minecraftforge.fml.common.Loader.instance().getModClassLoader().addFile(
	 * new File("G:/__java_hot__/__showTime__/mods/xue.jar"))
	 * net.minecraftforge.fml.common.Loader.instance().getModClassLoader().loadClass
	 * ("com.xue.main").newInstance()
	 */
}