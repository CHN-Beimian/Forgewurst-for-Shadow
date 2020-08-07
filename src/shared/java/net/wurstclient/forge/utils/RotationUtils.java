/*
 * Copyright (C) 2017 - 2019 | Wurst-Imperium | All rights reserved.
 *
 * This source code is subject to the terms of the GNU General Public
 * License, version 3. If a copy of the GPL was not distributed with this
 * file, You can obtain one at: https://www.gnu.org/licenses/gpl-3.0.txt
 */
package net.wurstclient.forge.utils;

import java.lang.reflect.Field;
import java.util.List;

import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.network.play.client.CPacketPlayer;
import net.minecraft.network.play.client.CPacketPlayer.Rotation;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.wurstclient.fmlevents.WPostMotionEvent;
import net.wurstclient.fmlevents.WPreMotionEvent;
import net.wurstclient.forge.ForgeWurst;
import net.wurstclient.forge.compatibility.WMinecraft;
import net.wurstclient.forge.compatibility.WVec3d;
import net.wurstclient.forge.hacks.combat.KillAuraHack;

@Mod.EventBusSubscriber
public final class RotationUtils {
	private static final Minecraft mc = Minecraft.getMinecraft();
	private static boolean fakeRotation;
	private static float serverYaw;
	private static float serverPitch;
	private static float realYaw;
	private static float realPitch;
	public static Rotation serverRotation = new Rotation(0F, 0F, true);

	@SubscribeEvent
	public static void onPreMotion(WPreMotionEvent event) {
		if (!fakeRotation)
			return;

		EntityPlayer player = event.getPlayer();
		realYaw = player.rotationYaw;
		realPitch = player.rotationPitch;
		player.rotationYaw = serverYaw;
		player.rotationPitch = serverPitch;
	}

	@SubscribeEvent
	public static void onPostMotion(WPostMotionEvent event) {
		if (!fakeRotation)
			return;

		EntityPlayer player = event.getPlayer();
		player.rotationYaw = realYaw;
		player.rotationPitch = realPitch;
		fakeRotation = false;
	}

	public static Vec3d getEyesPos() {
		return new Vec3d(WMinecraft.getPlayer().posX,
				WMinecraft.getPlayer().posY + WMinecraft.getPlayer().getEyeHeight(), WMinecraft.getPlayer().posZ);
	}

	public static Vec3d getClientLookVec() {
		EntityPlayerSP player = WMinecraft.getPlayer();

		float f = MathHelper.cos(-player.rotationYaw * 0.017453292F - (float) Math.PI);
		float f1 = MathHelper.sin(-player.rotationYaw * 0.017453292F - (float) Math.PI);

		float f2 = -MathHelper.cos(-player.rotationPitch * 0.017453292F);
		float f3 = MathHelper.sin(-player.rotationPitch * 0.017453292F);

		return new Vec3d(f1 * f2, f3, f * f2);
	}

	private static float[] getNeededRotations(Vec3d vec) {
		Vec3d eyesPos = getEyesPos();

		double diffX = WVec3d.getX(vec) - WVec3d.getX(eyesPos);
		double diffY = WVec3d.getY(vec) - WVec3d.getY(eyesPos);
		double diffZ = WVec3d.getZ(vec) - WVec3d.getZ(eyesPos);

		double diffXZ = Math.sqrt(diffX * diffX + diffZ * diffZ);

		float yaw = (float) Math.toDegrees(Math.atan2(diffZ, diffX)) - 90F;
		float pitch = (float) -Math.toDegrees(Math.atan2(diffY, diffXZ));

		return new float[] { MathHelper.wrapDegrees(yaw), MathHelper.wrapDegrees(pitch) };
	}

	public static double getAngleToLookVec(Vec3d vec) {
		float[] needed = getNeededRotations(vec);

		EntityPlayerSP player = WMinecraft.getPlayer();
		float currentYaw = MathHelper.wrapDegrees(player.rotationYaw);
		float currentPitch = MathHelper.wrapDegrees(player.rotationPitch);

		float diffYaw = currentYaw - needed[0];
		float diffPitch = currentPitch - needed[1];

		return Math.sqrt(diffYaw * diffYaw + diffPitch * diffPitch);
	}

	public static double getAngleToLastReportedLookVec(Vec3d vec) {
		float[] needed = getNeededRotations(vec);

		EntityPlayerSP player = WMinecraft.getPlayer();
		float lastReportedYaw;
		float lastReportedPitch;
		try {
			Field yawField = EntityPlayerSP.class.getDeclaredField(
					ForgeWurst.getForgeWurst().isObfuscated() ? "field_175164_bL" : "lastReportedYaw");
			yawField.setAccessible(true);
			lastReportedYaw = MathHelper.wrapDegrees(yawField.getFloat(player));

			Field pitchField = EntityPlayerSP.class.getDeclaredField(
					ForgeWurst.getForgeWurst().isObfuscated() ? "field_175165_bM" : "lastReportedPitch");
			pitchField.setAccessible(true);
			lastReportedPitch = MathHelper.wrapDegrees(pitchField.getFloat(player));

		} catch (ReflectiveOperationException e) {
			throw new RuntimeException(e);
		}

		float diffYaw = lastReportedYaw - needed[0];
		float diffPitch = lastReportedPitch - needed[1];

		return Math.sqrt(diffYaw * diffYaw + diffPitch * diffPitch);
	}

	public static boolean faceVectorPacket(Vec3d vec) {
		float[] rotations = getNeededRotations(vec);

		fakeRotation = true;
		serverYaw = rotations[0];
		serverPitch = rotations[1];

		return Math.abs(serverYaw - rotations[0]) < 1F;
	}

	public static void faceVectorForWalking(Vec3d vec) {
		float[] needed = getNeededRotations(vec);

		EntityPlayerSP player = WMinecraft.getPlayer();

		player.connection.sendPacket(new CPacketPlayer.Rotation(needed[0], needed[1], true));

		player.rotationYaw = needed[0];
		player.rotationPitch = needed[1];

	}

	public static void faceVectorP(Vec3d vec) {
		float[] needed = getNeededRotations(vec);

		EntityPlayerSP player = WMinecraft.getPlayer();

		player.connection.sendPacket(new CPacketPlayer.Rotation(needed[0], needed[1], mc.player.onGround));
	}
	public static void faceVectorC(Vec3d vec) {
		float[] needed = getNeededRotations(vec);
		EntityPlayerSP player = WMinecraft.getPlayer();
		player.rotationYaw = needed[0];
		player.rotationPitch = needed[1];
	}

	public static boolean canEntityBeSeen(Entity e) {
		Minecraft mc = Minecraft.getMinecraft();
		Vec3d vec1 = new Vec3d(mc.player.posX, mc.player.posY + mc.player.getEyeHeight(), mc.player.posZ);
		AxisAlignedBB box = e.getEntityBoundingBox();
		Vec3d vec2 = new Vec3d(e.posX, e.posY + (e.getEyeHeight() / 1.32F), e.posZ);
		double minx = e.posX - 0.25;
		double maxx = e.posX + 0.25;
		double miny = e.posY;
		double maxy = e.posY + Math.abs(e.posY - box.maxY);
		double minz = e.posZ - 0.25;
		double maxz = e.posZ + 0.25;
		boolean see = mc.world.rayTraceBlocks(vec1, vec2) == null ? true : false;

		if (see) {
			return true;
		}

		vec2 = new Vec3d(maxx, miny, minz);
		see = mc.world.rayTraceBlocks(vec1, vec2) == null ? true : false;

		if (see) {
			return true;
		}

		vec2 = new Vec3d(minx, miny, minz);
		see = mc.world.rayTraceBlocks(vec1, vec2) == null ? true : false;

		if (see) {
			return true;
		}

		vec2 = new Vec3d(minx, miny, maxz);
		see = mc.world.rayTraceBlocks(vec1, vec2) == null ? true : false;

		if (see) {
			return true;
		}

		vec2 = new Vec3d(maxx, miny, maxz);
		see = mc.world.rayTraceBlocks(vec1, vec2) == null ? true : false;

		if (see) {
			return true;
		}

		vec2 = new Vec3d(maxx, maxy, minz);
		see = mc.world.rayTraceBlocks(vec1, vec2) == null ? true : false;

		if (see) {
			return true;
		}

		vec2 = new Vec3d(minx, maxy, minz);
		see = mc.world.rayTraceBlocks(vec1, vec2) == null ? true : false;

		if (see) {
			return true;
		}

		vec2 = new Vec3d(minx, maxy, maxz - 0.1);
		see = mc.world.rayTraceBlocks(vec1, vec2) == null ? true : false;

		if (see) {
			return true;
		}

		vec2 = new Vec3d(maxx, maxy, maxz);
		see = mc.world.rayTraceBlocks(vec1, vec2) == null ? true : false;

		if (see) {
			return true;
		}

		return false;
	}

	public static float[] getRotations(EntityLivingBase ent) {
		double x = ent.posX;
		double z = ent.posZ;
		double y = ent.posY + ent.getEyeHeight() / 2.0F;
		return getRotationFromPosition(x, z, y);
	}

	public static float[] getPredictedRotations(EntityLivingBase ent) {
		double x = ent.posX + (ent.posX - ent.lastTickPosX);
		double z = ent.posZ + (ent.posZ - ent.lastTickPosZ);
		double y = ent.posY + ent.getEyeHeight() / 2.0F;
		return getRotationFromPosition(x, z, y);
	}

	public static float[] getAverageRotations(List<EntityLivingBase> targetList) {
		double posX = 0.0D;
		double posY = 0.0D;
		double posZ = 0.0D;

		for (Entity ent : targetList) {
			posX += ent.posX;
			posY += ent.getEntityBoundingBox().maxY - 2.0D;
			posZ += ent.posZ;
		}

		posX /= targetList.size();
		posY /= targetList.size();
		posZ /= targetList.size();
		return new float[] { getRotationFromPosition(posX, posZ, posY)[0],
				getRotationFromPosition(posX, posZ, posY)[1] };
	}

	public static float[] getRotationFromPosition(double x, double z, double y) {
		double xDiff = x - Minecraft.getMinecraft().player.posX;
		double zDiff = z - Minecraft.getMinecraft().player.posZ;
		double yDiff = y - Minecraft.getMinecraft().player.posY - 1.2;
		double dist = MathHelper.sqrt(xDiff * xDiff + zDiff * zDiff);
		float yaw = (float) (Math.atan2(zDiff, xDiff) * 180.0D / Math.PI) - 90.0F;
		float pitch = (float) -(Math.atan2(yDiff, dist) * 180.0D / Math.PI);
		return new float[] { yaw, pitch };
	}

	public static float getDistanceBetweenAngles(float angle1, float angle2) {
		float angle = Math.abs(angle1 - angle2) % 360.0F;

		if (angle > 180.0F) {
			angle = 360.0F - angle;
		}

		return angle;
	}

	public static float getYawChange(float yaw, double posX, double posZ) {
		double deltaX = posX - Minecraft.getMinecraft().player.posX;
		double deltaZ = posZ - Minecraft.getMinecraft().player.posZ;
		double yawToEntity = 0;

		if ((deltaZ < 0.0D) && (deltaX < 0.0D)) {
			if (deltaX != 0) {
				yawToEntity = 90.0D + Math.toDegrees(Math.atan(deltaZ / deltaX));
			}
		} else if ((deltaZ < 0.0D) && (deltaX > 0.0D)) {
			if (deltaX != 0) {
				yawToEntity = -90.0D + Math.toDegrees(Math.atan(deltaZ / deltaX));
			}
		} else {
			if (deltaZ != 0) {
				yawToEntity = Math.toDegrees(-Math.atan(deltaX / deltaZ));
			}
		}

		return MathUtils.wrapAngleTo180_float(-(yaw - (float) yawToEntity));
	}

	public static float getPitchChange(float pitch, Entity entity, double posY) {
		double deltaX = entity.posX - Minecraft.getMinecraft().player.posX;
		double deltaZ = entity.posZ - Minecraft.getMinecraft().player.posZ;
		double deltaY = posY - 2.2D + entity.getEyeHeight() - Minecraft.getMinecraft().player.posY;
		double distanceXZ = MathHelper.sqrt(deltaX * deltaX + deltaZ * deltaZ);
		double pitchToEntity = -Math.toDegrees(Math.atan(deltaY / distanceXZ));
		return -MathUtils.wrapAngleTo180_float(pitch - (float) pitchToEntity) - 2.5F;
	}

	public float[] getCustomRotsChange(float yaw, float pitch, double x, double y, double z) {
		double xDiff = x - mc.player.posX;
		double zDiff = z - mc.player.posZ;
		double yDiff = y - mc.player.posY;
		double dist = (double) MathHelper.sqrt(xDiff * xDiff + zDiff * zDiff);
		double mult = 1.0D / (dist + 1.0E-4D) * 2.0D;

		if (mult > 0.2D) {
			mult = 0.2D;
		}

		/*
		 * if (!mc.world.getEntitiesWithinAABBExcludingEntity(mc.player,
		 * mc.player.getEntityBoundingBox()).contains(target)) { x += 0.3D *
		 * (double)this.randoms[0]; y -= 0.4D + mult * (double)this.randoms[1]; z +=
		 * 0.3D * (double)this.randoms[2]; }
		 */

		xDiff = x - mc.player.posX;
		zDiff = z - mc.player.posZ;
		yDiff = y - mc.player.posY;
		float yawToEntity = (float) (Math.atan2(zDiff, xDiff) * 180.0D / Math.PI) - 90.0F;
		float pitchToEntity = (float) (-(Math.atan2(yDiff, dist) * 180.0D / Math.PI));
		return new float[] { MathUtils.wrapAngleTo180_float(-(yaw - yawToEntity)),
				-MathUtils.wrapAngleTo180_float(pitch - pitchToEntity) - 2.5F };
	}

	// liquidbounce
	public static Vec3d getVectorForRotation(final Rotation rotation) {
		float yawCos = MathHelper.cos(-rotation.getYaw(mc.player.rotationYaw) * 0.017453292F - (float) Math.PI);
		float yawSin = MathHelper.sin(-rotation.getYaw(mc.player.rotationYaw) * 0.017453292F - (float) Math.PI);
		float pitchCos = -MathHelper.cos(-rotation.getPitch(mc.player.rotationPitch) * 0.017453292F);
		float pitchSin = MathHelper.sin(-rotation.getPitch(mc.player.rotationPitch) * 0.017453292F);
		return new Vec3d(yawSin * pitchCos, pitchSin, yawCos * pitchCos);
	}
}
