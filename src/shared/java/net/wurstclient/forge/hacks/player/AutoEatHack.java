package net.wurstclient.forge.hacks.player;

import java.util.Comparator;

import net.minecraft.client.settings.KeyBinding;
import net.minecraft.item.ItemFood;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumHand;
import net.minecraft.util.FoodStats;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.wurstclient.fmlevents.WUpdateEvent;
import net.wurstclient.forge.Category;
import net.wurstclient.forge.Hack;
import net.wurstclient.forge.compatibility.WMinecraft;
import net.wurstclient.forge.settings.CheckboxSetting;
import net.wurstclient.forge.settings.EnumSetting;

public class AutoEatHack extends Hack {

	public AutoEatHack() {
		super("AutoEat", "Automatically eat when hungry");
		setCategory(Category.PLAYER);
	}

	@Override
	protected void onEnable() {
		if (eating && !mc.player.isHandActive()) {
			if (lastSlot != -1) {
				mc.player.inventory.currentItem = lastSlot;
				lastSlot = -1;
			}
			eating = false;
			KeyBinding.setKeyBindState(mc.gameSettings.keyBindUseItem.getKeyCode(), false);
			return;
		}
		if (eating)
			return;

		FoodStats stats = mc.player.getFoodStats();
		if (isValid(mc.player.getHeldItemOffhand(), stats.getFoodLevel())) {
			mc.player.setActiveHand(EnumHand.OFF_HAND);
			eating = true;
			KeyBinding.setKeyBindState(mc.gameSettings.keyBindUseItem.getKeyCode(), true);

		} else {
			for (int i = 0; i < 9; i++) {
				if (isValid(mc.player.inventory.getStackInSlot(i), stats.getFoodLevel())) {
					lastSlot = mc.player.inventory.currentItem;
					mc.player.inventory.currentItem = i;
					eating = true;
					KeyBinding.setKeyBindState(mc.gameSettings.keyBindUseItem.getKeyCode(), true);

					return;
				}
			}
		}
	}

	@Override
	protected void onDisable() {
		MinecraftForge.EVENT_BUS.unregister(this);
	}

	private int lastSlot = -1;
	private boolean eating = false;

	private boolean isValid(ItemStack stack, int food) {
		return stack.getItem() instanceof ItemFood && (20 - food) >= ((ItemFood) stack.getItem()).getHealAmount(stack);
	}

}