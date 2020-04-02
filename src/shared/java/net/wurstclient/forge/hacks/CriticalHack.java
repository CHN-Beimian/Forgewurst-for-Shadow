package net.wurstclient.forge.hacks;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.init.MobEffects;
import net.minecraft.network.play.client.CPacketPlayer;
import net.minecraft.network.play.client.CPacketUseEntity;
import net.minecraft.network.play.server.SPacketPlayerListItem.Action;
import net.minecraft.util.math.RayTraceResult.Type;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.eventhandler.Event.HasResult;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import net.wurstclient.fmlevents.WPacketInputEvent;
import net.wurstclient.fmlevents.WPacketOutputEvent;
import net.wurstclient.fmlevents.WUpdateEvent;
import net.wurstclient.forge.Category;
import net.wurstclient.forge.Hack;
import net.wurstclient.forge.settings.EnumSetting;
import net.wurstclient.forge.settings.SliderSetting;
import net.wurstclient.forge.utils.ChatUtils;
import net.wurstclient.forge.utils.TimerUtil;
import net.wurstclient.forge.utils.Wrapper;

public class CriticalHack extends Hack {
	private final EnumSetting<Mode> mode = new EnumSetting<CriticalHack.Mode>("Mode", Mode.values(), Mode.PACKET);
	TimerUtil timer;
	boolean isAttack;
	boolean cancelSomePackets;

	public CriticalHack() {
		super("Critical", "Changes all your hits to critical hits.");
		setCategory(Category.COMBAT);
		addSetting(mode);
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
	public void onUpdate(TickEvent.PlayerTickEvent event) {
		doCritical();
	}

	public void doCritical() {
		if(mc.player==null)
			return;
		if (!isEnabled())
			return;

		if (!mc.player.onGround)
			return;

		if (mc.player.isInWater() || mc.player.isInLava())
			return;

		switch (mode.getSelected()) {
		case TEST:

			doTestJump();

			break;
		case PACKET:
			doPacketJump();
			break;

		case MINI_JUMP:
			doMiniJump();
			break;

		case FULL_JUMP:
			doFullJump();
			break;
		}
	}

	/*
	 * @SubscribeEvent public boolean onAttack(WPacketOutputEvent event) {
	 * ChatUtils.message("C��"); if(mc.player==null) isAttack=false;
	 * if(mode.getSelected()==Mode.TEST) { if(event.getPacket()instanceof
	 * CPacketPlayer.Position) { CPacketUseEntity attack = (CPacketUseEntity)
	 * event.getPacket(); if(attack.getAction()
	 * ==net.minecraft.network.play.client.CPacketUseEntity.Action.ATTACK) {
	 * isAttack=true;
	 * 
	 * } } } return isAttack;
	 * 
	 * 
	 * 
	 * 
	 * }
	 */

	private void doFullJump() {
		// TODO �Զ����ɵķ������
		mc.player.jump();

	}

	private void doMiniJump() {
		// TODO �Զ����ɵķ������
		mc.player.addVelocity(0, 0.1, 0);
		mc.player.fallDistance = 0.1F;
		mc.player.onGround = false;

	}

	boolean canJump() {
		if (Wrapper.getPlayer().isOnLadder()) {
			return false;
		}
		if (Wrapper.getPlayer().isInWater()) {
			return false;
		}
		if (Wrapper.getPlayer().isInLava()) {
			return false;
		}
		if (Wrapper.getPlayer().isSneaking()) {
			return false;
		}
		if (Wrapper.getPlayer().isRiding()) {
			return false;
		}
		if (Wrapper.getPlayer().isPotionActive(MobEffects.BLINDNESS)) {
			return false;
		}
		return true;
	}

	/*
	 * private void doNewPacket() { double posX = mc.player.posX; double posY =
	 * mc.player.posY; double posZ = mc.player.posZ; double[] v3 = new double[] {
	 * 0.06142999976873398, 0.0, 0.012511000037193298, 0.0 }; int n = v3.length; int
	 * n2 = 0; while (n2 < n) { double offset = v3[n2];
	 * mc.player.connection.sendPacket(new CPacketPlayer.PositionRotation(posX, posY
	 * + offset, posZ, mc.player.rotationYaw, mc.player.rotationPitch, true)); ++n2;
	 * } }
	 */

	private void doPacketJump() {

		double posX = mc.player.posX;
		double posY = mc.player.posY;
		double posZ = mc.player.posZ;

		mc.player.connection.sendPacket(new CPacketPlayer.PositionRotation(posX, posY + 0.0625D, posZ,
				mc.player.rotationYaw, mc.player.rotationPitch, true));
		mc.player.connection.sendPacket(new CPacketPlayer.PositionRotation(posX, posY, posZ, mc.player.rotationYaw,
				mc.player.rotationPitch, false));
		mc.player.connection.sendPacket(new CPacketPlayer.PositionRotation(posX, posY + 1.1E-5D, posZ,
				mc.player.rotationYaw, mc.player.rotationPitch, false));
		mc.player.connection.sendPacket(new CPacketPlayer.PositionRotation(posX, posY, posZ, mc.player.rotationYaw,
				mc.player.rotationPitch, false));
	}

	private void doTestJump() {

		if (mc.objectMouseOver.entityHit == null && wurst.getHax().killauraHack.gettarget() == null
				&& wurst.getHax().tpAura.gettarget() == null&&wurst.getHax().killauraSigmaHack.gettarget()==null) {
			return;
		}

		double posX = mc.player.posX;
		double posY = mc.player.posY;
		double posZ = mc.player.posZ;

		mc.player.connection.sendPacket(new CPacketPlayer.PositionRotation(posX, posY + 0.0625D, posZ,
				mc.player.rotationYaw, mc.player.rotationPitch, true));
		mc.player.connection.sendPacket(new CPacketPlayer.PositionRotation(posX, posY, posZ, mc.player.rotationYaw,
				mc.player.rotationPitch, false));
		mc.player.connection.sendPacket(new CPacketPlayer.PositionRotation(posX, posY + 1.1E-5D, posZ,
				mc.player.rotationYaw, mc.player.rotationPitch, false));
		mc.player.connection.sendPacket(new CPacketPlayer.PositionRotation(posX, posY, posZ, mc.player.rotationYaw,
				mc.player.rotationPitch, false));
		
	}

	private enum Mode {
		PACKET("Packet"), MINI_JUMP("Mini Jump"), FULL_JUMP("Full Jump"), TEST("Test");

		private final String name;

		private Mode(String name) {
			this.name = name;
		}

		public String toString() {
			return name;
		}

	}
}
