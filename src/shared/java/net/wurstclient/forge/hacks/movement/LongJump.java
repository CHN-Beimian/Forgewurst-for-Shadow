package net.wurstclient.forge.hacks.movement;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import net.minecraft.network.Packet;
import net.minecraft.network.play.client.CPacketAnimation;
import net.minecraft.network.play.client.CPacketEntityAction;
import net.minecraft.network.play.client.CPacketPlayer;
import net.minecraft.network.play.client.CPacketPlayerTryUseItemOnBlock;
import net.minecraft.network.play.client.CPacketUseEntity;
import net.minecraft.network.play.client.CPacketVehicleMove;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.wurstclient.fmlevents.WPacketOutputEvent;
import net.wurstclient.fmlevents.WUpdateEvent;
import net.wurstclient.forge.Category;
import net.wurstclient.forge.Hack;
import net.wurstclient.forge.settings.SliderSetting;
import net.wurstclient.forge.settings.SliderSetting.ValueDisplay;
import net.wurstclient.forge.utils.MoveUtils;
import net.wurstclient.forge.utils.PlayerControllerUtils;
import net.wurstclient.forge.utils.Wrapper;

public class LongJump extends Hack {
	private final List<Packet> packets = new ArrayList<>();
	private final LinkedList<double[]> positions = new LinkedList<>();
	private boolean disableLogger;

	private final SliderSetting length =new SliderSetting("Length", 5, 2, 10, 1, ValueDisplay.DECIMAL);
		
	
	public LongJump() {
		super("LongJump", "Jump more further");
		setCategory(Category.MOVEMENT);
		addSetting(length);
	}

	@Override
	protected void onEnable() {
		Wrapper.checknull();
		synchronized (positions) {
			positions.add(new double[] { mc.player.posX,
					mc.player.getEntityBoundingBox().minY + (mc.player.getEyeHeight() / 2), mc.player.posZ });
			positions.add(new double[] { mc.player.posX, mc.player.getEntityBoundingBox().minY, mc.player.posZ });
		}
		MinecraftForge.EVENT_BUS.register(this);
	}

	@Override
	protected void onDisable() {
		if (mc.player == null)
			return;

		blink();
		MinecraftForge.EVENT_BUS.unregister(this);
	}

	@SubscribeEvent
	public void onUpdate(WUpdateEvent event) {
		Wrapper.checknull();
		synchronized (positions) {
			positions.add(
					new double[] { mc.player.posX, mc.player.getEntityBoundingBox().minY, mc.player.posZ });
		}

		if (PlayerControllerUtils.isMoving()) {
			if (mc.player.onGround) {
				MoveUtils.strafeHYT(length.getValueF());
				mc.player.motionY = 0.42F;
			}
			MoveUtils.strafeHYT(length.getValueF());
		} else {
			mc.player.motionX = mc.player.motionZ = 0D;
		}
		this.setEnabled(false);
	}

	@SubscribeEvent
	public void onPacket(WPacketOutputEvent event) {
		if (!(mc.player == null || disableLogger)) {

			if (event.getPacket() instanceof CPacketPlayer) {
				event.setCanceled(true);
			}
			if (event.getPacket() instanceof CPacketPlayer.Position
					|| event.getPacket() instanceof CPacketPlayer.Rotation
					|| event.getPacket() instanceof CPacketPlayerTryUseItemOnBlock
					|| event.getPacket() instanceof CPacketAnimation || event.getPacket() instanceof CPacketEntityAction
					|| event.getPacket() instanceof CPacketUseEntity) {

				packets.add((Packet) event.getPacket());
				event.setCanceled(true);
			}
		}
	}

	private void blink() {
		try {
			disableLogger = true;

			final Iterator<Packet> packetIterator = packets.iterator();
			for (; packetIterator.hasNext();) {
				Wrapper.sendPacket(packetIterator.next());
				packetIterator.remove();
			}

			disableLogger = false;
		} catch (final Exception e) {
			e.printStackTrace();
			disableLogger = false;
		}

		synchronized (positions) {
			positions.clear();
		}
	}

}
