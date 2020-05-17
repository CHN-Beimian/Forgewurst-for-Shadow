package net.wurstclient.hothack.hacks.combat;

import org.lwjgl.input.Keyboard;

import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.client.settings.KeyBinding;
import net.wurstclient.forge.utils.Wrapper;
import net.wurstclient.hothack.entry.Hack;

public class KillAura extends Hack{
	public KillAura() {
		setKey(new KeyBinding("Shadow.killaura", Keyboard.KEY_B, "Shadow"));
	}
	@Override
	public void onTicks() {
		 EntityPlayerSP player = mc.player;
		 
		 
	}
}
