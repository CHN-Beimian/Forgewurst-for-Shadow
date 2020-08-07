package net.wurstclient.forge.hacks.combat;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.monster.EntityMob;
import net.minecraft.entity.passive.EntityAnimal;
import net.minecraft.entity.passive.EntityVillager;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.network.play.client.CPacketPlayer;
import net.minecraft.util.EnumHand;
import net.minecraft.util.Timer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.wurstclient.fmlevents.WUpdateEvent;
import net.wurstclient.forge.Category;
import net.wurstclient.forge.Hack;
import net.wurstclient.forge.ai.AStarCustomPathFinder;
import net.wurstclient.forge.clickgui.Slider;
import net.wurstclient.forge.loader.ModFriendsLoader;
import net.wurstclient.forge.settings.CheckboxSetting;
import net.wurstclient.forge.settings.SliderSetting;
import net.wurstclient.forge.settings.SliderSetting.ValueDisplay;
import net.wurstclient.forge.utils.STimer;
import scala.reflect.internal.util.Statistics.TimerStack;

public class InfiniteAuraHack extends Hack {
	private final SliderSetting TIMER=new SliderSetting("Timer", 2.5, 0.1, 3, 1, ValueDisplay.DECIMAL);
	private final STimer timer=new STimer();
	private final STimer cps=new STimer();
	private double dashDistance = 5;
	private boolean canPass;
	private final CheckboxSetting invisibles = new CheckboxSetting("Invisibles", false);
	private final CheckboxSetting animal = new CheckboxSetting("Animals", true);
	private final CheckboxSetting player = new CheckboxSetting("Player", true);
	private final SliderSetting maxt = new SliderSetting("MAXT", 5, 1, 50, 1, ValueDisplay.DECIMAL);
	private final SliderSetting range1 = new SliderSetting("Range", 82, 2, 100, 1, ValueDisplay.DECIMAL);
	private final SliderSetting CPS = new SliderSetting("CPS", 7, 1, 20, 1, ValueDisplay.DECIMAL);
	private ArrayList<Vec3d> path = new ArrayList<>();
	private List<Vec3d>[] test = new ArrayList[50];
	private List<EntityLivingBase> targets = new CopyOnWriteArrayList<>();


	public InfiniteAuraHack() {
		super("InfiniteAura", "Killaura but with infinite reach");
		setCategory(Category.COMBAT);
		addSetting(CPS);
		addSetting(animal);
		addSetting(invisibles);
		addSetting(maxt);
		addSetting(player);
		addSetting(range1);
		addSetting(TIMER);
	}

	@Override
	protected void onEnable() {
		timer.reset();
		targets.clear();
		MinecraftForge.EVENT_BUS.register(this);
	}

	@Override
	protected void onDisable() {
		targets.clear();
		MinecraftForge.EVENT_BUS.unregister(this);
	}

	@SubscribeEvent
	public void onUpdate(WUpdateEvent event) {
		double hypixelTimer = TIMER.getValue()*1000;
		int maxtTargets = maxt.getValueI();
		int delayValue = (20 / CPS.getValueI()) * 50;
		targets = getTargets();
		 if (cps.check(delayValue)) {
		if (targets.size() > 0) {
			test = new ArrayList[50];
			for (int i = 0; i < (targets.size() > maxtTargets ? maxtTargets : targets.size()); i++) {
				EntityLivingBase T = targets.get(i);
				Vec3d topFrom = new Vec3d(mc.player.posX, mc.player.posY, mc.player.posZ);
				Vec3d to = new Vec3d(T.posX, T.posY, T.posZ);
				path = computePath(topFrom, to);
				test[i] = path;
				for (Vec3d pathElm : path) {

					mc.player.connection.sendPacket(new CPacketPlayer.PositionRotation(pathElm.x, pathElm.y, pathElm.z,
							mc.player.rotationYaw, mc.player.rotationPitch, true));

				}

				mc.player.swingArm(EnumHand.MAIN_HAND);
				mc.playerController.attackEntity(mc.player, T);
				Collections.reverse(path);
				for (Vec3d pathElm : path) {

					mc.player.connection.sendPacket(new CPacketPlayer.PositionRotation(pathElm.x, pathElm.y, pathElm.z,
							mc.player.rotationYaw, mc.player.rotationPitch, true));
				}
			}
			 cps.reset();
		}
		 }
	}

	boolean validEntity(EntityLivingBase entity) {
		float range = range1.getValueF();
		boolean players = player.isChecked();
		boolean animals = animal.isChecked();

		if ((mc.player.isEntityAlive()) && !(entity instanceof EntityPlayerSP)) {
			if (mc.player.getDistance(entity) <= range) {

				if (ModFriendsLoader.friendList.contains(entity.getName())) {
					return false;
				}
				if (entity.isPlayerSleeping()) {
					return false;
				}

				if (entity instanceof EntityPlayer) {
					if (players) {

						EntityPlayer player = (EntityPlayer) entity;
						if (!player.isEntityAlive() && player.getHealth() == 0.0) {
							return false;
						} else if (player.isInvisible() && !invisibles.isChecked()) {
							return false;
						} else if (ModFriendsLoader.friendList.contains(player.getName())) {
							return false;
						} else
							return true;
					}
				} else {
					if (!entity.isEntityAlive()) {

						return false;
					}
				}

				if (entity instanceof EntityMob && animals) {

					return true;
				}
				if ((entity instanceof EntityAnimal || entity instanceof EntityVillager) && animals) {
					if (entity.getName().equals("Villager")) {
						return false;
					}
					return true;
				}
			}
		}

		return false;
	}

	private List<EntityLivingBase> getTargets() {
		List<EntityLivingBase> targets = new ArrayList<>();

		for (Object o : mc.world.getLoadedEntityList()) {
			if (o instanceof EntityLivingBase) {
				EntityLivingBase entity = (EntityLivingBase) o;
				if (validEntity(entity)) {
					targets.add(entity);
				}
			}
		}
		targets.sort((o1, o2) -> (int) (o1.getDistance(mc.player) * 1000 - o2.getDistance(mc.player) * 1000));
		return targets;
	}

	private void canPassThrow(BlockPos pos) {
		Block block = Minecraft.getMinecraft().world.getBlockState(new BlockPos(pos.getX(), pos.getY(), pos.getZ()))
				.getBlock();

		/*
		 * return block.getMaterial((IBlockState) block) == Material.AIR ||
		 * block.getMaterial((IBlockState) block) == Material.PLANTS ||
		 * block.getMaterial((IBlockState) block) == Material.VINE || block ==
		 * Blocks.LADDER || block == Blocks.WATER || block == Blocks.FLOWING_WATER ||
		 * block == Blocks.WALL_SIGN || block == Blocks.STANDING_SIGN||true;
		 */

		if (block == null)
			return;
		if (block == Blocks.AIR||block==Blocks.PLANKS
				|| block == Blocks.VINE || block == Blocks.LADDER
				|| block == Blocks.WATER || block == Blocks.FLOWING_WATER || block == Blocks.WALL_SIGN
				|| block == Blocks.STANDING_SIGN) {
			
			
			canPass=true;
			
		}else {
			canPass=false;
		}
	}

	private ArrayList<Vec3d> computePath(Vec3d topFrom, Vec3d to) {
		canPassThrow(new BlockPos(topFrom));
		if (canPass!=true) {
			topFrom = topFrom.addVector(0, 1, 0);
		}
		AStarCustomPathFinder pathfinder = new AStarCustomPathFinder(topFrom, to);
		pathfinder.compute();

		int i = 0;
		Vec3d lastLoc = null;
		Vec3d lastDashLoc = null;
		ArrayList<Vec3d> path = new ArrayList<Vec3d>();
		ArrayList<Vec3d> pathFinderPath = pathfinder.getPath();
		for (Vec3d pathElm : pathFinderPath) {
			if (i == 0 || i == pathFinderPath.size() - 1) {
				if (lastLoc != null) {
					path.add(lastLoc.addVector(0.5, 0, 0.5));
				}
				path.add(pathElm.addVector(0.5, 0, 0.5));
				lastDashLoc = pathElm;
			} else {
				boolean canContinue = true;
				if (pathElm.squareDistanceTo(lastDashLoc) > dashDistance * dashDistance) {
					canContinue = false;
				} else {
					double smallX = Math.min(lastDashLoc.x, pathElm.x);
					double smallY = Math.min(lastDashLoc.y, pathElm.y);
					double smallZ = Math.min(lastDashLoc.z, pathElm.z);
					double bigX = Math.max(lastDashLoc.x, pathElm.x);
					double bigY = Math.max(lastDashLoc.y, pathElm.y);
					double bigZ = Math.max(lastDashLoc.z, pathElm.z);
					cordsLoop: for (int x = (int) smallX; x <= bigX; x++) {
						for (int y = (int) smallY; y <= bigY; y++) {
							for (int z = (int) smallZ; z <= bigZ; z++) {
								if (!AStarCustomPathFinder.checkPositionValidity(x, y, z, false)) {
									canContinue = false;
									break cordsLoop;
								}
							}
						}
					}
				}
				if (!canContinue) {
					path.add(lastLoc.addVector(0.5, 0, 0.5));
					lastDashLoc = lastLoc;
				}
			}
			lastLoc = pathElm;
			i++;
		}
		return path;
	}

}
