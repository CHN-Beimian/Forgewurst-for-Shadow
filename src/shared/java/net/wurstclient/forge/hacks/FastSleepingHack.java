package net.wurstclient.forge.hacks;

import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.network.play.client.CPacketEntityAction;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.wurstclient.fmlevents.WUpdateEvent;
import net.wurstclient.forge.Category;
import net.wurstclient.forge.Hack;

public class FastSleepingHack extends Hack{
	public FastSleepingHack() {
		super("FastSleeping","Make you sleep faster");
		setCategory(Category.PLAYER);
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
		EntityPlayerSP player=event.getPlayer();
		if(player.isPlayerSleeping()) {
			if(mc.player.getSleepTimer()>10) {
			player.connection.sendPacket(new CPacketEntityAction(player,CPacketEntityAction.Action.STOP_SLEEPING));
			}
		}
	}
	
}
