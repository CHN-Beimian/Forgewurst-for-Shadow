package net.wurstclient.zenwix.hothack.hacks.player;

import java.lang.reflect.Field;

import org.lwjgl.input.Keyboard;

import net.minecraft.block.Block;
import net.minecraft.block.BlockContainer;
import net.minecraft.block.BlockFalling;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.wurstclient.forge.utils.BlockInteractionHelper;
import net.wurstclient.forge.utils.EntityUtils;
import net.wurstclient.forge.utils.Wrapper;
import net.wurstclient.zenwix.hothack.entry.Hack;

public class Scaffold extends Hack {
	
	BlockInteractionHelper blockhelper = new BlockInteractionHelper();
	public Scaffold() {
		super("Scaffold", "Automatically place blocks under your feet");
		setKey(new KeyBinding("Shadow.Scaffold", Keyboard.KEY_M, "Shadow"));
	}

	@Override
	public void onTicks() {
		try {
			Field rightClickDelayTimer = mc.getClass().getDeclaredField("field_71467_ac");
			rightClickDelayTimer.setAccessible(true);
			rightClickDelayTimer.setInt(mc, 0);

		} catch (ReflectiveOperationException e) {
			off();
			throw new RuntimeException(e);
		}
		if (mc.player == null)
			return;
		Vec3d vec3d = EntityUtils.getInterpolatedPos(mc.player, 0);
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
		if (mc.player.onGround) {
			if (mc.gameSettings.keyBindSneak.isKeyDown()) {
				Vec3d vec = new Vec3d(0, 1, 0);
				BlockPos blp = new BlockPos(mc.player.posX, mc.player.posY - 1, mc.player.posZ);
				blockhelper.placeBlockScaffold(blp);
				mc.player.swingArm(EnumHand.MAIN_HAND);
			}
		}

	}
}
