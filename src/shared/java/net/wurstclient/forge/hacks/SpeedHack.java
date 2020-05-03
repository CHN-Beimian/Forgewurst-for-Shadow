package net.wurstclient.forge.hacks;

import java.lang.reflect.Field;

import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.network.play.client.CPacketAnimation;
import net.minecraft.util.Timer;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import net.wurstclient.fmlevents.WUpdateEvent;
import net.wurstclient.forge.Category;
import net.wurstclient.forge.Hack;
import net.wurstclient.forge.compatibility.WMinecraft;
import net.wurstclient.forge.settings.EnumSetting;
import net.wurstclient.forge.settings.SliderSetting;
import net.wurstclient.forge.settings.SliderSetting.ValueDisplay;
import net.wurstclient.forge.utils.BlockUtils;
import net.wurstclient.forge.utils.MoveUtils;
import net.wurstclient.forge.utils.PlayerControllerUtils;
import net.wurstclient.forge.utils.STimer;
import net.wurstclient.forge.utils.Wrapper;

public class SpeedHack extends Hack {
	private final EnumSetting<Mode> mode = new EnumSetting<SpeedHack.Mode>("Mode", Mode.values(), Mode.M1);
	private final SliderSetting SPEED = new SliderSetting("Speed", 0.2f, 0.2f, 2f, 0.01f, ValueDisplay.DECIMAL);
	private final SliderSetting TIMERSPEED = new SliderSetting("TimerSpeed", 2, 1, 20, 0.1, ValueDisplay.DECIMAL);
	
	//
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
	public SpeedHack() {
		super("Speed", "");
		setCategory(Category.COMBAT);
		addSetting(SPEED);
		addSetting(mode);
	}
	@Override
	public String getRenderName()
	{
	return getName()+" ["+mode.getSelected().toString()+"]";
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
		if (mode.getSelected() == Mode.NORMAL) {
			boolean boost = Math.abs(Wrapper.getPlayer().rotationYawHead - Wrapper.getPlayer().rotationYaw) < 90;
			if (Wrapper.getPlayer().moveForward > 0 && Wrapper.getPlayer().hurtTime < 5) {
				if (Wrapper.getPlayer().onGround) {
					Wrapper.getPlayer().jump();
					Wrapper.getPlayer().motionY = 0.405;
					float f = PlayerControllerUtils.getDirection();
					Wrapper.getPlayer().motionX -= (double) (MathHelper.sin(f) * SPEED.getValueF());
					Wrapper.getPlayer().motionZ += (double) (MathHelper.cos(f) * SPEED.getValueF());
				} else {
					double currentSpeed = Math.sqrt(Wrapper.getPlayer().motionX * Wrapper.getPlayer().motionX
							+ Wrapper.getPlayer().motionZ * Wrapper.getPlayer().motionZ);
					double speed = boost ? 1.0064 : 1.001;

					double direction = PlayerControllerUtils.getDirection();

					Wrapper.getPlayer().motionX = -Math.sin(direction) * speed * currentSpeed;
					Wrapper.getPlayer().motionZ = Math.cos(direction) * speed * currentSpeed;
				}
			} else if (mc.gameSettings.keyBindBack.isKeyDown() || mc.gameSettings.keyBindLeft.isKeyDown()
					|| mc.gameSettings.keyBindRight.isKeyDown()) {
				if (Wrapper.getPlayer().onGround) {
					Wrapper.getPlayer().jump();
					Wrapper.getPlayer().motionY = 0.405;
					float f = PlayerControllerUtils.getDirection();
					Wrapper.getPlayer().motionX -= (double) (MathHelper.sin(f) * SPEED.getValueF());
					Wrapper.getPlayer().motionZ += (double) (MathHelper.cos(f) * SPEED.getValueF());
				} else {
					double currentSpeed = Math.sqrt(Wrapper.getPlayer().motionX * Wrapper.getPlayer().motionX
							+ Wrapper.getPlayer().motionZ * Wrapper.getPlayer().motionZ);
					double speed = boost ? 1.0064 : 1.001;

					double direction = PlayerControllerUtils.getDirection();

					Wrapper.getPlayer().motionX = -Math.sin(direction) * speed * currentSpeed;
					Wrapper.getPlayer().motionZ = Math.cos(direction) * speed * currentSpeed;

				}
			}
		} else if (mode.getSelected() == Mode.M1) {
			// return if sneaking or not walking
			/*
			 * if(mc.player.isSneaking() || mc.player.moveForward == 0) return;
			 */
			if (Wrapper.getPlayer().moveForward > 0 && Wrapper.getPlayer().hurtTime < 5) {
				if (Wrapper.getPlayer().onGround) {
					mc.player.jump();
					// activate sprint if walking forward
					/*
					 * if(mc.player.moveForward > 0 && !mc.player.collidedHorizontally)
					 * mc.player.setSprinting(true);
					 */

					// activate mini jump if on ground

					double x = mc.player.motionX;
					double y = mc.player.motionY;
					double z = mc.player.motionZ;
					mc.player.jump();
					mc.player.setVelocity(x * 1.8, y, z * 1.8);

					double currentSpeed = Math.sqrt(Math.pow(x, 2) + Math.pow(z, 2));

					// limit speed to highest value that works on NoCheat+ version
					// 3.13.0-BETA-sMD5NET-b878
					// UPDATE: Patched in NoCheat+ version 3.13.2-SNAPSHOT-sMD5NET-b888
					double maxSpeed = 0.66F;

					if (currentSpeed > maxSpeed)
						mc.player.setVelocity(x / currentSpeed * maxSpeed, y, z / currentSpeed * maxSpeed);
				}else if (mc.gameSettings.keyBindBack.isKeyDown() || mc.gameSettings.keyBindLeft.isKeyDown()||mc.gameSettings.keyBindRight.isKeyDown()){
					if (Wrapper.getPlayer().onGround) {
					
					mc.player.jump();
					// activate sprint if walking forward
					/*
					 * if(mc.player.moveForward > 0 && !mc.player.collidedHorizontally)
					 * mc.player.setSprinting(true);
					 */

					// activate mini jump if on ground

					double x = mc.player.motionX;
					double y = mc.player.motionY;
					double z = mc.player.motionZ;
					mc.player.jump();
					mc.player.setVelocity(x * 1.8, y, z * 1.8);

					double currentSpeed = Math.sqrt(Math.pow(x, 2) + Math.pow(z, 2));

					// limit speed to highest value that works on NoCheat+ version
					// 3.13.0-BETA-sMD5NET-b878
					// UPDATE: Patched in NoCheat+ version 3.13.2-SNAPSHOT-sMD5NET-b888
					double maxSpeed = 0.66F;

					if (currentSpeed > maxSpeed)
						mc.player.setVelocity(x / currentSpeed * maxSpeed, y, z / currentSpeed * maxSpeed);
					}
				}
			}
		}else if(mode.getSelected()==Mode.Timer) {
			EntityPlayerSP player =event.getPlayer();
			if(player.onGround&&!player.isInWater()) {
				setTickLength(50);
				mc.player.jump();
			}else if(!player.isInWater()) {
				setTickLength(50 / TIMERSPEED.getValueF());
			}
		}else if(mode.getSelected()==Mode.Hypixel) {
			if (mc.player.collidedHorizontally)
            {
                collided = true;
            }

            if (collided)
            {
                Wrapper.setDefaultTimer();
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
		}else if(mode.getSelected()==Mode.AAC) {
			
				boolean boost = Math.abs(Wrapper.getPlayer().rotationYawHead - Wrapper.getPlayer().rotationYaw) < 90;
				if (Wrapper.getPlayer().moveForward > 0 && Wrapper.getPlayer().hurtTime < 5) {
					if (Wrapper.getPlayer().onGround) {
						Wrapper.getPlayer().jump();
						Wrapper.getPlayer().motionY = 0.405;
						float f = PlayerControllerUtils.getDirection();
						Wrapper.getPlayer().motionX -= (double) (MathHelper.sin(f) * 0);
						Wrapper.getPlayer().motionZ += (double) (MathHelper.cos(f) * 0);
					} else {
						double currentSpeed = Math.sqrt(Wrapper.getPlayer().motionX * Wrapper.getPlayer().motionX
								+ Wrapper.getPlayer().motionZ * Wrapper.getPlayer().motionZ);
						double speed = boost ? 1.0064 : 1.001;

						double direction = PlayerControllerUtils.getDirection();

						Wrapper.getPlayer().motionX = -Math.sin(direction) * speed * currentSpeed;
						Wrapper.getPlayer().motionZ = Math.cos(direction) * speed * currentSpeed;
					}
				} else if (mc.gameSettings.keyBindBack.isKeyDown() || mc.gameSettings.keyBindLeft.isKeyDown()
						|| mc.gameSettings.keyBindRight.isKeyDown()) {
					if (Wrapper.getPlayer().onGround) {
						Wrapper.getPlayer().jump();
						Wrapper.getPlayer().motionY = 0.405;
						float f = PlayerControllerUtils.getDirection();
						Wrapper.getPlayer().motionX -= (double) (MathHelper.sin(f) * SPEED.getValueF());
						Wrapper.getPlayer().motionZ += (double) (MathHelper.cos(f) * SPEED.getValueF());
					} else {
						double currentSpeed = Math.sqrt(Wrapper.getPlayer().motionX * Wrapper.getPlayer().motionX
								+ Wrapper.getPlayer().motionZ * Wrapper.getPlayer().motionZ);
						double speed = boost ? 1.0064 : 1.001;

						double direction = PlayerControllerUtils.getDirection();

						Wrapper.getPlayer().motionX = -Math.sin(direction) * speed * currentSpeed;
						Wrapper.getPlayer().motionZ = Math.cos(direction) * speed * currentSpeed;

					}
				}
			
		}
	}

	private enum Mode {
		NORMAL, M1,Timer,Hypixel,AAC
	}
	
	private void setTickLength(float tickLength)
	{
		try
		{
			Field fTimer = mc.getClass().getDeclaredField(
				wurst.isObfuscated() ? "field_71428_T" : "timer");
			fTimer.setAccessible(true);
			
			if(WMinecraft.VERSION.equals("1.10.2"))
			{
				Field fTimerSpeed = Timer.class.getDeclaredField(
					wurst.isObfuscated() ? "field_74278_d" : "timerSpeed");
				fTimerSpeed.setAccessible(true);
				fTimerSpeed.setFloat(fTimer.get(mc), 50 / tickLength);
				
			}else
			{
				Field fTickLength = Timer.class.getDeclaredField(
					wurst.isObfuscated() ? "field_194149_e" : "tickLength");
				fTickLength.setAccessible(true);
				fTickLength.setFloat(fTimer.get(mc), tickLength);
			}
			
		}catch(ReflectiveOperationException e)
		{
			throw new RuntimeException(e);
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
}
