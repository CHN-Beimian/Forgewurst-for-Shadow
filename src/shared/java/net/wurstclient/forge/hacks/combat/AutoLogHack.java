package net.wurstclient.forge.hacks.combat;

import java.util.Comparator;
import java.util.function.ToDoubleFunction;
import java.util.stream.Stream;

import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityAgeable;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.item.EntityEnderCrystal;
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
import net.minecraft.init.Enchantments;
import net.minecraft.item.Item;
import net.minecraft.item.ItemAxe;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemSword;
import net.minecraft.network.play.server.SPacketDisconnect;
import net.minecraft.util.CombatRules;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.world.World;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.EntityJoinWorldEvent;
import net.minecraftforge.event.entity.living.LivingDamageEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.wurstclient.fmlevents.WUpdateEvent;
import net.wurstclient.forge.Category;
import net.wurstclient.forge.Hack;
import net.wurstclient.forge.compatibility.WEntity;
import net.wurstclient.forge.compatibility.WMinecraft;
import net.wurstclient.forge.settings.CheckboxSetting;
import net.wurstclient.forge.settings.SliderSetting;
import net.wurstclient.forge.settings.SliderSetting.ValueDisplay;
import net.wurstclient.forge.utils.ChatUtils;
import net.wurstclient.forge.utils.EntityFakePlayer;
import net.wurstclient.forge.utils.PlayerDamageUtil;
import net.wurstclient.forge.utils.RotationUtils;

public class AutoLogHack extends Hack {
	public EntityLivingBase entity;
	public float EntityDamage;
	public float Damage;
	public float MyDamage;
	private SliderSetting health = new SliderSetting("Health", 6, 0, 36, 1, ValueDisplay.DECIMAL);
	private final CheckboxSetting leave = new CheckboxSetting("leave", false);
	private boolean shouldLog = false;
	long lastLog = System.currentTimeMillis();
	private EntityLivingBase target;

	public AutoLogHack() {
		super("AutoLog", "Auto alert when you're low on blood");
		setCategory(Category.COMBAT);
		addSetting(health);
		addSetting(leave);
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
	public void onUpdate(TickEvent.PlayerTickEvent event) {

		EntityPlayer player = event.player;
		if (player.world != null && event.side == Side.CLIENT) {
			if(player.isCreative())
				return;
			if (target != null) {

				/* float Damage1=Damage; */
				if (MyDamage < Damage && mc.player.getHealth() < target.getHealth()) {
					if (System.currentTimeMillis() - lastLog < 2000)
						return;
					log();
				}
				if (mc.player.getHealth() - Damage < health.getValue()) {
					if (System.currentTimeMillis() - lastLog < 2000)
						return;
					log();
				}
			}
			if(entity!=null) {
				if (MyDamage < EntityDamage && mc.player.getHealth() < entity.getHealth()) {
					if (System.currentTimeMillis() - lastLog < 2000)
						return;
					log();
				}
				if (mc.player.getHealth() - EntityDamage < health.getValue()) {
					if (System.currentTimeMillis() - lastLog < 2000)
						return;
					log();
				}
			}
			if (mc.player.getHealth() < health.getValue()) {
				if (System.currentTimeMillis() - lastLog < 2000)
					return;
				log();

			}
		}
	}

	@SubscribeEvent
	public void onListen(LivingDamageEvent event) {
		if (mc.player == null)
			return;
		if (event.getEntity() == mc.player) {
			if (mc.player.getHealth() - event.getAmount() < health.getValue()) {
				log();
			}
		}
	}

	private void log() {
		ChatUtils.warning("You are in danger!!!");
		if (leave.isChecked()) {
			Minecraft.getMinecraft().getConnection()
					.handleDisconnect(new SPacketDisconnect(new TextComponentString("AutoLogged")));
		}
		lastLog = System.currentTimeMillis();
	}

	private float getEntityDamage() {
		if (entity != null) {
			ItemStack stack2 = entity.getHeldItemMainhand();
			Item item = stack2.getItem();
			if (item instanceof ItemSword) {
				EntityDamage = ((ItemSword) item).getAttackDamage();
			} else if (item instanceof ItemAxe) {
				EntityDamage = 10.5F;
			}
			if (stack2.hasEffect()) {
				EntityDamage += EnchantmentHelper.getEnchantmentLevel(Enchantments.SHARPNESS, stack2);
			}

		}

		return EntityDamage;

	}

	private float getDamage() {
		if (wurst.getHax().killAuraHack.isEnabled()) {
			if (wurst.getHax().killAuraHack.gettarget() != null) {
				target = wurst.getHax().killAuraHack.gettarget();
				if (target instanceof EntityLivingBase) {
					ItemStack stack1 = target.getHeldItemMainhand();

					Item stack = stack1.getItem();

					if (stack instanceof ItemSword) {
						Damage = ((ItemSword) stack).getAttackDamage();
					} else if (stack instanceof ItemAxe) {
						Damage = 10.5F;
					}
					if (stack1.hasEffect()) {
						Damage += EnchantmentHelper.getEnchantmentLevel(Enchantments.SHARPNESS, stack1);
						ChatUtils.warning("Enemy's damage is" + Damage);
					}
					/*
					 * Damage = ((EntityLivingBase)
					 * target).getHeldItem(EnumHand.MAIN_HAND).getItemDamage();
					 */
				}
			}
		}
		return Damage;
	}

	private float getMyDamage() {
		EntityPlayerSP player = mc.player;
		ItemStack stack = player.getHeldItemMainhand();
		Item item = stack.getItem();
		if (item instanceof ItemSword) {
			Damage = ((ItemSword) item).getAttackDamage();
		} else if (item instanceof ItemAxe) {
			Damage = 10.5F;
		}
		if (stack.hasEffect()) {
			Damage += EnchantmentHelper.getEnchantmentLevel(Enchantments.SHARPNESS, stack);
		}
		return MyDamage;

	}

	private EntityLivingBase getEntity() {
		double rangeSq = Math.pow(4, 2);
		World world = mc.world;
		EntityLivingBase player = mc.player;
		Stream<EntityLivingBase> stream = world.loadedEntityList.parallelStream()
				.filter(e -> e instanceof EntityLivingBase).map(e -> (EntityLivingBase) e)
				.filter(e -> !e.isDead && e.getHealth() > 0).filter(e -> WEntity.getDistanceSq(player, e) <= rangeSq)
				.filter(e -> e != player).filter(e -> !(e instanceof EntityFakePlayer));
		entity = stream.min(Priority.DISTANCE.comparator).orElse(null);
		return entity;

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

	/*
	 * private EntityLivingBase getTarget() { if
	 * (wurst.getHax().killauraHack.isEnabled()) { if
	 * (wurst.getHax().killauraHack.gettarget() != null) { target =
	 * wurst.getHax().killauraHack.gettarget(); } } return target; }
	 */
}
