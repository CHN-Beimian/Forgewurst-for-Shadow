package net.wurstclient.forge.hacks;

import java.util.Comparator;
import java.util.Random;
import java.util.function.ToDoubleFunction;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EnumPlayerModelParts;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.wurstclient.fmlevents.WUpdateEvent;
import net.wurstclient.forge.Category;
import net.wurstclient.forge.Hack;
import net.wurstclient.forge.compatibility.WEntity;
import net.wurstclient.forge.compatibility.WMinecraft;
import net.wurstclient.forge.settings.EnumSetting;
import net.wurstclient.forge.settings.SliderSetting;
import net.wurstclient.forge.settings.SliderSetting.ValueDisplay;
import net.wurstclient.forge.utils.RotationUtils;

public class SkinDerpHack extends Hack {
	private final EnumSetting<Mode> mode = new EnumSetting<>("Mode", Mode.values(), Mode.HORIZONTAL);
	private final SliderSetting slowness = new SliderSetting("slowness", 2, 1, 10, 1, ValueDisplay.DECIMAL);
	private final static EnumPlayerModelParts[] PARTS_HORIZONTAL = new EnumPlayerModelParts[] {
			EnumPlayerModelParts.LEFT_SLEEVE, EnumPlayerModelParts.JACKET, EnumPlayerModelParts.HAT,
			EnumPlayerModelParts.LEFT_PANTS_LEG, EnumPlayerModelParts.RIGHT_PANTS_LEG,
			EnumPlayerModelParts.RIGHT_SLEEVE };

	private final static EnumPlayerModelParts[] PARTS_VERTICAL = new EnumPlayerModelParts[] { EnumPlayerModelParts.HAT,
			EnumPlayerModelParts.JACKET, EnumPlayerModelParts.LEFT_SLEEVE, EnumPlayerModelParts.RIGHT_SLEEVE,
			EnumPlayerModelParts.LEFT_PANTS_LEG, EnumPlayerModelParts.RIGHT_PANTS_LEG, };

	private Random r = new Random();
	private int len = EnumPlayerModelParts.values().length;

	public SkinDerpHack() {
		super("SkinDerp", "Randomly toggles parts of your skin.");
		setCategory(Category.MISC);
		addSetting(mode);
		addSetting(slowness);
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
		switch (mode.getSelected()) {
		case RANDOM:
			if (mc.player.ticksExisted % slowness.getValue() != 0)
				return;
			mc.gameSettings.switchModelPartEnabled(EnumPlayerModelParts.values()[r.nextInt(len)]);
			break;
		case VERTICAL:
		case HORIZONTAL:
			int i = (int) ((mc.player.ticksExisted / slowness.getValue()) % (PARTS_HORIZONTAL.length * 2)); // *2 for on/off
			boolean on = false;
			if (i >= PARTS_HORIZONTAL.length) {
				on = true;
				i -= PARTS_HORIZONTAL.length;
			}
			mc.gameSettings.setModelPartEnabled(
					mode.getSelected() == mode.getSelected().VERTICAL ? PARTS_VERTICAL[i] : PARTS_HORIZONTAL[i], on);

		}

	}

	public enum Mode {
		RANDOM, VERTICAL, HORIZONTAL
	}

}
