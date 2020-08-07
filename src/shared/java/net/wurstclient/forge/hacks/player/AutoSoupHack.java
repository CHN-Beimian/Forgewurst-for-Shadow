package net.wurstclient.forge.hacks.player;

import net.minecraft.client.settings.KeyBinding;
import net.minecraft.init.Items;
import net.minecraft.inventory.ClickType;
import net.minecraft.item.ItemSoup;
import net.minecraft.item.ItemStack;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.eventhandler.Event.HasResult;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.wurstclient.fmlevents.WUpdateEvent;
import net.wurstclient.forge.Category;
import net.wurstclient.forge.Hack;
import net.wurstclient.forge.settings.SliderSetting;
import net.wurstclient.forge.settings.SliderSetting.ValueDisplay;

public class AutoSoupHack extends Hack{
	private final SliderSetting health = new SliderSetting("Health",
			"Eats a soup when your health\n"
				+ "reaches this value or falls below it.",
			6.5, 0.5, 9.5, 0.5, ValueDisplay.DECIMAL);
		
		private int oldSlot = -1;
	public AutoSoupHack() {
		super("AutoSoup","Drink soup automatically when you're short of blood");
		setCategory(Category.COMBAT);
		addSetting(health);
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
		// sort empty bowls
				for(int i = 0; i < 36; i++)
				{
					// filter out non-bowl items and empty bowl slot
					ItemStack stack = mc.player.inventory.getStackInSlot(i);
					if(stack == null || stack.getItem() != Items.BOWL || i == 9)
						continue;
					
					// check if empty bowl slot contains a non-bowl item
					ItemStack emptyBowlStack = mc.player.inventory.getStackInSlot(9);
					boolean swap = !emptyBowlStack.isEmpty()
						&& emptyBowlStack.getItem() != Items.BOWL;
					
					// place bowl in empty bowl slot
					mc.playerController.windowClick(0, i < 9 ? i + 36 : i, 0, ClickType.PICKUP, mc.player);
					mc.playerController.windowClick(0, 9, 0, ClickType.PICKUP, mc.player);
					// place non-bowl item from empty bowl slot in current slot
					if(swap)
					mc.playerController.windowClick(0, i < 9 ? i + 36 : i, 0, ClickType.PICKUP, mc.player);
				}
				int soupInHotbar = findSoup(0, 9);
				
				// check if any soup was found
				if(soupInHotbar != -1)
				{
					// check if player should eat soup
					if(!shouldEatSoup())
					{
						stopIfEating();
						return;
					}
					
					// save old slot
					if(oldSlot == -1)
						oldSlot = mc.player.inventory.currentItem;
					
					// set slot
					mc.player.inventory.currentItem = soupInHotbar;
					
					// eat soup
					KeyBinding.setKeyBindState(mc.gameSettings.keyBindUseItem.getKeyCode(), true);
				
					
					return;
				}
				
				stopIfEating();
				
				// search soup in inventory
				int soupInInventory = findSoup(9, 36);
				
				// move soup in inventory to hotbar
				if(soupInInventory != -1)
				mc.playerController.windowClick(0, soupInInventory, 0, ClickType.QUICK_MOVE, mc.player);
				
				
	}
	private int findSoup(int startSlot, int endSlot)
	{
		for(int i = startSlot; i < endSlot; i++)
		{
			ItemStack stack = mc.player.inventory.getStackInSlot(i);
			
			if(stack != null && stack.getItem() instanceof ItemSoup)
				return i;
		}
		
		return -1;
	}
	private boolean shouldEatSoup()
	{
		// check health
		if(mc.player.getHealth() > health.getValueF() * 2F)
			return false;
		
		return true;
	}
	private void stopIfEating()
	{
		// check if eating
		if(oldSlot == -1)
			return;
		
		// stop eating
		KeyBinding.setKeyBindState(mc.gameSettings.keyBindUseItem.getKeyCode(), false);
		
		// reset slot
		mc.player.inventory.currentItem = oldSlot;
		oldSlot = -1;
	}
}
