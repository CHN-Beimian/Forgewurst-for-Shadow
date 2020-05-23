package net.wurstclient.hothack.hacks.movement;

import java.lang.reflect.Field;

import org.lwjgl.input.Keyboard;

import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.util.Timer;
import net.minecraftforge.common.MinecraftForge;
import net.wurstclient.forge.compatibility.WMinecraft;
import net.wurstclient.forge.utils.Wrapper;
import net.wurstclient.hothack.entry.Hack;

public class Fly extends Hack {
	
	public static Fly fly;
	int ticks = 0;
	public Fly()
	  {
		super("Fly","fly");
	
	    setKey(new KeyBinding("Shadow.flight", Keyboard.KEY_0, "Shadow"));
	  }
	  
	/*
	 * public String getName() { return "������"; }
	 * 
	 * public String getDescription() { return "����������������������"; }
	 */
	@Override 
	public void onEnable() {
		setTickLength(50/5);
	}
	@Override
	public void onDisable() {
		setTickLength(50);
	}
	  @Override
	  public void onTicks()
	  {
		  setTickLength(50/5);
		  EntityPlayerSP player = Wrapper.getPlayer();
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
	  public static Fly instance() {
		  return fly;
	  }
	  private void setTickLength(float tickLength)
		{
			try
			{
				Field fTimer = mc.getClass().getDeclaredField( "field_71428_T");
				fTimer.setAccessible(true);
				
				if(WMinecraft.VERSION.equals("1.10.2"))
				{
					Field fTimerSpeed = Timer.class.getDeclaredField("field_74278_d" );
					fTimerSpeed.setAccessible(true);
					fTimerSpeed.setFloat(fTimer.get(mc), 50 / tickLength);
					
				}else
				{
					Field fTickLength = Timer.class.getDeclaredField(
					 "field_194149_e" );
					fTickLength.setAccessible(true);
					fTickLength.setFloat(fTimer.get(mc), tickLength);
				}
				
			}catch(ReflectiveOperationException e)
			{
				throw new RuntimeException(e);
			}
		}

}
