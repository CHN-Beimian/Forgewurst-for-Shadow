package net.wurstclient.hothack.loader;

import net.minecraft.block.Block;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.client.multiplayer.WorldClient;
import net.minecraft.client.network.NetHandlerPlayClient;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.client.event.RenderGameOverlayEvent.Text;
import net.minecraftforge.client.event.RenderWorldLastEvent;
import net.minecraftforge.event.entity.EntityEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import net.wurstclient.hothack.entry.Hack;
import net.wurstclient.hothack.entry.HackEntry;
import net.wurstclient.hothack.hacks.movement.Fly;

import org.lwjgl.input.Keyboard;
import org.lwjgl.opengl.Display;

public class EventLoader {
	int key=0;
	private static Minecraft mc = Minecraft.getMinecraft();
	EntityPlayerSP player = mc.player;
	private int ticks;
	private int tpdelay;
	public static String wj;
	public static Block selectedBlock = null;
	public static String BT = "ForgeWurst For Shadow ";
	SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss");
	private boolean[] keyStates = new boolean['?'];

	@SubscribeEvent
	public void onTick(TickEvent.ServerTickEvent event) {
		Iterator var3 = HackEntry.instance.hacks.iterator();
		while (var3.hasNext()) {
			Hack mod = (Hack) var3.next();
			if ((mod.isEnable()) && (mc.world != null)) {
				mod.onTick();
			}
		}
	}

	/*
	 * @SubscribeEvent public void onGameOverlay(RenderGameOverlayEvent.Text event)
	 * { for (YAWWindow windows : YouAlwaysWinClickGui.windows) { if
	 * (windows.isPinned()) { windows.draw(0, 0); } } }
	 */

	@SubscribeEvent
	public void onTicks(TickEvent.ClientTickEvent event) {
		SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss");
		Display.setResizable(true);
		Display.setTitle(BT + "| " + wj + " |  " + sdf.format(new Date()) + "  ");
		Iterator var4 = HackEntry.instance.hacks.iterator();
		while (var4.hasNext()) {
			Hack mod = (Hack) var4.next();
			if ((mod.isEnable()) && (mc.world != null)) {
				mod.onTicks();
			}
			/*
			 * if(mod.getKey().isPressed()) { key=key+1; } if (key%2!=0&& (mc.world !=
			 * null)) { mod.toggle(); break; }
			 */
			 if ((checkAndSaveKeyState(mod.getKey())) && (mc.world != null))
		      {
		        mod.toggle();
		        break;
		      }
			 if ((checkAndSaveKeyState(HackKeyLoader.fly)) && (mc.world != null))
		      {
			        mod.toggle();
			        break;
			  }
			
		}
	}

	public static boolean checkAndSaveKeyState(KeyBinding key2) {
	
		return key2.isPressed();
	}

	@SubscribeEvent
	public void onPlayerUpdate(TickEvent.PlayerTickEvent event) {
		Iterator var3 = HackEntry.instance.hacks.iterator();
		while (var3.hasNext()) {
			Hack mod = (Hack) var3.next();
			if ((mod.isEnable()) && (mc.world != null)) {
				mod.onUpdate();
			}
		}
	}

	@SubscribeEvent
	public void onWorldUpdate(TickEvent.WorldTickEvent event) {
		Iterator var3 = HackEntry.instance.hacks.iterator();
		while (var3.hasNext()) {
			Hack mod = (Hack) var3.next();
			if ((mod.isEnable()) && (mc.world != null)) {
				mod.onUpdate();
			}
		}
	}

	@SubscribeEvent
	public void onRenderWorld(RenderWorldLastEvent event) {
		Iterator var3 = HackEntry.instance.hacks.iterator();
		while (var3.hasNext()) {
			Hack mod = (Hack) var3.next();
			if ((mod.isEnable()) && (mc.world != null)) {
				mod.onWorldRender();
			}
		}
	}

	@SubscribeEvent
	public void onRenderIngame(RenderGameOverlayEvent.Text event) {
		Iterator var3 = HackEntry.instance.hacks.iterator();
		while (var3.hasNext()) {
			Hack mod = (Hack) var3.next();
			if ((mod.isEnable()) && (mc.world != null)) {
				mod.onRenderIngame();
			}
		}
	}

	@SubscribeEvent
	public void HuangBai(TickEvent.ClientTickEvent event) {
		Display.setResizable(true);
		Display.setTitle(BT + "| " + wj + " |" + this.sdf.format(new Date()) + "  ");
	}

	/*
	 * @SubscribeEvent public void onFly(TickEvent.PlayerTickEvent event) {
	 * if(mc.player==null) return; if(HackKeyLoader.fly.isPressed()) { key=key+1; }
	 * if(key%2!=0) { Fly.instance().onTicks(); }
	 * 
	 * }
	 */
	public boolean checkAndSaveKeyState(int key)
	  {
	    return mc.currentScreen == null;
	  }
}
