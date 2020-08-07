package net.wurstclient.forge.hacks.player;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.block.Block;
import net.minecraft.block.BlockBed;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.network.play.client.CPacketPlayer;
import net.minecraft.network.play.client.CPacketPlayerDigging;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.Vec3d;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.wurstclient.fmlevents.WUpdateEvent;
import net.wurstclient.forge.Category;
import net.wurstclient.forge.Hack;
import net.wurstclient.forge.utils.BlockUtils;
import net.wurstclient.forge.utils.STimer;

public class BedFuckerHack extends Hack {
	public static BlockPos blockBreaking;
	STimer timer = new STimer();
	List<BlockPos> beds = new ArrayList<>();

	public BedFuckerHack() {
		super("BedFucker", "Destroy the bed across the block");
		setCategory(Category.PLAYER);
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
		int reach = 6;

		for (int y = reach; y >= -reach; --y) {
			for (int x = -reach; x <= reach; ++x) {
				for (int z = -reach; z <= reach; ++z) {
					if (mc.player.isSneaking()) {
						return;
					}

					BlockPos pos = new BlockPos(mc.player.posX + x, mc.player.posY + y, mc.player.posZ + z);

					if (getFacingDirection(pos) != null && blockChecks(mc.world.getBlockState(pos).getBlock())
							&& mc.player.getDistance(mc.player.posX + x, mc.player.posY + y,
									mc.player.posZ + z) < mc.playerController.getBlockReachDistance() - 0.2) {
						if (!beds.contains(pos)) {
							beds.add(pos);
						}
					}
				}
			}
		}

		BlockPos closest = null;
		if (!beds.isEmpty())
			for (int i = 0; i < beds.size(); i++) {
				BlockPos bed = beds.get(i);

				if (mc.player.getDistance(bed.getX(), bed.getY(),
						bed.getZ()) > mc.playerController.getBlockReachDistance() - 0.2
						|| mc.world.getBlockState(bed).getBlock() != Blocks.BED) {
					beds.remove(i);
				}

				if (closest == null || mc.player.getDistance(bed.getX(), bed.getY(), bed.getZ()) < mc.player
						.getDistance(closest.getX(), closest.getY(), closest.getZ())) {
					closest = bed;
				}
			}
		if (closest != null) {
			float[] rot = getRotations(closest, getClosestEnum(closest));
			/*
			 * mc.player.cameraYaw=rot[0]; mc.player.cameraPitch=rot[1];
			 */
		
			/*
			 * mc.player.rotationYaw = rot[0]; mc.player.rotationPitch = rot[1];
			 * blockBreaking = closest;
			 */
			EnumFacing direction =getClosestEnum(closest);
			mc.player.connection.sendPacket(new CPacketPlayerDigging(CPacketPlayerDigging.Action.START_DESTROY_BLOCK, closest, direction));
			mc.player.connection.sendPacket(new CPacketPlayerDigging(CPacketPlayerDigging.Action.STOP_DESTROY_BLOCK, closest, direction));
			mc.player.swingArm(EnumHand.MAIN_HAND);
			return;
		}

		/* blockBreaking = null; */
		/*
		 * if (blockBreaking != null) {
		 * 
		 * if (mc.playerController.blockHitDelay > 1) {
		 * mc.playerController.blokHitDelay = 1; }
		 * 
		 * 
		 * mc.player.swingArm(EnumHand.MAIN_HAND);; EnumFacing direction =
		 * getClosestEnum(blockBreaking);
		 * 
		 * if (direction != null) { mc.playerController.clickBlock(blockBreaking,
		 * direction); BlockUtils.breakBlockSimple(closest); } }
		 */
	}

	private boolean blockChecks(Block block) {
		return block == Blocks.BED;
	}

	public static float[] getRotations(BlockPos block, EnumFacing face) {
		double x = block.getX() + 0.5 - mc.player.posX + (double) face.getFrontOffsetX() / 2;
		double z = block.getZ() + 0.5 - mc.player.posZ + (double) face.getFrontOffsetZ() / 2;
		double d1 = mc.player.posY + mc.player.getEyeHeight() - (block.getY() + 0.5);
		double d3 = MathHelper.sqrt(x * x + z * z);
		float yaw = (float) (Math.atan2(z, x) * 180.0D / Math.PI) - 90.0F;
		float pitch = (float) (Math.atan2(d1, d3) * 180.0D / Math.PI);

		if (yaw < 0.0F) {
			yaw += 360f;
		}

		return new float[] { yaw, pitch };
	}

	private EnumFacing getClosestEnum(BlockPos pos) {
		EnumFacing closestEnum = EnumFacing.UP;
		float rotations = MathHelper.wrapDegrees(getRotations(pos, EnumFacing.UP)[0]);

		if (rotations >= 45 && rotations <= 135) {
			closestEnum = EnumFacing.EAST;
		} else if ((rotations >= 135 && rotations <= 180) || (rotations <= -135 && rotations >= -180)) {
			closestEnum = EnumFacing.SOUTH;
		} else if (rotations <= -45 && rotations >= -135) {
			closestEnum = EnumFacing.WEST;
		} else if ((rotations >= -45 && rotations <= 0) || (rotations <= 45 && rotations >= 0)) {
			closestEnum = EnumFacing.NORTH;
		}

		if (MathHelper.wrapDegrees(getRotations(pos, EnumFacing.UP)[1]) > 75
				|| MathHelper.wrapDegrees(getRotations(pos, EnumFacing.UP)[1]) < -75) {
			closestEnum = EnumFacing.UP;
		}

		return closestEnum;
	}

	private EnumFacing getFacingDirection(BlockPos pos) {
		IBlockState state = mc.world.getBlockState(pos);
		EnumFacing direction = null;

		if (!mc.world.getBlockState(pos.add(0, 1, 0)).getBlock().isFullCube(state)
				&& !(mc.world.getBlockState(pos.add(0, 1, 0)).getBlock() instanceof BlockBed)) {
			direction = EnumFacing.UP;
		} else if (!mc.world.getBlockState(pos.add(0, -1, 0)).getBlock().isFullCube(state)
				&& !(mc.world.getBlockState(pos.add(0, -1, 0)).getBlock() instanceof BlockBed)) {
			direction = EnumFacing.DOWN;
		} else if (!mc.world.getBlockState(pos.add(1, 0, 0)).getBlock().isFullCube(state)
				&& !(mc.world.getBlockState(pos.add(1, 0, 0)).getBlock() instanceof BlockBed)) {
			direction = EnumFacing.EAST;
		} else if (!mc.world.getBlockState(pos.add(-1, 0, 0)).getBlock().isFullCube(state)
				&& !(mc.world.getBlockState(pos.add(-1, 0, 0)).getBlock() instanceof BlockBed)) {
			direction = EnumFacing.WEST;
		} else if (!mc.world.getBlockState(pos.add(0, 0, 1)).getBlock().isFullCube(state)
				&& !(mc.world.getBlockState(pos.add(0, 0, 1)).getBlock() instanceof BlockBed)) {
			direction = EnumFacing.SOUTH;
		} else if (!mc.world.getBlockState(pos.add(0, 0, 1)).getBlock().isFullCube(state)
				&& !(mc.world.getBlockState(pos.add(0, 0, -1)).getBlock() instanceof BlockBed)) {
			direction = EnumFacing.NORTH;
		}

		RayTraceResult rayResult = mc.world.rayTraceBlocks(
				new Vec3d(mc.player.posX, mc.player.posY + mc.player.getEyeHeight(), mc.player.posZ),
				new Vec3d(pos.getX() + 0.5, pos.getY(), pos.getZ() + 0.5));

		if (rayResult != null && rayResult.getBlockPos() == pos) {
			return rayResult.sideHit;
		}

		return direction;
	}
}
