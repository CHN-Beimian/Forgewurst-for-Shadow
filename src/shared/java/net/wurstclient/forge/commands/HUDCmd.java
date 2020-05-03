package net.wurstclient.forge.commands;

import net.minecraftforge.common.MinecraftForge;
import net.wurstclient.forge.Command;
import net.wurstclient.forge.Command.CmdException;
import net.wurstclient.forge.Command.CmdSyntaxError;

public class HUDCmd extends Command{
	private boolean enabled;
	public HUDCmd()
	{
		super("hud", "close or open your hud.", "Syntax: .hud");
	}
	
	@Override
	public void call(String[] args) throws CmdException
	{
		if(args.length > 0)
			throw new CmdSyntaxError();
		
		enabled = !enabled;
		
		if(enabled)
			MinecraftForge.EVENT_BUS.unregister(wurst.getForgeWurst().getHud());
		else
			MinecraftForge.EVENT_BUS.register(wurst.getForgeWurst().getHud());
	}
}
