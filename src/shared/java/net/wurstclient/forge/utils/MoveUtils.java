package net.wurstclient.forge.utils;

import net.minecraft.block.Block;
import net.minecraft.block.BlockAir;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.MobEffects;
import net.minecraft.potion.Potion;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;

public class MoveUtils {
	private static Minecraft mc = Minecraft.getMinecraft();
    public static double defaultSpeed() {
        double baseSpeed = 0.2873D;
        if (Minecraft.getMinecraft().player.isPotionActive(MobEffects.SPEED)) {
            int amplifier = Minecraft.getMinecraft().player.getActivePotionEffect(MobEffects.SPEED).getAmplifier();
          //  if(((Options) settings.get(MODE).getValue()).getSelected().equalsIgnoreCase("Hypixel")){
           // 	baseSpeed *= (1.0D + 0.225D * (amplifier + 1));
           // }else
            	baseSpeed *= (1.0D + 0.2D * (amplifier + 1));
        }
        return baseSpeed;
    }
    public static void strafe(double speed) {
        float a = mc.player.rotationYaw * 0.017453292F;
        float l = mc.player.rotationYaw * 0.017453292F - (float) Math.PI * 1.5f;
        float r = mc.player.rotationYaw * 0.017453292F + (float) Math.PI * 1.5f;
        float rf = mc.player.rotationYaw * 0.017453292F + (float) Math.PI * 0.19f;
        float lf = mc.player.rotationYaw * 0.017453292F + (float) Math.PI * -0.19f;
        float lb = mc.player.rotationYaw * 0.017453292F - (float) Math.PI * 0.76f;
        float rb = mc.player.rotationYaw * 0.017453292F - (float) Math.PI * -0.76f;
        if (mc.gameSettings.keyBindForward.isPressed()) {
            if (mc.gameSettings.keyBindLeft.isPressed() && !mc.gameSettings.keyBindRight.isPressed()) {
                mc.player.motionX -= (double) (MathHelper.sin(lf) * speed);
                mc.player.motionZ += (double) (MathHelper.cos(lf) * speed);
            } else if (mc.gameSettings.keyBindRight.isPressed() && !mc.gameSettings.keyBindLeft.isPressed()) {
                mc.player.motionX -= (double) (MathHelper.sin(rf) * speed);
                mc.player.motionZ += (double) (MathHelper.cos(rf) * speed);
            } else {
                mc.player.motionX -= (double) (MathHelper.sin(a) * speed);
                mc.player.motionZ += (double) (MathHelper.cos(a) * speed);
            }
        } else if (mc.gameSettings.keyBindBack.isPressed()) {
            if (mc.gameSettings.keyBindLeft.isPressed() && !mc.gameSettings.keyBindRight.isPressed()) {
                mc.player.motionX -= (double) (MathHelper.sin(lb) * speed);
                mc.player.motionZ += (double) (MathHelper.cos(lb) * speed);
            } else if (mc.gameSettings.keyBindRight.isPressed() && !mc.gameSettings.keyBindLeft.isPressed()) {
                mc.player.motionX -= (double) (MathHelper.sin(rb) * speed);
                mc.player.motionZ += (double) (MathHelper.cos(rb) * speed);
            } else {
                mc.player.motionX += (double) (MathHelper.sin(a) * speed);
                mc.player.motionZ -= (double) (MathHelper.cos(a) * speed);
            }
        } else if (mc.gameSettings.keyBindLeft.isPressed() && !mc.gameSettings.keyBindRight.isPressed() && !mc.gameSettings.keyBindForward.isPressed() && !mc.gameSettings.keyBindBack.isPressed()) {
            mc.player.motionX += (double) (MathHelper.sin(l) * speed);
            mc.player.motionZ -= (double) (MathHelper.cos(l) * speed);
        } else if (mc.gameSettings.keyBindRight.isPressed() && !mc.gameSettings.keyBindLeft.isPressed() && !mc.gameSettings.keyBindForward.isPressed() && !mc.gameSettings.keyBindBack.isPressed()) {
            mc.player.motionX += (double) (MathHelper.sin(r) * speed);
            mc.player.motionZ -= (double) (MathHelper.cos(r) * speed);
        }

    }
    public static void setMotion(double speed) {
        double forward = mc.player.movementInput.moveForward;
        double strafe = mc.player.movementInput.moveStrafe;
        float yaw = mc.player.rotationYaw;
        if ((forward == 0.0D) && (strafe == 0.0D)) {
        	mc.player.motionX = 0;
        	mc.player.motionZ = 0;
        } else {
            if (forward != 0.0D) {
                if (strafe > 0.0D) {
                    yaw += (forward > 0.0D ? -45 : 45);
                } else if (strafe < 0.0D) {
                    yaw += (forward > 0.0D ? 45 : -45);
                }
                strafe = 0.0D;
                if (forward > 0.0D) {
                    forward = 1;
                } else if (forward < 0.0D) {
                    forward = -1;
                }
            }
            mc.player.motionX = forward * speed * Math.cos(Math.toRadians(yaw + 90.0F)) + strafe * speed * Math.sin(Math.toRadians(yaw + 90.0F)); 
            mc.player.motionZ = forward * speed * Math.sin(Math.toRadians(yaw + 90.0F)) - strafe * speed * Math.cos(Math.toRadians(yaw + 90.0F));
        }
    }   

    public static boolean checkTeleport(double x, double y, double z,double distBetweenPackets){
    	double distx = mc.player.posX - x;
    	double disty = mc.player.posY - y;
    	double distz = mc.player.posZ - z;
    	double dist = Math.sqrt(mc.player.getDistanceSq(x, y, z));
    	double distanceEntreLesPackets = distBetweenPackets;
    	double nbPackets = Math.round(dist / distanceEntreLesPackets + 0.49999999999) - 1;
    	
    	double xtp = mc.player.posX;
    	double ytp = mc.player.posY;
    	double ztp = mc.player.posZ;		
    		for (int i = 1; i < nbPackets;i++){		
    			double xdi = (x - mc.player.posX)/( nbPackets);	
    			 xtp += xdi;
    			 
    			double zdi = (z - mc.player.posZ)/( nbPackets);	
    			 ztp += zdi;
    			 
    			double ydi = (y - mc.player.posY)/( nbPackets);	
    			ytp += ydi;			
    	    	AxisAlignedBB bb = new AxisAlignedBB(xtp - 0.3, ytp, ztp - 0.3, xtp + 0.3, ytp + 1.8, ztp + 0.3);
    	    	if(!mc.world.getCollisionBoxes(mc.player, bb).isEmpty()){
    	    		return false;
    	    	}
    			
    		}
    	return true;
    }


    public static boolean isOnGround(double height) {
        if (!mc.world.getCollisionBoxes(mc.player, mc.player.getEntityBoundingBox().offset(0.0D, -height, 0.0D)).isEmpty()) {
            return true;
        } else {
            return false;
        }
    }
    public static int getJumpEffect() {
        if (mc.player.isPotionActive(MobEffects.JUMP_BOOST))
            return mc.player.getActivePotionEffect(MobEffects.JUMP_BOOST).getAmplifier() + 1;
        else
            return 0;
    }
    public static int getSpeedEffect() {
        if (mc.player.isPotionActive(MobEffects.SPEED))
            return mc.player.getActivePotionEffect(MobEffects.SPEED).getAmplifier() + 1;
        else
            return 0;
    }

    public static Block getBlockUnderPlayer(EntityPlayer inPlayer, double height) {
        return Minecraft.getMinecraft().world.getBlockState(new BlockPos(inPlayer.posX, inPlayer.posY - height, inPlayer.posZ)).getBlock();
    }

    public static Block getBlockAtPosC(double x, double y, double z) {
        EntityPlayer inPlayer = Minecraft.getMinecraft().player;
        return Minecraft.getMinecraft().world.getBlockState(new BlockPos(inPlayer.posX + x, inPlayer.posY + y, inPlayer.posZ + z)).getBlock();
    }

	/*
	 * public static float getDistanceToGround(Entity e) { if
	 * (mc.player.collidedVertically&& mc.player.onGround) { return 0.0F; } for
	 * (float a = (float) e.posY; a > 0.0F; a -= 1.0F) { int[] stairs = {53, 67,
	 * 108, 109, 114, 128, 134, 135, 136, 156, 163, 164, 180}; int[] exemptIds = {
	 * 6, 27, 28, 30, 31, 32, 37, 38, 39, 40, 50, 51, 55, 59, 63, 65, 66, 68, 69,
	 * 70, 72, 75, 76, 77, 83, 92, 93, 94, 104, 105, 106, 115, 119, 131, 132, 143,
	 * 147, 148, 149, 150, 157, 171, 175, 176, 177}; Block block =
	 * mc.world.getBlockState(new BlockPos(e.posX, a - 1.0F, e.posZ)).getBlock(); if
	 * (!(block instanceof BlockAir)) { if ((Block.getIdFromBlock(block) == 44) ||
	 * (Block.getIdFromBlock(block) == 126)) { return (float) (e.posY - a - 0.5D) <
	 * 0.0F ? 0.0F : (float) (e.posY - a - 0.5D); } int[] arrayOfInt1; int j =
	 * (arrayOfInt1 = stairs).length; for (int i = 0; i < j; i++) { int id =
	 * arrayOfInt1[i]; if (Block.getIdFromBlock(block) == id) { return (float)
	 * (e.posY - a - 1.0D) < 0.0F ? 0.0F : (float) (e.posY - a - 1.0D); } } j =
	 * (arrayOfInt1 = exemptIds).length; for (int i = 0; i < j; i++) { int id =
	 * arrayOfInt1[i]; if (Block.getIdFromBlock(block) == id) { return (float)
	 * (e.posY - a) < 0.0F ? 0.0F : (float) (e.posY - a); } } return (float) (e.posY
	 * - a + block.getbo getBlockBoundsMaxY() - 1.0D); } } return 0.0F; }
	 */
    
    
    public static float[] getRotationsBlock(BlockPos block, EnumFacing face) {
        double x = block.getX() + 0.5 - mc.player.posX +  (double) face.getFrontOffsetX()/2;
        double z = block.getZ() + 0.5 - mc.player.posZ +  (double) face.getFrontOffsetZ()/2;
        double y = (block.getY() + 0.5);
        double d1 = mc.player.posY + mc.player.getEyeHeight() - y;
        double d3 = MathHelper.sqrt(x * x + z * z);
        float yaw = (float) (Math.atan2(z, x) * 180.0D / Math.PI) - 90.0F;
        float pitch = (float) (Math.atan2(d1, d3) * 180.0D / Math.PI);
        if (yaw < 0.0F) {
            yaw += 360f;
        }
        return new float[]{yaw, pitch};
    }
    public static boolean isBlockAboveHead(){
    	AxisAlignedBB bb =new AxisAlignedBB(mc.player.posX - 0.3, mc.player.posY+mc.player.getEyeHeight(), mc.player.posZ + 0.3,
 											mc.player.posX + 0.3, mc.player.posY+2.5 ,mc.player.posZ - 0.3);
 	  return !mc.world.getCollisionBoxes(mc.player, bb).isEmpty();
    }
    public static boolean isCollidedH(double dist){
    	AxisAlignedBB bb =new AxisAlignedBB(mc.player.posX - 0.3, mc.player.posY+2, mc.player.posZ + 0.3,
											mc.player.posX + 0.3, mc.player.posY+3 ,mc.player.posZ - 0.3);
    	if (!mc.world.getCollisionBoxes(mc.player, bb.offset(0.3 + dist, 0, 0)).isEmpty()) {
    		return true;
    	}else if (!mc.world.getCollisionBoxes(mc.player, bb.offset(-0.3 - dist, 0, 0)).isEmpty()) {
    		return true;
    	}else if (!mc.world.getCollisionBoxes(mc.player, bb.offset(0, 0, 0.3 + dist)).isEmpty()) {
    		return true;
    	}else if (!mc.world.getCollisionBoxes(mc.player, bb.offset(0, 0, -0.3 - dist)).isEmpty()) {
    		return true;
	 	}
    	return false;
    }
    public static boolean isRealCollidedH(double dist){
		AxisAlignedBB bb =new AxisAlignedBB(mc.player.posX - 0.3, mc.player.posY + 0.5, mc.player.posZ + 0.3,
											mc.player.posX + 0.3, mc.player.posY+1.9 ,mc.player.posZ - 0.3);
		if (!mc.world.getCollisionBoxes(mc.player, bb.offset(0.3 + dist, 0, 0)).isEmpty()) {
		  	return true;
	  	}else if (!mc.world.getCollisionBoxes(mc.player, bb.offset(-0.3 - dist, 0, 0)).isEmpty()) {
		  	return true;
	  	}else if (!mc.world.getCollisionBoxes(mc.player, bb.offset(0, 0, 0.3 + dist)).isEmpty()) {
		  return true;
	  	}else if (!mc.world.getCollisionBoxes(mc.player, bb.offset(0, 0, -0.3 - dist)).isEmpty()) {
		  	return true;
	  	}
	  return false;
    }
    public static boolean isMoving() {
        return mc.player != null && (mc.player.movementInput.moveForward != 0F || mc.player.movementInput.moveStrafe != 0F);
    }
    public static void strafeHYT(final float speed) {
        if(!isMoving())
            return;

        final double yaw = getDirection();
        mc.player.motionX = -Math.sin(yaw) * speed;
        mc.player.motionZ = Math.cos(yaw) * speed;
    }
    public static double getDirection() {
        float rotationYaw = mc.player.rotationYaw;

        if(mc.player.moveForward < 0F)
            rotationYaw += 180F;

        float forward = 1F;
        if(mc.player.moveForward < 0F)
            forward = -0.5F;
        else if(mc.player.moveForward > 0F)
            forward = 0.5F;

        if(mc.player.moveStrafing > 0F)
            rotationYaw -= 90F * forward;

        if(mc.player.moveStrafing < 0F)
            rotationYaw += 90F * forward;

        return Math.toRadians(rotationYaw);
    }
}


