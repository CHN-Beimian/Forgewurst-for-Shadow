package net.wurstclient.hothack;

import java.awt.Dimension;
import java.awt.Toolkit;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import org.lwjgl.opengl.Display;

import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.client.settings.KeyBinding;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.asm.transformers.deobf.FMLDeobfuscatingRemapper;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.wurstclient.fmlevents.listener.EventManager;
import net.wurstclient.forge.CommandList;
import net.wurstclient.forge.CommandProcessor;
import net.wurstclient.forge.ForgeWurst;
import net.wurstclient.forge.Friends;
import net.wurstclient.forge.GoogleAnalytics;
import net.wurstclient.forge.HackList;
import net.wurstclient.forge.IngameHUD;
import net.wurstclient.forge.KeybindList;
import net.wurstclient.forge.KeybindProcessor;
import net.wurstclient.forge.analytics.JGoogleAnalyticsTracker;
import net.wurstclient.forge.clickgui.ClickGui;
import net.wurstclient.forge.compatibility.WMinecraft;
import net.wurstclient.forge.loader.ModEnemyLoader;
import net.wurstclient.forge.loader.ModFriendsLoader;
import net.wurstclient.forge.loader.ModKeyLoader;
import net.wurstclient.forge.loader.ModMessageLoader;
import net.wurstclient.forge.loader.ModNoticeLoader;
import net.wurstclient.forge.update.WurstUpdater;
import net.wurstclient.hothack.hacks.Fly;
import net.wurstclient.hothack.init.HackLoader;
import net.wurstclient.hothack.init.HotHackKey;

public class HotHack {
	public final ForgeWurst wurst = ForgeWurst.getForgeWurst();

	public static int time = 0;
	public static HotHack hothack;

	public final HotHackKey key = HotHackKey.instance();
	public final Minecraft mc = Minecraft.getMinecraft();
	public final EntityPlayerSP player = mc.player;

	public Fly fly = new Fly();

	public HotHack() {
		
		new HotHackKey();
		MinecraftForge.EVENT_BUS.register(this);

		/*
		 * String mcClassName = Minecraft.class.getName().replace(".", "/");
		 * FMLDeobfuscatingRemapper remapper = FMLDeobfuscatingRemapper.INSTANCE;
		 * obfuscated = !mcClassName.equals(remapper.unmap(mcClassName));
		 * wurst.obfuscated = true; wurst.configFolder =
		 * Minecraft.getMinecraft().mcDataDir.toPath().resolve("Shadow"); try {
		 * Files.createDirectories(wurst.configFolder); }catch(IOException e) { throw
		 * new RuntimeException(e); } wurst.friends=new
		 * Friends(wurst.configFolder.resolve("friends.json")); wurst.friends.init();
		 * wurst.hax = new HackList(wurst.configFolder.resolve("enabled-hacks.json"),
		 * wurst.configFolder.resolve("settings.json")); wurst.hax.loadEnabledHacks();
		 * wurst.hax.loadSettings();
		 * 
		 * wurst.cmds = new CommandList();
		 * 
		 * wurst.keybinds = new
		 * KeybindList(wurst.configFolder.resolve("keybinds.json"));
		 * wurst.keybinds.init();
		 * 
		 * wurst.gui = new ClickGui(wurst.configFolder.resolve("windows.json"));
		 * wurst.gui.init(wurst.hax);
		 * 
		 * JGoogleAnalyticsTracker.setProxy(System.getenv("http_proxy"));
		 * wurst.analytics = new GoogleAnalytics("UA-52838431-17",
		 * "client.forge.wurstclient.net",
		 * wurst.configFolder.resolve("analytics.json")); wurst.analytics.loadConfig();
		 * 
		 * Dimension screen = Toolkit.getDefaultToolkit().getScreenSize();
		 * wurst.analytics.getConfigData() .setScreenResolution(screen.width + "x" +
		 * screen.height);
		 * 
		 * wurst.hud = new IngameHUD(wurst.hax, wurst.gui);
		 * MinecraftForge.EVENT_BUS.register(wurst.hud);
		 * 
		 * 
		 * wurst.cmdProcessor = new CommandProcessor(wurst.cmds);
		 * MinecraftForge.EVENT_BUS.register(wurst.cmdProcessor);
		 * 
		 * wurst.keybindProcessor = new KeybindProcessor(wurst.hax, wurst.keybinds,
		 * wurst.cmdProcessor);
		 * MinecraftForge.EVENT_BUS.register(wurst.keybindProcessor);
		 * 
		 * wurst.updater = new WurstUpdater();
		 * MinecraftForge.EVENT_BUS.register(wurst.updater);
		 * wurst.analytics.trackPageView("/mc" + WMinecraft.VERSION + "/v" +
		 * wurst.VERSION, "Hurricane " + wurst.VERSION + " MC" + WMinecraft.VERSION);
		 */

	}

	public static void inject() {
		if (instance() == null) {
			hothack = new HotHack();
		}
	}

	public static HotHack instance() {
		return hothack;
	}

	public static boolean on(KeyBinding key) {
		boolean isOn = false;
		if (key.isPressed()) {
			time = time + 1;
		}
		if (time % 2 != 0) {
			isOn = true;
		} else if (time % 2 == 0) {
			isOn = false;
		}
		return isOn;
	}

	@SubscribeEvent
	public void PlayerTick(TickEvent.PlayerTickEvent event) {

		EntityPlayerSP player = mc.player;

		if (player.world == null && event.side != Side.CLIENT)
			return;
		if (key.fly.isPressed()) {
			time = time + 1;
		}

		if (time % 2 != 0) {
			fly.onEnable();
			fly.onUpdate(event);
		} else {
			fly.onDisable();
		}

	}

}
