package net.wurstclient.forge.hacks.player;

import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.client.gui.inventory.GuiChest;
import net.minecraft.inventory.ClickType;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.Slot;
import net.minecraft.network.play.server.SPacketWindowItems;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.wurstclient.fmlevents.WPacketInputEvent;
import net.wurstclient.fmlevents.WUpdateEvent;
import net.wurstclient.forge.Category;
import net.wurstclient.forge.Hack;
import net.wurstclient.forge.settings.SliderSetting;
import net.wurstclient.forge.settings.SliderSetting.ValueDisplay;
import net.wurstclient.forge.utils.Wrapper;

public class ChestStealHack extends Hack{
	private final SliderSetting delay =new SliderSetting("delay", 4, 0, 20, 1, ValueDisplay.DECIMAL);
	private int ticks;
	public ChestStealHack(){
	super("ChestSteal","");
	setCategory(Category.PLAYER);
	addSetting(delay);
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
		EntityPlayerSP player = event.getPlayer();
		if ((!Wrapper.getMinecraft().inGameHasFocus) 
        		&& ((Wrapper.getMinecraft().currentScreen instanceof GuiChest))) {
			if (!isContainerEmpty(player.openContainer)) {
				for (int i = 0; i < player.openContainer.inventorySlots.size() - 36; ++i) {
                    Slot slot = player.openContainer.getSlot(i);
                    if (slot.getHasStack() && slot.getStack() != null) {
                    	if (this.ticks >= delay.getValueI()){
        	            	Wrapper.getMinecraft().playerController.windowClick(player.openContainer.windowId, i, 1, ClickType.QUICK_MOVE, player);
        	            	this.ticks = 0;
        	            }
                    }
                }
				this.ticks += 1;
			} 
		}
	}

	
	boolean isContainerEmpty(Container container) {
		boolean temp = true;
	    int i = 0;
	    for(int slotAmount = container.inventorySlots.size() == 90 ? 54 : 35; i < slotAmount; i++) {
	    	if (container.getSlot(i).getHasStack()) {
	    		temp = false;
	    	}
	    }
	    return temp;
	}
}
