package net.wurstclient.forge.commands;

import net.minecraft.network.play.client.CPacketPlayer;
import net.minecraft.util.math.Vec3d;
import net.minecraftforge.fml.client.FMLClientHandler;
import net.wurstclient.forge.Command;
import net.wurstclient.forge.utils.MathUtils;

public final class DamageCmd extends Command
{
	public DamageCmd()
	{
		super("damage", "Applies the given amount of damage.",
			".damage <amount>", "Note: The amount is in half-hearts.",
			"Example: .damage 7 (applies 3.5 hearts)",
			"To apply more damage, run the command multiple times.");
	}
	
	@Override
	public void call(String[] args) throws CmdException
	{
		if(args.length == 0)
			throw new CmdSyntaxError();
		
		if(mc.player.capabilities.isCreativeMode)
			throw new CmdError("Cannot damage in creative mode.");
		
		int amount = parseAmount(args[0]);
		applyDamage(amount);
	}
	
	private int parseAmount(String dmgString) throws CmdSyntaxError
	{
		if(!MathUtils.isInteger(dmgString))
			throw new CmdSyntaxError("Not a number: " + dmgString);
		
		int dmg = Integer.parseInt(dmgString);
		
		if(dmg < 1)
			throw new CmdSyntaxError("Minimum amount is 1.");
		
		if(dmg > 7)
			throw new CmdSyntaxError("Maximum amount is 7.");
		
		return dmg;
	}
	
	private void applyDamage(int amount)
	{
		Vec3d pos = mc.player.getPositionVector();
		
		for(int i = 0; i < 80; i++)
		{
			sendPosition(pos.x, pos.y + amount + 2.1, pos.z, false);
			sendPosition(pos.x, pos.y + 0.05, pos.z, false);
		}
		
		sendPosition(pos.x, pos.y, pos.z, true);
	}
	
	private void sendPosition(double x, double y, double z, boolean onGround)
	{
		/*
		 * FMLClientHandler.instance().getClientPlayerEntity().sendPacket( new
		 * PlayerMoveC2SPacket.PositionOnly(x, y, z, onGround));
		 */
		mc.player.connection.sendPacket(new CPacketPlayer.PositionRotation(x, y, z, mc.player.rotationYaw,
				mc.player.rotationPitch, onGround));
	}
}