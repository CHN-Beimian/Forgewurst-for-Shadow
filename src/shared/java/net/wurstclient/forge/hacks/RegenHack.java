package net.wurstclient.forge.hacks;

import com.google.common.base.Ticker;

import net.minecraft.block.material.Material;
import net.minecraft.network.play.client.CPacketPlayer;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.client.FMLClientHandler;
import net.minecraftforge.fml.common.MinecraftDummyContainer;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import net.wurstclient.fmlevents.WUpdateEvent;
import net.wurstclient.forge.Category;
import net.wurstclient.forge.Hack;
import net.wurstclient.forge.settings.SliderSetting;
import net.wurstclient.forge.settings.SliderSetting.ValueDisplay;

public class RegenHack extends Hack{
	private final SliderSetting health=new SliderSetting("Health", 7, 0.5, 10, 1, ValueDisplay.DECIMAL);
	public RegenHack() {
		super("Regen","Makes you return blood faster");
		setCategory(Category.PLAYER);
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
		if(mc.player==null)
			return;
		if (mc.player.getHealth() < health.getValue() * 2 && mc.player.getFoodStats().getFoodLevel() > 16 && !mc.gameSettings.keyBindUseItem.isPressed()&& mc.player.collidedVertically &&
                mc.player.onGround && !mc.gameSettings.keyBindJump.isPressed() && (!mc.player.isInsideOfMaterial(Material.LAVA)) &&
                !mc.player.isInWater()) {
            for (int i = 0; i < 40; i++) {
            	mc.player.connection.sendPacket(new CPacketPlayer());
            }
        }
	}
}
