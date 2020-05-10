package net.wurstclient.forge.hacks;

import java.util.Comparator;
import java.util.function.ToDoubleFunction;
import java.util.stream.Stream;

import net.minecraft.client.entity.EntityPlayerSP;
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
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.world.World;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.wurstclient.forge.Category;
import net.wurstclient.forge.Hack;
import net.wurstclient.forge.compatibility.WEntity;
import net.wurstclient.forge.compatibility.WMinecraft;

import net.wurstclient.forge.hacks.KillauraHack.ModeRotate;


import net.wurstclient.forge.hacks.KillauraHack.Target;
import net.wurstclient.forge.settings.CheckboxSetting;
import net.wurstclient.forge.settings.EnumSetting;
import net.wurstclient.forge.settings.SliderSetting;
import net.wurstclient.forge.settings.SliderSetting.ValueDisplay;
import net.wurstclient.forge.utils.EntityFakePlayer;
import net.wurstclient.forge.utils.RotationUtils;
import net.wurstclient.forge.utils.STimer;

public class AimBotHack extends Hack{
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
	


	public static float sYaw;
	public static float sPitch;
	public static float aacB;
	int[] randoms = new int[] { 0, 1, 0 };
	private final STimer timer = new STimer();
	private final STimer cps = new STimer();

	
	private final CheckboxSetting friends = new CheckboxSetting("friends", true);
	private final SliderSetting range = new SliderSetting("Range", 5, 1, 6, 0.05, ValueDisplay.DECIMAL);

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
	public AimBotHack() {
		super("AimBot","auto aim");
		setCategory(Category.COMBAT);
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
	public void onUpdate() {
		double rangeSq = Math.pow(range.getValue(), 2);
		World world=mc.player.world;
		EntityPlayerSP player=mc.player;
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
		
		RotationUtils.faceVectorC(target.getEntityBoundingBox().getCenter());

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

}
