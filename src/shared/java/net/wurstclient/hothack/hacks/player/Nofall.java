package net.wurstclient.hothack.hacks.player;

import org.lwjgl.input.Keyboard;

import net.minecraft.client.settings.KeyBinding;
import net.minecraft.network.play.client.CPacketPlayer;
import net.wurstclient.hothack.entry.Hack;

public class Nofall extends Hack{
	public Nofall() {
		super("Nofall","nofall");
		setKey(new KeyBinding("Shadow.Nofall", Keyboard.KEY_V, "Shadow"));
	}
	
	@Override
	public void onTicks() {
		if(mc.player.fallDistance > 2)
			mc.getConnection().sendPacket(new CPacketPlayer(true));
	}
}
