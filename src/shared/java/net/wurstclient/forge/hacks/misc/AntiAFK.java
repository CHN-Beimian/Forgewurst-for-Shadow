package net.wurstclient.forge.hacks.misc;

import java.util.Random;

import org.omg.CORBA.PRIVATE_MEMBER;

import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.network.play.client.CPacketAnimation;
import net.minecraft.util.EnumHand;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.wurstclient.fmlevents.WUpdateEvent;
import net.wurstclient.forge.Category;
import net.wurstclient.forge.Hack;
import net.wurstclient.forge.settings.CheckboxSetting;

public class AntiAFK extends Hack {
	private final CheckboxSetting swing = new CheckboxSetting("Swing", true);
	private final CheckboxSetting turn = new CheckboxSetting("Turn", true);
	 private Random random = new Random();

	public AntiAFK() {
		super("AntiAFK", "Moves in order not to get kicked. (May be invisible client-sided)");
		setCategory(Category.MISC);
		addSetting(swing);
		addSetting(turn);
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
		 if (mc.playerController.getIsHittingBlock()) return;

	        if (mc.player.ticksExisted % 40 == 0 && swing.isChecked())
	            mc.getConnection().sendPacket(new CPacketAnimation(EnumHand.MAIN_HAND));
	        if (mc.player.ticksExisted % 15 == 0 && turn.isChecked())
	            mc.player.rotationYaw = random.nextInt(360) - 180;

	        if (!(swing.isChecked() || turn.isChecked()) && mc.player.ticksExisted % 80 == 0) {
	            mc.player.jump();
	           
	        }
	    
	}
}
