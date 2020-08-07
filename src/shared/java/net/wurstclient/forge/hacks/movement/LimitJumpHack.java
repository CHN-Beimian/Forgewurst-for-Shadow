package net.wurstclient.forge.hacks.movement;

import net.minecraft.client.Minecraft;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.wurstclient.fmlevents.WUpdateEvent;
import net.wurstclient.forge.Category;
import net.wurstclient.forge.Hack;
import net.wurstclient.forge.settings.SliderSetting;
import net.wurstclient.forge.settings.SliderSetting.ValueDisplay;

public class LimitJumpHack extends Hack{
	private final SliderSetting speed=new SliderSetting("Speed", 0.1, 0.01, 2, 0.01, ValueDisplay.DECIMAL);
	public LimitJumpHack() {
		super("LimitJump","");
		setCategory(Category.MOVEMENT);
		addSetting(speed);
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
		Minecraft.getMinecraft().player.jumpMovementFactor=speed.getValueF();
	}
	
}
