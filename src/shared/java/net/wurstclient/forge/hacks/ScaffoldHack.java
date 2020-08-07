package net.wurstclient.forge.hacks;

import java.lang.reflect.Field;

import com.google.common.eventbus.Subscribe;

import io.netty.handler.codec.http2.Http2FrameLogger.Direction;
import net.minecraft.block.Block;
import net.minecraft.block.BlockAir;
import net.minecraft.block.BlockContainer;
import net.minecraft.block.BlockFalling;
import net.minecraft.block.state.IBlockState;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.wurstclient.fmlevents.WUpdateEvent;
import net.wurstclient.forge.Category;
import net.wurstclient.forge.Hack;
import net.wurstclient.forge.settings.EnumSetting;
import net.wurstclient.forge.settings.SliderSetting;
import net.wurstclient.forge.settings.SliderSetting.ValueDisplay;
import net.wurstclient.forge.utils.BlockInteractionHelper;
import net.wurstclient.forge.utils.BlockUtils;
import net.wurstclient.forge.utils.EntityUtils;
import net.wurstclient.forge.utils.Wrapper;

public class ScaffoldHack extends Hack {
	private final EnumSetting<Mode> mode=new EnumSetting<ScaffoldHack.Mode>("Mode", Mode.values(), Mode.Wurst);
	BlockInteractionHelper blockhelper = new BlockInteractionHelper();
	private final SliderSetting future = new SliderSetting("future", 2, 0, 60, 2, ValueDisplay.DECIMAL);

	public ScaffoldHack() {
		super("Scaffold", "Automatically place blocks under your feet");
	
		setCategory(Category.PLAYER);
		addSetting(future);
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
	public void onUpdate(WUpdateEvent event) {
		if(mode.getSelected()==Mode.M1) {
		try
		{
			Field rightClickDelayTimer =
				mc.getClass().getDeclaredField(wurst.isObfuscated()
					? "field_71467_ac" : "rightClickDelayTimer");
			rightClickDelayTimer.setAccessible(true);
			rightClickDelayTimer.setInt(mc, 0);
			
		}catch(ReflectiveOperationException e)
		{
			setEnabled(false);
			throw new RuntimeException(e);
		}
		if (mc.player == null)
			return;
		Vec3d vec3d = EntityUtils.getInterpolatedPos(mc.player, future.getValueF());
		BlockPos blockPos = new BlockPos(vec3d).down();
		BlockPos belowBlockPos = blockPos.down();

		// check if block is already placed
		if (!Wrapper.getWorld().getBlockState(blockPos).getMaterial().isReplaceable())
			return;

		// search blocks in hotbar
		int newSlot = -1;
		for (int i = 0; i < 9; i++) {
			// filter out non-block items
			ItemStack stack = Wrapper.getPlayer().inventory.getStackInSlot(i);

			if (stack == ItemStack.EMPTY || !(stack.getItem() instanceof ItemBlock)) {
				continue;
			}
			Block block = ((ItemBlock) stack.getItem()).getBlock();
			if (block instanceof BlockContainer) {
				continue;
			}

			// filter out non-solid blocks
			if (!Block.getBlockFromItem(stack.getItem()).getDefaultState().isFullBlock())
				continue;

			// don't use falling blocks if it'd fall
			if (((ItemBlock) stack.getItem()).getBlock() instanceof BlockFalling) {
				if (Wrapper.getWorld().getBlockState(belowBlockPos).getMaterial().isReplaceable())
					continue;
			}

			newSlot = i;
			break;
		}

		// check if any blocks were found
		if (newSlot == -1)
			return;

		// set slot
		int oldSlot = Wrapper.getPlayer().inventory.currentItem;
		Wrapper.getPlayer().inventory.currentItem = newSlot;

		// check if we don't have a block adjacent to blockpos
		if (!blockhelper.checkForNeighbours(blockPos)) {
			return;
		}

		// place block
		blockhelper.placeBlockScaffold(blockPos);

		// reset slot
		Wrapper.getPlayer().inventory.currentItem = oldSlot;
		if(mc.gameSettings.keyBindJump.isKeyDown()) {
			mc.player.motionY=0.37;
		}
		
		if(mc.player.onGround) {
			if(mc.gameSettings.keyBindJump.isKeyDown()) {
				mc.player.motionY=0.4;
			}
			
		if(mc.gameSettings.keyBindSneak.isKeyDown()) {
			
			
			Vec3d vec=new Vec3d(0, 1, 0);
			BlockPos blp=new BlockPos(mc.player.posX, mc.player.posY-1, mc.player.posZ);
			blockhelper.placeBlockScaffold(blp);
			mc.player.swingArm(EnumHand.MAIN_HAND);
		}
		}
		}else if(mode.getSelected()==Mode.Wurst) {
			
		
		}
	}
	private enum Mode{
		M1,Wurst
	}
	
}
