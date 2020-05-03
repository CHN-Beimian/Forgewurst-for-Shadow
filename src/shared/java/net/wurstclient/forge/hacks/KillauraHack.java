/*
 * Copyright (C) 2017 - 2019 | Wurst-Imperium | All rights reserved.
 *
 * This source code is subject to the terms of the GNU General Public
 * License, version 3. If a copy of the GPL was not distributed with this
 * file, You can obtain one at: https://www.gnu.org/licenses/gpl-3.0.txt
 */
package net.wurstclient.forge.hacks;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Comparator;
import java.util.Random;
import java.util.function.ToDoubleFunction;
import java.util.stream.Stream;

import org.lwjgl.opengl.Display;
import org.lwjgl.opengl.GL11;

import com.ibm.icu.impl.duration.impl.DataRecord.EUnitVariant;

import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.client.multiplayer.PlayerControllerMP;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.tileentity.TileEntityRendererDispatcher;
import net.minecraft.client.settings.KeyBinding;
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
import net.minecraft.entity.projectile.EntityFishHook;
import net.minecraft.init.Items;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.network.play.client.CPacketEntityAction;
import net.minecraft.network.play.client.CPacketPlaceRecipe;
import net.minecraft.network.play.client.CPacketPlayer;
import net.minecraft.network.play.client.CPacketPlayerDigging;
import net.minecraft.network.play.client.CPacketUseEntity;
import net.minecraft.network.play.server.SPacketSoundEffect;
import net.minecraft.network.play.server.SPacketUpdateHealth;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.Timer;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraftforge.client.event.RenderWorldLastEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.living.LivingEntityUseItemEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent.PlayerTickEvent;
import net.minecraftforge.fml.relauncher.ReflectionHelper;
import net.wurstclient.fmlevents.WPacketInputEvent;
import net.wurstclient.fmlevents.WUpdateEvent;
import net.wurstclient.forge.Category;
import net.wurstclient.forge.Friends;
import net.wurstclient.forge.Hack;
import net.wurstclient.forge.KeybindList.Keybind;
import net.wurstclient.forge.compatibility.WEntity;
import net.wurstclient.forge.compatibility.WMinecraft;
import net.wurstclient.forge.compatibility.WPlayer;
import net.wurstclient.forge.hacks.PVPHack.VelocityMode;
import net.wurstclient.forge.hacks.TpAuraHack.Target;
import net.wurstclient.forge.loader.ModEnemyLoader;
import net.wurstclient.forge.loader.ModFriendsLoader;
import net.wurstclient.forge.settings.CheckboxSetting;
import net.wurstclient.forge.settings.EnumSetting;
import net.wurstclient.forge.settings.SliderSetting;
import net.wurstclient.forge.settings.SliderSetting.ValueDisplay;
import net.wurstclient.forge.utils.ChatUtils;
import net.wurstclient.forge.utils.EntityFakePlayer;
import net.wurstclient.forge.utils.MathUtils;
import net.wurstclient.forge.utils.RenderUtils;
import net.wurstclient.forge.utils.RotationUtils;
import net.wurstclient.forge.utils.STimer;
import net.wurstclient.forge.utils.Wrapper;

public final class KillauraHack extends Hack {
	int n = 0;
	long lastLog = System.currentTimeMillis();
	public boolean isBlock;
	public boolean isDamage;
	private final CheckboxSetting autoBlock = new CheckboxSetting("AutoBlock", true);
	public final CheckboxSetting onlyPlayer = new CheckboxSetting("OnlyPlyaer", "Only attack players", true);
	public final EnumSetting<ModeRotate> moder = new EnumSetting<KillauraHack.ModeRotate>("ModeRotate",
			ModeRotate.values(), ModeRotate.C);
	public final CheckboxSetting clientRotate = new CheckboxSetting("ClientRotate", "Turn your head that you can see",
			false);
	private int time;
	private final CheckboxSetting useCooldown = new CheckboxSetting("Use cooldown",
			"Use your weapon's cooldown as the attack speed.\n" + "When checked, the 'Speed' slider will be ignored.",
			true);
	private final EnumSetting<RotateMode> rotateMode = new EnumSetting<KillauraHack.RotateMode>("RotateMode",
			RotateMode.values(), RotateMode.SIGMA);
	private final EnumSetting<Mode> mode1 = new EnumSetting<KillauraHack.Mode>("Mode", Mode.values(), Mode.M1);
	private final CheckboxSetting ecstasy = new CheckboxSetting("Ecstasy", false);
	private final CheckboxSetting rotate = new CheckboxSetting("Rotate", true);
	public static float sYaw;
	public static float sPitch;
	public static float aacB;
	int[] randoms = new int[] { 0, 1, 0 };
	private final STimer timer = new STimer();
	private final STimer cps = new STimer();
	private final SliderSetting CPS = new SliderSetting("CPS", 7, 1, 20, 1, ValueDisplay.DECIMAL);
	private final SliderSetting TIMER = new SliderSetting("Timer", 2.5, 0.1, 3, 1, ValueDisplay.DECIMAL);
	private final EnumSetting<Target> mode = new EnumSetting<Target>("Target", Target.values(), Target.ENEMY);
	private final CheckboxSetting friends = new CheckboxSetting("friends", true);
	private final SliderSetting range = new SliderSetting("Range", 5, 1, 6, 0.05, ValueDisplay.DECIMAL);
	private final CheckboxSetting legit = new CheckboxSetting("Legit", true);
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

	public KillauraHack() {
		super("Killaura", "Automatically attacks entities around you.");
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
		addSetting(CPS);
		addSetting(TIMER);
		addSetting(rotate);
		addSetting(ecstasy);
		addSetting(rotateMode);
		addSetting(mode1);
		addSetting(useCooldown);
		addSetting(moder);
		addSetting(onlyPlayer);
		addSetting(autoBlock);

	}

	@Override
	protected void onEnable() {
		wurst.getHax().autoClickerHack.setEnabled(false);
		wurst.getHax().tpAura.setEnabled(false);
		wurst.getHax().massKilAuraHack.setEnabled(false);
		MinecraftForge.EVENT_BUS.register(this);
	}

	@Override
	protected void onDisable() {
		MinecraftForge.EVENT_BUS.unregister(this);
		target = null;
	}

	public EntityLivingBase getTarget(EntityLivingBase target) {
		return target;
	}

	@SubscribeEvent
	public void onUpdate(WUpdateEvent event) {
		if (mode1.getSelected() == Mode.M1) {
			double hypixelTimer = TIMER.getValue() * 1000;
			int delayValue = (20 / CPS.getValueI()) * 50;
			EntityPlayerSP player = event.getPlayer();
			World world = WPlayer.getWorld(player);

			if (player.getCooledAttackStrength(0) < 1)
				return;

			double rangeSq = Math.pow(range.getValue(), 2);
			Stream<EntityLivingBase> stream = world.loadedEntityList.parallelStream()
					.filter(e -> e instanceof EntityLivingBase).map(e -> (EntityLivingBase) e)
					.filter(e -> !e.isDead && e.getHealth() > 0)
					.filter(e -> WEntity.getDistanceSq(player, e) <= rangeSq).filter(e -> e != player)
					.filter(e -> !(e instanceof EntityFakePlayer));

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

			if (wurst.getHax().antiBotHack.isEnabled()) {

				stream = stream.filter(
						e -> !(e instanceof EntityPlayer && wurst.getHax().antiBotHack.isBot((EntityPlayer) e)));
			}
			if (wurst.getHax().teamsHack.isEnabled()) {

				stream = stream.filter(e -> (e instanceof EntityPlayer && wurst.getHax().teamsHack.isTeam(e)));
			}

			target = stream.min(priority.getSelected().comparator).orElse(null);

			if (onlyPlayer.isChecked()) {
				if (!(target instanceof EntityPlayer))
					return;
			}

			/*
			 * if (target instanceof EntityPlayer) { if
			 * (wurst.getHax().antiBotHack.isEnabled()) { if
			 * (wurst.getHax().antiBotHack.isBot((EntityPlayer) target)) return; } if
			 * (wurst.getHax().teamsHack.isEnabled()) { if
			 * (!wurst.getHax().teamsHack.isTeam(target)) return; }
			 * 
			 * }
			 */
			if (target == null) {
				if (ecstasy.isChecked()) {
					wurst.getHax().derp.setEnabled(true);
				}
				return;
			}
			if (wurst.getHax().derp.isEnabled()) {
				wurst.getHax().derp.setEnabled(false);
			}
			if (cps.check(delayValue)) {
				switch (mode.getSelected()) {
				case NULL:
					/* customRots(target); */
					if (rotate.isChecked()) {
						if (rotateMode.getSelected() == RotateMode.M1) {
							RotationUtils.faceVectorForWalking(target.getEntityBoundingBox().getCenter());
						} else if (rotateMode.getSelected() == RotateMode.SIGMA) {
							customRots(target);
						}
					}
					mc.playerController.attackEntity(player, target);
					player.swingArm(EnumHand.MAIN_HAND);
					break;
				case ENEMY:
					if (ModEnemyLoader.enemyList.contains(target.getName())) {
						/* customRots(target); */
						if (rotate.isChecked()) {
							if (rotateMode.getSelected() == RotateMode.M1) {
								RotationUtils.faceVectorForWalking(target.getEntityBoundingBox().getCenter());
							} else if (rotateMode.getSelected() == RotateMode.SIGMA) {
								customRots(target);
							}
						}
						mc.player.swingArm(EnumHand.MAIN_HAND);
						mc.playerController.attackEntity(player, target);
					}
					break;
				case FRIEND:
					if (!ModFriendsLoader.friendList.contains(target.getName())) {
						/* customRots(target); */
						if (rotate.isChecked()) {
							if (rotateMode.getSelected() == RotateMode.M1) {
								RotationUtils.faceVectorForWalking(target.getEntityBoundingBox().getCenter());
							} else if (rotateMode.getSelected() == RotateMode.SIGMA) {
								customRots(target);
							}
						}
						doHit();
						doBlock();
						break;
					}
				case Single:
					if (!isCorrectEntity(target))
						return;
					mc.playerController.attackEntity(player, target);
					player.swingArm(EnumHand.MAIN_HAND);
					break;
				}
				cps.reset();
				/*
				 * if(friends.isChecked()) {
				 * if(!ModFriendsLoader.friendList.contains(target.getName())) { RotationUtils
				 * .faceVectorPacket(target.getEntityBoundingBox().getCenter());
				 * 
				 * mc.playerController.attackEntity(player, target);
				 * player.swingArm(EnumHand.MAIN_HAND); } }else { RotationUtils
				 * .faceVectorPacket(target.getEntityBoundingBox().getCenter());
				 * 
				 * mc.playerController.attackEntity(player, target);
				 * player.swingArm(EnumHand.MAIN_HAND); }
				 */
			}
		} else if (mode1.getSelected() == Mode.M2) {
			EntityPlayerSP player = event.getPlayer();
			World world = player.world;
			time += 50;
			if (useCooldown.isChecked() ? player.getCooledAttackStrength(0) < 1 : time < 1000 / CPS.getValue())
				return;
			double rangeSq = Math.pow(range.getValue(), 2);
			Stream<EntityLivingBase> stream = world.loadedEntityList.parallelStream()
					.filter(e -> e instanceof EntityLivingBase).map(e -> (EntityLivingBase) e)
					.filter(e -> !e.isDead && e.getHealth() > 0)
					.filter(e -> WEntity.getDistanceSq(player, e) <= rangeSq).filter(e -> e != player)
					.filter(e -> !(e instanceof EntityFakePlayer));

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
			if (wurst.getHax().antiBotHack.isEnabled()) {

				stream = stream.filter(
						e -> !(e instanceof EntityPlayer && wurst.getHax().antiBotHack.isBot((EntityPlayer) e)));
			}
			if (wurst.getHax().teamsHack.isEnabled()) {

				stream = stream.filter(e -> !(e instanceof EntityPlayer && wurst.getHax().teamsHack.isTeam(e)));
			}

			target = stream.min(priority.getSelected().comparator).orElse(null);
			if (target == null)
				return;

			if (onlyPlayer.isChecked()) {
				if (!(target instanceof EntityPlayer))
					return;
			}

			/*
			 * if (target instanceof EntityPlayer) { if
			 * (wurst.getHax().antiBotHack.isEnabled()) { if
			 * (wurst.getHax().antiBotHack.isBot((EntityPlayer) target)) return; } if
			 * (wurst.getHax().teamsHack.isEnabled()) { if
			 * (!wurst.getHax().teamsHack.isTeam(target)) return; }
			 * 
			 * }
			 */

			switch (mode.getSelected()) {
			case NULL:
				/* customRots(target); */
				if (rotate.isChecked()) {
					if (rotateMode.getSelected() == RotateMode.M1) {
						RotationUtils.faceVectorForWalking(target.getEntityBoundingBox().getCenter());
					} else if (rotateMode.getSelected() == RotateMode.SIGMA) {
						customRots(target);
					}
				}
				doMaxVelocity();
				mc.playerController.attackEntity(player, target);
				player.swingArm(EnumHand.MAIN_HAND);
				time = 0;
				break;
			case ENEMY:
				if (ModEnemyLoader.enemyList.contains(target.getName())) {
					/* customRots(target); */
					if (rotate.isChecked()) {
						if (rotateMode.getSelected() == RotateMode.M1) {
							RotationUtils.faceVectorForWalking(target.getEntityBoundingBox().getCenter());
						} else if (rotateMode.getSelected() == RotateMode.SIGMA) {
							customRots(target);
						}
					}
					mc.player.swingArm(EnumHand.MAIN_HAND);
					doMaxVelocity();
					mc.playerController.attackEntity(player, target);
					time = 0;
				}
				break;
			case FRIEND:
				if (!ModFriendsLoader.friendList.contains(target.getName())) {
					/* customRots(target); */
					if (rotate.isChecked()) {
						if (rotateMode.getSelected() == RotateMode.M1) {
							RotationUtils.faceVectorForWalking(target.getEntityBoundingBox().getCenter());
						} else if (rotateMode.getSelected() == RotateMode.SIGMA) {
							customRots(target);
						}
					}
					doMaxVelocity();
					doHit();
					doBlock();
					/* rightClick(); */

					time = 0;

					break;

				}
			case Single:

				break;
			}

		}
	}

	private void doHit() {
		if (mc.player == null)
			return;

		if (autoBlock.isChecked()) {
			if (isBlock)
				return;
		}
		mc.playerController.attackEntity(mc.player, target);
		mc.player.swingArm(EnumHand.MAIN_HAND);

	}

	private void doBlock() {
		isBlock = true;
		if (mc.player == null)
			return;
		if (!autoBlock.isChecked())
			return;
		if (mc.player.getHeldItem(EnumHand.MAIN_HAND).getItem() == Items.WOODEN_SWORD
				|| mc.player.getHeldItem(EnumHand.MAIN_HAND).getItem() == Items.DIAMOND_SWORD
				|| mc.player.getHeldItem(EnumHand.MAIN_HAND).getItem() == Items.STONE_SWORD
				|| mc.player.getHeldItem(EnumHand.MAIN_HAND).getItem() == Items.GOLDEN_SWORD
				|| mc.player.getHeldItem(EnumHand.MAIN_HAND).getItem() == Items.IRON_SWORD) {
			/*
			 * rightClick(); rightClick(); rightClick();
			 */
			/* KeyBinding.onTick(mc.gameSettings.keyBindUseItem.getKeyCode()); */
			/*
			 * rightClick(); mc.player.connection.sendPacket(new
			 * CPacketUseEntity(target,EnumHand.OFF_HAND,new Vec3d((double)randomNumber(-50,
			 * 50) / 100.0D, (double)randomNumber(0, 200) / 100.0D,
			 * (double)randomNumber(-50, 50) / 100.0D)));
			 * mc.player.connection.sendPacket(new CPacketUseEntity(target,
			 * EnumHand.OFF_HAND));
			 */

			KeyBinding.setKeyBindState(mc.gameSettings.keyBindUseItem.getKeyCode(), true);
			if (System.currentTimeMillis() - lastLog < 5000)
				return;

			KeyBinding.setKeyBindState(mc.gameSettings.keyBindUseItem.getKeyCode(), false);
			rightClick();
			isBlock = false;
		}

	}

	private void unBloock() {

		try {
			Method syncCurrentPlayItem = mc.playerController.getClass()
					.getDeclaredMethod(wurst.isObfuscated() ? "func_147121_ag" : "syncCurrentPlayItem");
			syncCurrentPlayItem.setAccessible(true);
			syncCurrentPlayItem.invoke(mc.playerController);

		} catch (ReflectiveOperationException e) {
			setEnabled(false);
			throw new RuntimeException(e);
		}

		mc.player.connection.sendPacket(
				new CPacketPlayerDigging(net.minecraft.network.play.client.CPacketPlayerDigging.Action.RELEASE_USE_ITEM,
						BlockPos.ORIGIN, EnumFacing.DOWN));
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
		NULL, ENEMY, FRIEND, Single
	}

	public static int randomNumber(int max, int min) {
		return Math.round((float) min + (float) Math.random() * (float) (max - min));
	}

	public void customRots(EntityLivingBase ent) {
		double randomYaw = 0.05D;
		double randomPitch = 0.05D;
		float[] rotsN = this.getCustomRotsChange(sYaw, sPitch, target.posX + (double) randomNumber(1, -1) * randomYaw,
				target.posY + (double) randomNumber(1, -1) * randomPitch,
				target.posZ + (double) randomNumber(1, -1) * randomYaw);
		float targetYaw = rotsN[0];
		float yawFactor = targetYaw * targetYaw / (4.7F * targetYaw);

		if (targetYaw < 5.0F) {
			yawFactor = targetYaw * targetYaw / (3.7F * targetYaw);
		}

		if (Math.abs(yawFactor) > 7.0F) {
			aacB = yawFactor * 7.0F;
			yawFactor = targetYaw * targetYaw / (3.7F * targetYaw);
		} else {
			yawFactor = targetYaw * targetYaw / (6.7F * targetYaw) + aacB;
		}

		/* em.setYaw(sYaw + yawFactor); */
		sYaw += yawFactor;
		float targetPitch = rotsN[1];
		float pitchFactor = targetPitch / 3.7F;
		mc.player.connection.sendPacket(new CPacketPlayer.Rotation(sYaw + yawFactor, sPitch + pitchFactor, true));
		/* em.setPitch(sPitch + pitchFactor); */
		sPitch += pitchFactor;
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

		if (!mc.world.getEntitiesWithinAABBExcludingEntity(mc.player, mc.player.getEntityBoundingBox())
				.contains(target)) {
			x += 0.3D * (double) this.randoms[0];
			y -= 0.4D + mult * (double) this.randoms[1];
			z += 0.3D * (double) this.randoms[2];
		}

		xDiff = x - mc.player.posX;
		zDiff = z - mc.player.posZ;
		yDiff = y - mc.player.posY;
		float yawToEntity = (float) (Math.atan2(zDiff, xDiff) * 180.0D / Math.PI) - 90.0F;
		float pitchToEntity = (float) (-(Math.atan2(yDiff, dist) * 180.0D / Math.PI));
		return new float[] { MathUtils.wrapAngleTo180_float(-(yaw - yawToEntity)),
				-MathUtils.wrapAngleTo180_float(pitch - pitchToEntity) - 2.5F };
	}

	private enum Mode {
		M1, M2, SIGMA
	}

	private enum RotateMode {
		M1, SIGMA, Wurst
	}

	private void doMaxVelocity() {
		if (wurst.getHax().pvpHack.max_velocity_ == true) {
			/*
			 * ReflectionHelper.setPrivateValue(mc.player.getClass(), mc.player, new
			 * Boolean(false), 5);
			 */
			if (wurst.getHax().pvpHack.velocitymode.getSelected() == VelocityMode.REFLECTION) {
				try {
					Field f = mc.player.getClass()
							.getDeclaredField(wurst.isObfuscated() ? "field_175171_bO" : "serverSprintState");
					f.setAccessible(true);
					f.setBoolean(mc.player, false);
				} catch (ReflectiveOperationException e) {
					throw new RuntimeException(e);
				}
			} else if (wurst.getHax().pvpHack.velocitymode.getSelected() == VelocityMode.PACKET) {
				mc.player.connection
						.sendPacket(new CPacketEntityAction(mc.player, CPacketEntityAction.Action.START_SPRINTING));
			}
		}
	}

	private boolean isCorrectEntity(Entity entity) {
		EntityPlayerSP player = mc.player;
		World world = mc.world;

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

		return stream.findFirst().isPresent();
	}

	/*
	 * @SubscribeEvent public void onBlock(WUpdateEvent event) {
	 * 
	 * if(mc.player==null) return; n=n+1;
	 * 
	 * doBlock();
	 * 
	 * }
	 */

	@SubscribeEvent
	public void onTickEvent(PlayerTickEvent event) {
		if (mc.player == null)
			return;
		if (target == null)
			return;
		if (ModFriendsLoader.friendList.contains(target.getName()))
			return;

		if (onlyPlayer.isChecked()) {
			if (!(target instanceof EntityPlayer))
				return;
		}

		if (target instanceof EntityPlayer) {
			if (wurst.getHax().antiBotHack.isEnabled()) {
				if (wurst.getHax().antiBotHack.isBot((EntityPlayer) target))
					return;
			}
			if (wurst.getHax().teamsHack.isEnabled()) {
				if (!wurst.getHax().teamsHack.isTeam(target))
					return;
			}

		}
		if (isDamage)
			return;

		if (rotateMode.getSelected() == RotateMode.Wurst) {
			if (moder.getSelected() == ModeRotate.C) {
				RotationUtils.faceVectorC(target.getEntityBoundingBox().getCenter());
			} else if (moder.getSelected() == ModeRotate.P) {
				RotationUtils.faceVectorP(target.getEntityBoundingBox().getCenter());
			}

		}
	}

	public static enum ModeRotate {
		P, C
	}

	@SubscribeEvent
	public void onHurt(WUpdateEvent event) {
		EntityPlayerSP player = Wrapper.getPlayer();
		if (player == null)
			return;
		if (player.hurtTime > 0 && player.hurtTime < 7) {
			isDamage = true;
		} else {
			isDamage = false;
		}
	}

	private void rightClick() {
		try {
			Method rightClickMouse = mc.getClass()
					.getDeclaredMethod(wurst.isObfuscated() ? "func_147121_ag" : "rightClickMouse");
			rightClickMouse.setAccessible(true);
			rightClickMouse.invoke(mc);

		} catch (ReflectiveOperationException e) {
			setEnabled(false);
			throw new RuntimeException(e);
		}
	}
}
