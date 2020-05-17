package net.wurstclient.hothack.loader;

import org.lwjgl.input.Keyboard;

import net.minecraft.client.settings.KeyBinding;
import net.minecraftforge.fml.client.registry.ClientRegistry;

public class HackKeyLoader {
	public static KeyBinding fly;
	
	public HackKeyLoader() {
		fly=new KeyBinding("Shadow.fly", Keyboard.KEY_G, "Shadow");
		
		ClientRegistry.registerKeyBinding(fly);
	}
	
}
