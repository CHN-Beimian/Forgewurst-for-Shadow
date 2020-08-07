package net.wurstclient.forge.hacks.combat;

import java.util.Comparator;
import java.util.Random;
import java.util.function.ToDoubleFunction;
import java.util.stream.Stream;

import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.client.settings.KeyBinding;
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
import net.minecraft.item.ItemAxe;
import net.minecraft.item.ItemSword;
import net.minecraft.network.play.client.CPacketPlayer;
import net.minecraft.network.play.client.CPacketTabComplete;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.world.World;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.client.FMLClientHandler;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent.PlayerTickEvent;
import net.wurstclient.fmlevents.WUpdateEvent;
import net.wurstclient.forge.Category;
import net.wurstclient.forge.Hack;
import net.wurstclient.forge.compatibility.WEntity;
import net.wurstclient.forge.compatibility.WMinecraft;
import net.wurstclient.forge.compatibility.WPlayer;
import net.wurstclient.forge.settings.CheckboxSetting;
import net.wurstclient.forge.settings.EnumSetting;
import net.wurstclient.forge.settings.SliderSetting;
import net.wurstclient.forge.settings.SliderSetting.ValueDisplay;
import net.wurstclient.forge.utils.EntityFakePlayer;
import net.wurstclient.forge.utils.RotationUtils;
import net.wurstclient.forge.utils.Timer;
import net.wurstclient.forge.utils.Wrapper;

public class AutoClickerHack extends Hack {
	private Timer timer = new Timer();
	private Random random = new Random();
	private final CheckboxSetting requireWeapon = new CheckboxSetting("requireWeapon", false);
	private final SliderSetting range = new SliderSetting("Range", 5, 1, 100, 0.05, ValueDisplay.DECIMAL);
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
	private final SliderSetting cps = new SliderSetting("cps", 8, 1, 20, 1, ValueDisplay.DECIMAL);
	private int n = 0;
	private EnumSetting<MODE> mode = new EnumSetting<MODE>("Mode", MODE.values(), MODE.SWITCH);

	public AutoClickerHack() {
		super("AutoClicker", "");
		setCategory(Category.COMBAT);
		addSetting(cps);
		addSetting(mode);
		addSetting(requireWeapon);

	}

	@Override
	protected void onEnable() {
		wurst.getHax().tpAura.setEnabled(false);
		wurst.getHax().killAuraHack.setEnabled(false);
		wurst.getHax().massKilAuraHack.setEnabled(false);
		MinecraftForge.EVENT_BUS.register(this);
	}

	@Override
	protected void onDisable() {
		MinecraftForge.EVENT_BUS.unregister(this);
	}

	@SubscribeEvent
	public void onUpdate(WUpdateEvent event) {
		EntityPlayerSP player = event.getPlayer();
		World world = WPlayer.getWorld(player);
		switch (mode.getSelected()) {
		case ENABLE:
			if (this.timer.hasReached(1000.0 / this.cps.getValue() - random.nextInt(4))) {
				if (Wrapper.getMinecraft().objectMouseOver != null
						&& Wrapper.getMinecraft().objectMouseOver.entityHit != null) {
					KeyBinding.onTick(mc.gameSettings.keyBindAttack.getKeyCode());
				}
				timer.reset();
			}
			break;
		case SWITCH:
			if (Wrapper.getPlayer() != null) {

				if (this.timer.hasReached(1000.0 / this.cps.getValue() - random.nextInt(4))) {
					
					if (Wrapper.getMinecraft().objectMouseOver != null
							&& Wrapper.getMinecraft().objectMouseOver.entityHit != null) {
						Wrapper.getPlayer().swingArm(EnumHand.MAIN_HAND);
						mc.playerController.attackEntity(player, Wrapper.getMinecraft().objectMouseOver.entityHit);
						
					}
					timer.reset();
				}

			}
			break;
		}

	}

	public enum MODE {
		ENABLE, SWITCH
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

	private boolean shouldClick() {
		if (Wrapper.getPlayer().getHeldItemMainhand() != null
				&& !(Wrapper.getPlayer().getHeldItemMainhand().getItem() instanceof ItemSword)
				&& !(Wrapper.getPlayer().getHeldItemMainhand().getItem() instanceof ItemAxe)
				&& this.requireWeapon.isChecked()) {
			return false;
		}
		if (Wrapper.getPlayer().getHeldItemMainhand() == null && this.requireWeapon.isChecked()) {
			return false;
		}
		if (!Wrapper.getMinecraft().gameSettings.keyBindAttack.isPressed()) {
			return false;
		}
		if (Wrapper.getMinecraft().gameSettings.keyBindUseItem.isPressed()) {
			return false;
		}
		if (Wrapper.getMinecraft().currentScreen != null) {
			return false;
		}
		return true;
	}
	
}
