package net.wurstclient.forge.hacks.combat;

import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.init.Items;
import net.minecraft.inventory.ClickType;
import net.minecraft.item.ItemStack;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.wurstclient.fmlevents.WUpdateEvent;
import net.wurstclient.forge.Category;
import net.wurstclient.forge.Hack;
import net.wurstclient.forge.settings.CheckboxSetting;
import net.wurstclient.forge.settings.SliderSetting;

public class AutoTotemHack extends Hack {
	private final CheckboxSetting soft = new CheckboxSetting("Soft", true);
	int totems;
	boolean moving = false;
	boolean returnI = false;

	public AutoTotemHack() {
		super("AutoTotem", "");
		setCategory(Category.COMBAT);
		addSetting(soft);

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
		if (mc.currentScreen instanceof GuiContainer)
			return;
		if (returnI) {
			int t = -1;
			for (int i = 0; i < 45; i++)
				if (mc.player.inventory.getStackInSlot(i).isEmpty()) {
					t = i;
					break;
				}
			if (t == -1)
				return;
			mc.playerController.windowClick(0, t < 9 ? t + 36 : t, 0, ClickType.PICKUP, mc.player);
			returnI = false;
		}
		totems = mc.player.inventory.mainInventory.stream()
				.filter(itemStack -> itemStack.getItem() == Items.TOTEM_OF_UNDYING).mapToInt(ItemStack::getCount).sum();
		if (mc.player.getHeldItemOffhand().getItem() == Items.TOTEM_OF_UNDYING)
			totems++;
		else {
			if (soft.isChecked() && !mc.player.getHeldItemOffhand().isEmpty())
				return;
			if (moving) {
				mc.playerController.windowClick(0, 45, 0, ClickType.PICKUP, mc.player);
				moving = false;
				if (!mc.player.inventory.getItemStack().isEmpty())
					returnI = true;
				return;
			}
			if (mc.player.inventory.getItemStack().isEmpty()) {
				if (totems == 0)
					return;
				int t = -1;
				for (int i = 0; i < 45; i++)
					if (mc.player.inventory.getStackInSlot(i).getItem() == Items.TOTEM_OF_UNDYING) {
						t = i;
						break;
					}
				if (t == -1)
					return; // Should never happen!
				mc.playerController.windowClick(0, t < 9 ? t + 36 : t, 0, ClickType.PICKUP, mc.player);
				moving = true;
			} else if (!soft.isChecked()) {
				int t = -1;
				for (int i = 0; i < 45; i++)
					if (mc.player.inventory.getStackInSlot(i).isEmpty()) {
						t = i;
						break;
					}
				if (t == -1)
					return;
				mc.playerController.windowClick(0, t < 9 ? t + 36 : t, 0, ClickType.PICKUP, mc.player);
			}
		}
	}
}
