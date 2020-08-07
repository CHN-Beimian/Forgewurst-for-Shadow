package net.wurstclient.forge.hacks;

import java.util.Comparator;
import java.util.Random;
import java.util.function.ToDoubleFunction;
import java.util.stream.Stream;

import org.lwjgl.opengl.GL11;

import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.client.renderer.tileentity.TileEntityRendererDispatcher;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityAgeable;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.monster.EntityEnderman;
import net.minecraft.entity.monster.EntityGolem;
import net.minecraft.entity.monster.EntityPigZombie;
import net.minecraft.entity.monster.IMob;
import net.minecraft.entity.passive.EntityAmbientCreature;
import net.minecraft.entity.passive.EntityAnimal;
import net.minecraft.entity.passive.EntityTameable;
import net.minecraft.entity.passive.EntityVillager;
import net.minecraft.entity.passive.EntityWaterMob;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.client.event.RenderWorldLastEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.wurstclient.fmlevents.WUpdateEvent;
import net.wurstclient.forge.Category;
import net.wurstclient.forge.ForgeWurst;
import net.wurstclient.forge.Hack;
import net.wurstclient.forge.compatibility.WEntity;
import net.wurstclient.forge.compatibility.WMinecraft;
import net.wurstclient.forge.compatibility.WPlayer;
import net.wurstclient.forge.hacks.SuperFlyHack.FlightMode;
import net.wurstclient.forge.loader.ModEnemyLoader;
import net.wurstclient.forge.loader.ModFriendsLoader;
import net.wurstclient.forge.settings.CheckboxSetting;
import net.wurstclient.forge.settings.EnumSetting;
import net.wurstclient.forge.settings.SliderSetting;
import net.wurstclient.forge.settings.SliderSetting.ValueDisplay;
import net.wurstclient.forge.utils.EntityFakePlayer;
import net.wurstclient.forge.utils.PlayerControllerUtils;
import net.wurstclient.forge.utils.RenderUtils;
import net.wurstclient.forge.utils.RotationUtils;

public class TpAuraHack extends Hack {
	private final SliderSetting hitrange = new SliderSetting("HitRange", 4.25, 1, 6, 0.05, ValueDisplay.DECIMAL);
	private final EnumSetting<HitWay> way = new EnumSetting<TpAuraHack.HitWay>("HitWay", HitWay.values(),
			HitWay.HITRANGE);
	private final EnumSetting<Target> mode = new EnumSetting<Target>("Target", Target.values(), Target.ENEMY);
	private Random random = new Random();
	private final SliderSetting range = new SliderSetting("Range", 5, 1, 200, 0.05, ValueDisplay.DECIMAL);

	private final EnumSetting<Priority> priority = new EnumSetting<>("Priority",
			"Determines which entity will be attacked first.\n"
					+ "\u00a7lDistance\u00a7r - Attacks the closest entity.\n"
					+ "\u00a7lAngle\u00a7r - Attacks the entity that requires\n" + "the least head movement.\n"
					+ "\u00a7lHealth\u00a7r - Attacks the weakest entity.",
			Priority.values(), Priority.ANGLE);

	private final CheckboxSetting filterPlayers = new CheckboxSetting("Filter players", "Won't attack other players.",
			false);
	private final CheckboxSetting filterSleeping = new CheckboxSetting("Filter sleeping",
			"Won't attack sleeping players.", false);
	private final SliderSetting filterFlying = new SliderSetting("Filter flying",
			"Won't attack players that\n" + "are at least the given\n" + "distance above ground.", 0, 0, 2, 0.05,
			v -> v == 0 ? "off" : ValueDisplay.DECIMAL.getValueString(v));

	private final CheckboxSetting filterMonsters = new CheckboxSetting("Filter monsters",
			"Won't attack zombies, creepers, etc.", false);
	private final CheckboxSetting filterPigmen = new CheckboxSetting("Filter pigmen", "Won't attack zombie pigmen.",
			false);
	private final CheckboxSetting filterEndermen = new CheckboxSetting("Filter endermen", "Won't attack endermen.",
			false);

	private final CheckboxSetting filterAnimals = new CheckboxSetting("Filter animals", "Won't attack pigs, cows, etc.",
			false);
	private final CheckboxSetting filterBabies = new CheckboxSetting("Filter babies",
			"Won't attack baby pigs,\n" + "baby villagers, etc.", false);
	private final CheckboxSetting filterPets = new CheckboxSetting("Filter pets",
			"Won't attack tamed wolves,\n" + "tamed horses, etc.", false);

	private final CheckboxSetting filterVillagers = new CheckboxSetting("Filter villagers", "Won't attack villagers.",
			false);
	private final CheckboxSetting filterGolems = new CheckboxSetting("Filter golems",
			"Won't attack iron golems,\n" + "snow golems and shulkers.", false);

	private final CheckboxSetting filterInvisible = new CheckboxSetting("Filter invisible",
			"Won't attack invisible entities.", false);

	private EntityLivingBase target;
	private EntityLivingBase enemy;

	public TpAuraHack() {
		super("TpAura", "tp and kill");
		setCategory(Category.COMBAT);
		addSetting(range);
		addSetting(priority);
		addSetting(filterPlayers);
		addSetting(filterSleeping);
		addSetting(filterFlying);
		addSetting(filterMonsters);
		addSetting(filterPigmen);
		addSetting(filterEndermen);
		addSetting(filterAnimals);
		addSetting(filterBabies);
		addSetting(filterPets);
		addSetting(filterVillagers);
		addSetting(filterGolems);
		addSetting(filterInvisible);
		addSetting(mode);
		addSetting(way);
		addSetting(hitrange);
	}

	@Override
	protected void onEnable() {
		wurst.getHax().autoClickerHack.setEnabled(false);
		wurst.getHax().massKilAuraHack.setEnabled(false);
		wurst.getHax().killAuraHack.setEnabled(false);
		EntityPlayerSP player = mc.player;
		if (player != null) {
			player.noClip = true;
			player.onGround = false;
			player.fallDistance = 0;
		}
		MinecraftForge.EVENT_BUS.register(this);
	}

	@Override
	protected void onDisable() {
		EntityPlayerSP player = mc.player;
		if (player != null) {
			player.noClip = false;
			player.onGround = true;
		}
		MinecraftForge.EVENT_BUS.unregister(this);
		target = null;
	}

	@SubscribeEvent
	public void onUpdate(WUpdateEvent event) {
		EntityPlayerSP player = event.getPlayer();
		World world = WPlayer.getWorld(player);

		if (player.getCooledAttackStrength(0) < 1)
			return;

		double rangeSq = Math.pow(range.getValue(), 2);
		Stream<EntityLivingBase> stream = world.loadedEntityList.parallelStream()
				.filter(e -> e instanceof EntityLivingBase).map(e -> (EntityLivingBase) e)
				.filter(e -> !e.isDead && e.getHealth() > 0).filter(e -> WEntity.getDistanceSq(player, e) <= rangeSq)
				.filter(e -> e != player).filter(e -> !(e instanceof EntityFakePlayer));

		if (filterPlayers.isChecked())
			stream = stream.filter(e -> !(e instanceof EntityPlayer));

		if (filterSleeping.isChecked())
			stream = stream.filter(e -> !(e instanceof EntityPlayer && ((EntityPlayer) e).isPlayerSleeping()));

		if (filterFlying.getValue() > 0)
			stream = stream.filter(e -> {

				if (!(e instanceof EntityPlayer))
					return true;

				AxisAlignedBB box = e.getEntityBoundingBox();
				box = box.union(box.offset(0, -filterFlying.getValue(), 0));
				// Using expand() with negative values doesn't work in 1.10.2.
				return world.collidesWithAnyBlock(box);
			});

		if (filterMonsters.isChecked())
			stream = stream.filter(e -> !(e instanceof IMob));

		if (filterPigmen.isChecked())
			stream = stream.filter(e -> !(e instanceof EntityPigZombie));

		if (filterEndermen.isChecked())
			stream = stream.filter(e -> !(e instanceof EntityEnderman));

		if (filterAnimals.isChecked())
			stream = stream.filter(e -> !(e instanceof EntityAnimal || e instanceof EntityAmbientCreature
					|| e instanceof EntityWaterMob));

		if (filterBabies.isChecked())
			stream = stream.filter(e -> !(e instanceof EntityAgeable && ((EntityAgeable) e).isChild()));

		if (filterPets.isChecked())
			stream = stream.filter(e -> !(e instanceof EntityTameable && ((EntityTameable) e).isTamed()))
					.filter(e -> !WEntity.isTamedHorse(e));

		if (filterVillagers.isChecked())
			stream = stream.filter(e -> !(e instanceof EntityVillager));

		if (filterGolems.isChecked())
			stream = stream.filter(e -> !(e instanceof EntityGolem));

		if (filterInvisible.isChecked())
			stream = stream.filter(e -> !e.isInvisible());

		target = stream.min(priority.getSelected().comparator).orElse(null);

		if (target == null)
			return;
		switch (way.getSelected()) {
		case HITRANGE:
			switch (mode.getSelected()) {
			case MISSILE2:
				break;
			case MISSILE:
				break;
			case TRACK:
				break;
			case NULL:
				player.setPositionAndUpdate(target.posX, target.posY + hitrange.getValue(), target.posZ);
				RotationUtils.faceVectorPacket(target.getEntityBoundingBox().getCenter());
				mc.playerController.attackEntity(player, target);
				player.swingArm(EnumHand.MAIN_HAND);
				break;
			case ENEMY:
				if (ModEnemyLoader.enemyList.contains(target.getName())) {
					player.setPositionAndUpdate(target.posX, target.posY + hitrange.getValue(), target.posZ);
					RotationUtils.faceVectorPacket(target.getEntityBoundingBox().getCenter());
					mc.playerController.attackEntity(player, target);
				}
				break;
			case FRIEND:
				if (!ModFriendsLoader.friendList.contains(target.getName())) {
					player.setPositionAndUpdate(target.posX, target.posY + hitrange.getValue(), target.posZ);
					RotationUtils.faceVectorPacket(target.getEntityBoundingBox().getCenter());
					mc.playerController.attackEntity(player, target);
					player.swingArm(EnumHand.MAIN_HAND);
					break;
				}
			}
			break;

		case RANDOM:
			switch (mode.getSelected()) {
			case MISSILE2:
				
				for (Entity enemy : world.playerEntities) {
					if (ModEnemyLoader.enemyList.contains(enemy.getName())) {
						double d1 = enemy.getDistance(mc.player.posX, mc.player.posY, mc.player.posZ);
						double d = enemy.getDistanceSq(player);
						if (d <= 200) {
						
								double[] v = new double[] { mc.player.posX, mc.player.posY, mc.player.posZ };
								double[] v1 = new double[] { enemy.posX, enemy.posY, enemy.posZ };
								PlayerControllerUtils.setReach(enemy, hitrange.getValue());
							
							
							  player.setPositionAndUpdate(enemy.posX + random.nextInt(2) * 2 - 2,
							  enemy.posY, player.posZ + random.nextInt(2) * 2 - 2);
							 
								mc.playerController.attackEntity(mc.player, enemy);
								mc.player.swingArm(EnumHand.MAIN_HAND);
						
						}
					}

				}
			break;
			case MISSILE:
				attackEnemy();
				break;
			case TRACK:
				findTarget();
				if (ModEnemyLoader.enemyList.contains(findTarget().getName())) {
					double[] d = new double[] { player.posX, player.posY, player.posZ };
					double[] d1 = new double[] { enemy.posX, enemy.posY, enemy.posZ };
					PlayerControllerUtils.setReach(player, hitrange.getValue());
					PlayerControllerUtils.teleportToPosition(d, d1, hitrange.getValue(), 0.0, false, true);
					player.setPositionAndUpdate(target.posX + random.nextInt(3) * 2 - 2, target.posY,
							target.posZ + random.nextInt(3) * 2 - 2);
					mc.playerController.attackEntity(player, enemy);
				}
				break;
			case NULL:
				player.setPositionAndUpdate(target.posX + random.nextInt(3) * 2 - 2, target.posY,
						target.posZ + random.nextInt(3) * 2 - 2);
				RotationUtils.faceVectorPacket(target.getEntityBoundingBox().getCenter());
				mc.playerController.attackEntity(player, target);
				player.swingArm(EnumHand.MAIN_HAND);
				break;
			case ENEMY:
				if (ModEnemyLoader.enemyList.contains(target.getName())) {
					player.setPositionAndUpdate(target.posX + random.nextInt(3) * 2 - 2, target.posY,
							target.posZ + random.nextInt(3) * 2 - 2);
					RotationUtils.faceVectorPacket(target.getEntityBoundingBox().getCenter());
					mc.playerController.attackEntity(player, target);
				}
				break;
			case FRIEND:
				if (!ModFriendsLoader.friendList.contains(target.getName())) {
					player.setPositionAndUpdate(target.posX + random.nextInt(3) * 2 - 2, target.posY,
							target.posZ + random.nextInt(3) * 2 - 2);
					RotationUtils.faceVectorPacket(target.getEntityBoundingBox().getCenter());
					mc.playerController.attackEntity(player, target);
					player.swingArm(EnumHand.MAIN_HAND);
					break;
				}
			}
			break;
		}

		/*
		 * switch (mode.getSelected()) { case NULL:
		 * player.setPositionAndUpdate(target.posX + random.nextInt(3) * 2 - 2,
		 * target.posY, target.posZ + random.nextInt(3) * 2 - 2);
		 * RotationUtils.faceVectorPacket(target.getEntityBoundingBox().getCenter());
		 * mc.playerController.attackEntity(player, target);
		 * player.swingArm(EnumHand.MAIN_HAND); break; case ENEMY: if
		 * (ModEnemyLoader.enemyList.contains(target.getName())) {
		 * player.setPositionAndUpdate(target.posX + random.nextInt(3) * 2 - 2,
		 * target.posY, target.posZ + random.nextInt(3) * 2 - 2);
		 * RotationUtils.faceVectorPacket(target.getEntityBoundingBox().getCenter());
		 * mc.playerController.attackEntity(player, target); } break; case FRIEND: if
		 * (!ModFriendsLoader.friendList.contains(target.getName())) {
		 * player.setPositionAndUpdate(target.posX + random.nextInt(3) * 2 - 2,
		 * target.posY, target.posZ + random.nextInt(3) * 2 - 2);
		 * RotationUtils.faceVectorPacket(target.getEntityBoundingBox().getCenter());
		 * mc.playerController.attackEntity(player, target);
		 * player.swingArm(EnumHand.MAIN_HAND); break; } }
		 */
		/*
		 * if (friends.isChecked()) { if
		 * (!ModFriendsLoader.friendList.contains(target.getName())) {
		 * player.setPositionAndUpdate(target.posX + random.nextInt(3) * 2 - 2,
		 * target.posY, target.posZ + random.nextInt(3) * 2 - 2);
		 * RotationUtils.faceVectorPacket(target.getEntityBoundingBox().getCenter());
		 * mc.playerController.attackEntity(player, target);
		 * player.swingArm(EnumHand.MAIN_HAND); } }else {
		 * player.setPositionAndUpdate(target.posX + random.nextInt(3) * 2 - 2,
		 * target.posY, target.posZ + random.nextInt(3) * 2 - 2);
		 * RotationUtils.faceVectorPacket(target.getEntityBoundingBox().getCenter());
		 * mc.playerController.attackEntity(player, target);
		 * player.swingArm(EnumHand.MAIN_HAND); }
		 */

	}

	public EntityLivingBase gettarget() {
		return this.target;
	}

	@SubscribeEvent
	public void onRenderWorldLast(RenderWorldLastEvent event) {
		if (target == null)
			return;

		// GL settings
		GL11.glEnable(GL11.GL_BLEND);
		GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
		GL11.glEnable(GL11.GL_LINE_SMOOTH);
		GL11.glLineWidth(2);
		GL11.glDisable(GL11.GL_TEXTURE_2D);
		GL11.glEnable(GL11.GL_CULL_FACE);
		GL11.glDisable(GL11.GL_DEPTH_TEST);

		GL11.glPushMatrix();
		GL11.glTranslated(-TileEntityRendererDispatcher.staticPlayerX, -TileEntityRendererDispatcher.staticPlayerY,
				-TileEntityRendererDispatcher.staticPlayerZ);

		AxisAlignedBB box = new AxisAlignedBB(BlockPos.ORIGIN);
		float p = (target.getMaxHealth() - target.getHealth()) / target.getMaxHealth();
		float red = p * 2F;
		float green = 2 - red;

		GL11.glTranslated(target.posX, target.posY, target.posZ);
		GL11.glTranslated(0, 0.05, 0);
		GL11.glScaled(target.width, target.height, target.width);
		GL11.glTranslated(-0.5, 0, -0.5);

		if (p < 1) {
			GL11.glTranslated(0.5, 0.5, 0.5);
			GL11.glScaled(p, p, p);
			GL11.glTranslated(-0.5, -0.5, -0.5);
		}

		GL11.glColor4f(red, green, 0, 0.25F);
		GL11.glBegin(GL11.GL_QUADS);
		RenderUtils.drawSolidBox(box);
		GL11.glEnd();

		GL11.glColor4f(red, green, 0, 0.5F);
		GL11.glBegin(GL11.GL_LINES);
		RenderUtils.drawOutlinedBox(box);
		GL11.glEnd();

		GL11.glPopMatrix();

		// GL resets
		GL11.glColor4f(1, 1, 1, 1);
		GL11.glEnable(GL11.GL_DEPTH_TEST);
		GL11.glEnable(GL11.GL_TEXTURE_2D);
		GL11.glDisable(GL11.GL_BLEND);
		GL11.glDisable(GL11.GL_LINE_SMOOTH);
	}

	private enum Priority {
		DISTANCE("Distance", e -> WEntity.getDistanceSq(WMinecraft.getPlayer(), e)),

		ANGLE("Angle", e -> RotationUtils.getAngleToLookVec(e.getEntityBoundingBox().getCenter())),

		HEALTH("Health", e -> e.getHealth());

		private final String name;
		private final Comparator<EntityLivingBase> comparator;

		private Priority(String name, ToDoubleFunction<EntityLivingBase> keyExtractor) {
			this.name = name;
			comparator = Comparator.comparingDouble(keyExtractor);
		}

		@Override
		public String toString() {
			return name;
		}
	}

	public enum Target {
		NULL, ENEMY, FRIEND, TRACK, MISSILE,MISSILE2
	}

	public enum HitWay {
		RANDOM, HITRANGE
	}

	public EntityLivingBase findTarget() {
		double rangeSq = Math.pow(200, 2);
		World world = mc.player.world;
		EntityPlayerSP player = mc.player;
		Stream<EntityLivingBase> stream = world.loadedEntityList.parallelStream()
				.filter(e -> e instanceof EntityLivingBase).map(e -> (EntityLivingBase) e)
				.filter(e -> !e.isDead && e.getHealth() > 0).filter(e -> WEntity.getDistanceSq(player, e) <= rangeSq)
				.filter(e -> e != player).filter(e -> !(e instanceof EntityFakePlayer));

		if (filterPlayers.isChecked())
			stream = stream.filter(e -> !(e instanceof EntityPlayer));

		if (filterSleeping.isChecked())
			stream = stream.filter(e -> !(e instanceof EntityPlayer && ((EntityPlayer) e).isPlayerSleeping()));

		if (filterFlying.getValue() > 0)
			stream = stream.filter(e -> {

				if (!(e instanceof EntityPlayer))
					return true;

				AxisAlignedBB box = e.getEntityBoundingBox();
				box = box.union(box.offset(0, -filterFlying.getValue(), 0));
				// Using expand() with negative values doesn't work in 1.10.2.
				return world.collidesWithAnyBlock(box);
			});

		if (filterMonsters.isChecked())
			stream = stream.filter(e -> !(e instanceof IMob));

		if (filterPigmen.isChecked())
			stream = stream.filter(e -> !(e instanceof EntityPigZombie));

		if (filterEndermen.isChecked())
			stream = stream.filter(e -> !(e instanceof EntityEnderman));

		if (filterAnimals.isChecked())
			stream = stream.filter(e -> !(e instanceof EntityAnimal || e instanceof EntityAmbientCreature
					|| e instanceof EntityWaterMob));

		if (filterBabies.isChecked())
			stream = stream.filter(e -> !(e instanceof EntityAgeable && ((EntityAgeable) e).isChild()));

		if (filterPets.isChecked())
			stream = stream.filter(e -> !(e instanceof EntityTameable && ((EntityTameable) e).isTamed()))
					.filter(e -> !WEntity.isTamedHorse(e));

		if (filterVillagers.isChecked())
			stream = stream.filter(e -> !(e instanceof EntityVillager));

		if (filterGolems.isChecked())
			stream = stream.filter(e -> !(e instanceof EntityGolem));

		if (filterInvisible.isChecked())
			stream = stream.filter(e -> !e.isInvisible());

		enemy = stream.min(Priority.DISTANCE.comparator).orElse(null);

		return enemy;

	}

	private void attackEnemy() {

		World world = mc.world;
		for (Entity player : world.playerEntities) {
			if (ModEnemyLoader.enemyList.contains(player.getName())) {
				double d1 = player.getDistance(mc.player.posX, mc.player.posY, mc.player.posZ);
				double d = player.getDistanceSq(player);
				if (d <= 76) {
					
						double[] v = new double[] { mc.player.posX, mc.player.posY, mc.player.posZ };
						double[] v1 = new double[] { player.posX, player.posY, player.posZ };
						PlayerControllerUtils.setReach(player, hitrange.getValue());
						PlayerControllerUtils.teleportToPosition(v, v1, hitrange.getValue(), 0.0, false, true);
						player.setPositionAndUpdate(player.posX + random.nextInt(3) * 2 - 2, player.posY,
								player.posZ + random.nextInt(3) * 2 - 2);
						mc.playerController.attackEntity(mc.player, player);
						mc.player.swingArm(EnumHand.MAIN_HAND);
					
				}
			}

		}

	}

}
