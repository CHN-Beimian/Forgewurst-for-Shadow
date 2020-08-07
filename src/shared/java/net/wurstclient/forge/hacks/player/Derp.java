package net.wurstclient.forge.hacks.player;

import java.util.Random;

import net.minecraft.entity.Entity;
import net.minecraft.network.play.client.CPacketAnimation;
import net.minecraft.network.play.client.CPacketEntityAction;
import net.minecraft.network.play.client.CPacketPlayer;
import net.minecraft.network.play.client.CPacketPlayerDigging;
import net.minecraft.network.play.client.CPacketVehicleMove;
import net.minecraft.network.status.client.CPacketPing;
import net.minecraft.util.math.MathHelper;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.client.FMLClientHandler;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.wurstclient.fmlevents.WUpdateEvent;
import net.wurstclient.forge.Category;
import net.wurstclient.forge.Hack;
import net.wurstclient.forge.settings.EnumSetting;
import net.wurstclient.forge.utils.system.Frame;

public class Derp extends Hack{
	private static final EnumSetting<Mode> mode=new EnumSetting<Derp.Mode>("Mode", Mode.values(), Mode.M1);
	private static final Random random = new Random();
	public Derp() {
		super("Derp","Randomly moves your head around");
		setCategory(Category.PLAYER);
		addSetting(mode);
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
		if(mode.getSelected()==Mode.M1) {
		float yaw = mc.player.rotationYawHead + random.nextFloat() * 360F - 180F;
		float pitch = random.nextFloat() * 180F - 90F;
		mc.player.connection.sendPacket(new CPacketPlayer.Rotation(yaw, pitch, true));
		}else if(mode.getSelected()==Mode.M2) {
			 float f1 = (new Random()).nextFloat() * 360.0F;
             float f2 = (new Random()).nextFloat() * 360.0F;
             mc.player.connection.sendPacket(new CPacketPlayer.Rotation(f1, f2, true));
			/*
			 * mc.player.rotationYaw=f1; mc.player.rotationPitch=f2;
			 */
		}else if(mode.getSelected()==Mode.M3) {
			 float f1 = 180;
             float f2 = (new Random()).nextFloat() * 450.0F;
             mc.player.connection.sendPacket(new CPacketPlayer.Rotation(f2, f1, true));
           
		}else if(mode.getSelected()==Mode.ROLLHEAD) {
			float timer = mc.player.timeInPortal % 20 / 10F;
			float pitch = MathHelper.sin(timer * (float)Math.PI) * 90F;
			mc.player.connection.sendPacket(new CPacketPlayer.Rotation(mc.player.rotationYaw, pitch, mc.player.onGround));
		
			
		}
	}
	private enum Mode{
		M1,M2,M3,ROLLHEAD
	}
}
