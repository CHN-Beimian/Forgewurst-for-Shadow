package net.wurstclient.hothack.hacks.movement;

import java.lang.reflect.Field;

import org.lwjgl.input.Keyboard;

import net.minecraft.client.settings.KeyBinding;
import net.minecraft.util.Timer;
import net.wurstclient.forge.compatibility.WMinecraft;
import net.wurstclient.forge.utils.BlockUtils;
import net.wurstclient.forge.utils.MoveUtils;
import net.wurstclient.forge.utils.PlayerControllerUtils;
import net.wurstclient.forge.utils.STimer;
import net.wurstclient.forge.utils.Wrapper;
import net.wurstclient.hothack.entry.Hack;

public class Speed extends Hack{
	 public boolean shouldslow = false;
	    double count = 0;
	    int jumps;
	    private float air, ground, aacSlow;
	    public static STimer timer = new STimer();
	    boolean collided = false, lessSlow;
	    int spoofSlot = 0;
	    double less, stair;
	    
	    private double speed, speedvalue;
	    private double lastDist;
	    public static int stage, aacCount;
	    STimer aac = new STimer();
	    STimer lastFall = new STimer();
	    STimer lastCheck = new STimer();
	public Speed() {
		super("Speed","speed");
		setKey(new KeyBinding("Shadow.Speed", Keyboard.KEY_Y, "Shadow"));
	}
	
	@Override
	public void onTicks() {
		if (mc.player.collidedHorizontally)
        {
            collided = true;
        }

        if (collided)
        {
            setTickLength(50);
            stage = -1;
        }

        if (stair > 0)
        {
            stair -= 0.25;
        }

        less -= less > 1 ? 0.12 : 0.11;

        if (less < 0)
        {
            less = 0;
        }

        if (!BlockUtils.isInLiquid() && MoveUtils.isOnGround(0.01) && (PlayerControllerUtils.isMoving2()))
        {
            collided = mc.player.collidedHorizontally;

            if (stage >= 0 || collided)
            {
                stage = 0;
                double motY = 0.407 + MoveUtils.getJumpEffect() * 0.1;

                if (stair == 0)
                {
                    mc.player.jump();
                   
                   mc.player.motionY = motY;
                }
                else
                {
                }

                less++;

                if (less > 1 && !lessSlow)
                {
                    lessSlow = true;
                }
                else
                {
                    lessSlow = false;
                }

                if (less > 1.12)
                {
                    less = 1.12;
                }
            }
        }

        speed = getHypixelSpeed(stage) + 0.0331;
        speed *= 0.91;

        if (stair > 0)
        {
            speed *= 0.7 - MoveUtils.getSpeedEffect() * 0.1;
        }

        if (stage < 0)
        {
            speed = MoveUtils.defaultSpeed();
        }

        if (lessSlow)
        {
            speed *= 0.95;
        }

        if (BlockUtils.isInLiquid())
        {
            speed = 0.55;
        }

        if ((mc.player.moveForward != 0.0f || mc.player.moveStrafing != 0.0f))
        {
            setMotion(speed);
            ++stage;
        }
	}
	 private double getHypixelSpeed(int stage)
	    {
	        double value = MoveUtils.defaultSpeed() + (0.028 * MoveUtils.getSpeedEffect()) + (double) MoveUtils.getSpeedEffect() / 15;
	        double firstvalue = 0.4145 + (double) MoveUtils.getSpeedEffect() / 12.5;
	        double decr = (((double) stage / 500) * 2);

	        if (stage == 0)
	        {
	            //JUMP
	            if (timer.delay(300))
	            {
	                timer.reset();
	                //mc.timer.timerSpeed = 1.354f;
	            }

	            if (!lastCheck.delay(500))
	            {
	                if (!shouldslow)
	                {
	                    shouldslow = true;
	                }
	            }
	            else
	            {
	                if (shouldslow)
	                {
	                    shouldslow = false;
	                }
	            }

	            value = 0.64 + (MoveUtils.getSpeedEffect() + (0.028 * MoveUtils.getSpeedEffect())) * 0.134;
	        }
	        else if (stage == 1)
	        {
			/*
			 * if (mc.timer.timerSpeed == 1.354f) { //mc.timer.timerSpeed = 1.254f; }
			 */

	            value = firstvalue;
	        }
	        else if (stage >= 2)
	        {
			/*
			 * if (mc.timer.timerSpeed == 1.254f) { //mc.timer.timerSpeed = 1f; }
			 */

	            value = firstvalue - decr;
	        }

	        if (shouldslow || !lastCheck.delay(500) || collided)
	        {
	            value = 0.2;

	            if (stage == 0)
	            {
	                value = 0;
	            }
	        }

	        return Math.max(value, shouldslow ? value : MoveUtils.defaultSpeed() + (0.028 * MoveUtils.getSpeedEffect()));
	    }
	 private void setMotion(double speed)
	    {
	        double forward = mc.player.movementInput.moveForward;
	        double strafe = mc.player.movementInput.moveStrafe;
	        float yaw = mc.player.rotationYaw;

	        if ((forward == 0.0D) && (strafe == 0.0D))
	        {
	        	mc.player.motionX=0;
	        	mc.player.motionZ=0;
	        }
	        else
	        {
	            if (forward != 0.0D)
	            {
	                if (strafe > 0.0D)
	                {
	                    yaw += (forward > 0.0D ? -45 : 45);
	                }
	                else if (strafe < 0.0D)
	                {
	                    yaw += (forward > 0.0D ? 45 : -45);
	                }

	                strafe = 0.0D;

	                if (forward > 0.0D)
	                {
	                    forward = 1;
	                }
	                else if (forward < 0.0D)
	                {
	                    forward = -1;
	                }
	            }
	            mc.player.motionX=forward * speed * Math.cos(Math.toRadians(yaw + 90.0F)) + strafe * speed * Math.sin(Math.toRadians(yaw + 90.0F));
	        	mc.player.motionZ=forward * speed * Math.sin(Math.toRadians(yaw + 90.0F)) - strafe * speed * Math.cos(Math.toRadians(yaw + 90.0F));
	            
	        }
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
