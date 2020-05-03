package net.wurstclient.forge.hacks;

import org.lwjgl.input.Keyboard;

import net.minecraft.client.gui.GuiChat;
import net.minecraft.client.gui.GuiIngameMenu;
import net.minecraft.client.gui.GuiOptions;
import net.minecraft.client.gui.GuiScreenOptionsSounds;
import net.minecraft.client.gui.GuiVideoSettings;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.network.play.client.CPacketVehicleMove;
import net.minecraft.network.play.server.SPacketMoveVehicle;
import net.minecraftforge.client.event.GuiOpenEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent.PlayerTickEvent;
import net.wurstclient.fmlevents.LocalPlayerUpdateEvent;
import net.wurstclient.fmlevents.WUpdateEvent;
import net.wurstclient.forge.Category;
import net.wurstclient.forge.Hack;
import net.wurstclient.forge.KeybindList;
import net.wurstclient.forge.KeybindList.Keybind;
import net.wurstclient.forge.KeybindProcessor;
import net.wurstclient.forge.clickgui.ClickGui;
import net.wurstclient.forge.loader.ModKeyLoader;
import net.wurstclient.forge.utils.ChatUtils;
import net.wurstclient.forge.utils.KeyBindingUtils;
import net.wurstclient.forge.utils.ReflectionUtils;

public class GuiMoveHack extends Hack {
	public GuiMoveHack() {
		super("GuiMove", "Move with a gui open");
		setCategory(Category.PLAYER);
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
		
		if ( mc.currentScreen != null && !( mc.currentScreen instanceof GuiChat)) {
			KeyBinding[] key = {  mc.gameSettings.keyBindForward,  mc.gameSettings.keyBindBack,  mc.gameSettings.keyBindLeft,  mc.gameSettings.keyBindRight,  mc.gameSettings.keyBindJump };
			KeyBinding[] array;
			for (int length = (array = key).length, i = 0; i < length; ++i) {
				KeyBinding b = array[i];
				KeyBindingUtils.setPressed(b, b.isKeyDown());
				if(b.isKeyDown()) {
					ChatUtils.message(b.getDisplayName()+"ok");
				}
			}
			KeyBinding b = array[1];
			if(b.isKeyDown()) {
				ChatUtils.message(b.getDisplayName()+"ok");
			}

		} /*
			 * else if (mc.currentScreen == null) { for (KeyBinding bind : ) { if
			 * (!Keyboard.isKeyDown(bind.getKeyCode())) {
			 * KeyBinding.setKeyBindState(bind.getKeyCode(), false); } } }
			 */

	}

}
