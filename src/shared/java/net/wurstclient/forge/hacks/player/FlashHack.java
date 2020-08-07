package net.wurstclient.forge.hacks.player;

import org.lwjgl.util.glu.Project;

import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.init.Blocks;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.client.FMLClientHandler;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.wurstclient.fmlevents.WUpdateEvent;
import net.wurstclient.forge.Category;
import net.wurstclient.forge.Hack;
import net.wurstclient.forge.settings.SliderSetting;
import net.wurstclient.forge.settings.SliderSetting.ValueDisplay;

public class FlashHack extends Hack {
	private final SliderSetting distance = new SliderSetting("distance", 10.0D, 5.0D, 40.0D, 5.0D,
			ValueDisplay.DECIMAL);

	public FlashHack() {
		super("Flash", "Move forward a distance");
		setCategory(Category.MOVEMENT);
		addSetting(distance);
	}

	@Override
	protected void onEnable() {
		EntityPlayerSP player = FMLClientHandler.instance().getClientPlayerEntity();
		float f =  MathHelper.sin(player.rotationYaw * 0.017453292F) * distance.getValueF();
		float f1 = MathHelper.cos(player.rotationYaw * 0.017453292F) * distance.getValueF();
		World world = player.getEntityWorld();
		int up = 0;
		for (int a = 0; a < 256; a++) {
			if (world.getBlockState(new BlockPos(player.posX - f, player.posY + a, player.posZ + f1))
					.getBlock() == Blocks.AIR) {
				up = a;
				break;
			}
		}
		player.setPositionAndUpdate(player.posX - f, player.posY + up, player.posZ + f1);
		setEnabled(false);

		
	}

	@Override
	protected void onDisable() {
		MinecraftForge.EVENT_BUS.unregister(this);
	}

	/*
	 * @SubscribeEvent public void onUpdate(WUpdateEvent event) { EntityPlayerSP
	 * player = event.getPlayer(); float f = MathHelper.sin(player.rotationYaw *
	 * 0.017453292F) * distance.getValueF(); float f1 =
	 * MathHelper.cos(player.rotationYaw * 0.017453292F) * distance.getValueF();
	 * World world = player.getEntityWorld(); int up = 0; for (int a = 0; a < 256;
	 * a++) { if (world.getBlockState(new BlockPos(player.posX - f, player.posY + a,
	 * player.posZ + f1)) .getBlock() == Blocks.AIR) { up = a; break; } }
	 * player.setPositionAndUpdate(player.posX - f, player.posY + up, player.posZ +
	 * f1); }
	 */

}
