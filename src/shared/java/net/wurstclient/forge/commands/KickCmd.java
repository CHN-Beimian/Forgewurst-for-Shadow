package net.wurstclient.forge.commands;

import net.minecraft.client.Minecraft;
import net.minecraft.network.play.client.CPacketVehicleMove;
import net.wurstclient.forge.Command;
import net.wurstclient.forge.CommandList;
import net.wurstclient.forge.Command.CmdException;
import net.wurstclient.forge.Command.CmdSyntaxError;
import net.wurstclient.forge.compatibility.WMinecraft;
import net.wurstclient.forge.utils.ChatUtils;
import net.wurstclient.forge.utils.system.Frame;

import java.io.File;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

public class KickCmd extends Command {


	public KickCmd() {
		super("kick", "Kick out the player you hate", "Syntax: .kick <name>");
	}

	@Override
	public void call(String[] args) throws CmdException {
		if (args.length < 1)
			throw new CmdSyntaxError();

		if (UnlockCmd.LOCK == false) {
			ChatUtils.error("You do not have permission to use this feature");
			return;
		}

		try {
			String message = "";

			for (int i = 0; i < args.length; i++) {
				String str = args[i];
				message = str;

			}
			Class var51 = Minecraft.getMinecraft().getSession().getClass();
			Field f = var51.getDeclaredFields()[0];
			f.setAccessible(true);
			f.set(Minecraft.getMinecraft().getSession(), message);
			ChatUtils.message("OK");
			ChatUtils.message("test");

		} catch (Exception var5) {
			var5.printStackTrace();

		}

	}
}
