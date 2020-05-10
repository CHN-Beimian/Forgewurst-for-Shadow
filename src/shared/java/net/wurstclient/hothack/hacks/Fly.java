package net.wurstclient.hothack.hacks;

import java.lang.reflect.Field;

import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.util.Timer;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent.PlayerTickEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.wurstclient.forge.ForgeWurst;
import net.wurstclient.forge.compatibility.WMinecraft;
import net.wurstclient.forge.utils.Wrapper;
import net.wurstclient.hothack.Hack;
import net.wurstclient.hothack.HotHack;

public class Fly extends Hack{
	private static final ForgeWurst wurst = ForgeWurst.getForgeWurst();
	int ticks = 0;
	@Override
	public void onEnable() {
		MinecraftForge.EVENT_BUS.register(this);
	}

	@Override
	public void onDisable() {
		MinecraftForge.EVENT_BUS.unregister(this);
		
	}

@SubscribeEvent
	public void onUpdate(TickEvent.PlayerTickEvent event) {
		EntityPlayerSP player = mc.player;
		if(player==null)
		return;
		
		player.motionY = 0.0;
		player.setSprinting(true);
		player.onGround = true;
		ticks++;
		if (ticks == 2 || ticks == 4 || ticks == 6 || ticks == 8 || ticks == 10 || ticks == 12 || ticks == 14
				|| ticks == 16 || ticks == 18 || ticks == 20) {
			player.setPosition(player.posX, player.posY + 0.00000000128, player.posZ);
		}
		if (ticks == 20) {
			ticks = 0;
		}
	}
	private void setTickLength(float tickLength)
	{
		try
		{
			
			Field fTimer = Minecraft.getMinecraft().getClass().getDeclaredField(
				wurst.isObfuscated() ? "field_71428_T" : "timer");
			fTimer.setAccessible(true);
			
			if(WMinecraft.VERSION.equals("1.10.2"))
			{
				Field fTimerSpeed = Timer.class.getDeclaredField(
					wurst.isObfuscated() ? "field_74278_d" : "timerSpeed");
				fTimerSpeed.setAccessible(true);
				fTimerSpeed.setFloat(fTimer.get(Minecraft.getMinecraft()), 50 / tickLength);
				
			}else
			{
				Field fTickLength = Timer.class.getDeclaredField(
					wurst.isObfuscated() ? "field_194149_e" : "tickLength");
				fTickLength.setAccessible(true);
				fTickLength.setFloat(fTimer.get(Minecraft.getMinecraft()), tickLength);
			}
			
		}catch(ReflectiveOperationException e)
		{
			throw new RuntimeException(e);
		}
	}
	
}
