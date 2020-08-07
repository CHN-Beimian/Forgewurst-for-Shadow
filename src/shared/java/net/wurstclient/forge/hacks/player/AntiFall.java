package net.wurstclient.forge.hacks.player;

import net.minecraft.block.BlockAir;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.wurstclient.fmlevents.WUpdateEvent;
import net.wurstclient.forge.Category;
import net.wurstclient.forge.Hack;
import net.wurstclient.forge.utils.ChatUtils;

public class AntiFall extends Hack{
	long lastLog = System.currentTimeMillis();
	int time=0;
	public AntiFall() {
		super("Back","When you fall into the void, return to the original place");
		setCategory(Category.PLAYER);
	}

	@Override
	protected void onEnable() {
		 time=0;
		MinecraftForge.EVENT_BUS.register(this);
	}

	@Override
	protected void onDisable() {
		MinecraftForge.EVENT_BUS.unregister(this);
	}
	@SubscribeEvent
	public void onUpdate(WUpdateEvent event) {
	
		if(wurst.getHax().glideHack.isEnabled()||wurst.getHax().superFly.isEnabled()) {
			if (System.currentTimeMillis() - lastLog < 2000)
				return;
			ChatUtils.warning("BACK is automatically closed during flight");
			lastLog = System.currentTimeMillis();
			return;
		}
		if(isBlockBelow()||mc.player.onGround||mc.player.motionY>=0) {
			return;
		}
		if(mc.player.fallDistance*2.0f>=5.0&&!mc.player.onGround) {
			mc.player.motionY=2;
			mc.player.fallDistance=0.0f;
		}
	}
	private Boolean isBlockBelow() {
		for(int i=(int) (mc.player.posY-1.0);i>0;--i) {
			final BlockPos pos=new BlockPos(mc.player.posX, i, mc.player.posZ);
			if(!(mc.world.getBlockState(pos).getBlock() instanceof BlockAir)) {
				return true;
			}
		}
		return false;
		
	}
}
