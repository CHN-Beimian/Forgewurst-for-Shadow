package net.wurstclient.hothack.init;

import org.lwjgl.input.Keyboard;

import net.minecraft.client.settings.KeyBinding;
import net.minecraftforge.fml.client.registry.ClientRegistry;

public class HotHackKey {
	private static  HotHackKey hotkey;
	
	
	public static KeyBinding fly;

	public HotHackKey() {
		fly = new KeyBinding("Shadow.fly", Keyboard.KEY_G, "Shadow");

		
		ClientRegistry.registerKeyBinding(fly);
	}
	public static HotHackKey instance() {
		return hotkey;
	}
	
	
}
