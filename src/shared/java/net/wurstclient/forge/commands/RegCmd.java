package net.wurstclient.forge.commands;

import net.wurstclient.forge.Command;
import net.wurstclient.forge.Command.CmdException;
import net.wurstclient.forge.Command.CmdSyntaxError;
import net.wurstclient.forge.compatibility.WMinecraft;

public class RegCmd extends Command{
	public RegCmd() {
		super("reg","login some servers","\"Syntax: .reg <password1> <password2>\"");
	}
	@Override
	public void call(String[] args) throws CmdException
	{
		if(args.length < 1)
			throw new CmdSyntaxError();
		String message= ".reg " + String.join(" ", args)+String.join(" ", args);
		String message1=".reg "+"args "+"args";
		WMinecraft.getPlayer().sendChatMessage(message1);
		System.out.println(message1);
		
	}
}
