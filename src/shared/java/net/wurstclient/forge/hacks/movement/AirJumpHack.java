package net.wurstclient.forge.hacks.movement;

import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.client.FMLClientHandler;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.wurstclient.fmlevents.WUpdateEvent;
import net.wurstclient.forge.Category;
import net.wurstclient.forge.Hack;
import net.wurstclient.forge.settings.EnumSetting;
import net.wurstclient.forge.settings.SliderSetting;
import net.wurstclient.forge.settings.SliderSetting.ValueDisplay;

public class AirJumpHack extends Hack{
	private final EnumSetting<Mode> mode=new EnumSetting<AirJumpHack.Mode>("Mode", Mode.values(), Mode.M1);
private SliderSetting height =new SliderSetting("Height", 0.5D, 0.25D, 10D, 0.05, ValueDisplay.DECIMAL);
	public AirJumpHack() {
		super("AirJump", "Makes you jump higher");
		setCategory(Category.MOVEMENT);
		addSetting(height);
		addSetting(mode);
	}
	@Override
	protected void onEnable() {
		if(mode.getSelected()==Mode.M1) {
			if(mc.player==null)
				return;
			EntityPlayerSP player = mc.player;
			player.motionY=height.getValue();
		}
		MinecraftForge.EVENT_BUS.register(this);
		
	}
	@Override
	protected void onDisable() {
		if(mode.getSelected()==Mode.M1) {
			if(mc.player==null)
				return;
			EntityPlayerSP player = mc.player;
			player.motionY=height.getValue();
		}
		MinecraftForge.EVENT_BUS.unregister(this);
	}
	@SubscribeEvent
	public void onUpdate(WUpdateEvent event)
	{
		if(mode.getSelected()==Mode.ENABLE) {
			EntityPlayerSP player = event.getPlayer();
			player.motionY=height.getValue();
		}
		
	}
	private enum Mode{
		ENABLE,M1
	}

}
