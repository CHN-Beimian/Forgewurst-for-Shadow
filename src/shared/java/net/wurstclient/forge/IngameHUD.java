/*
 * Copyright (C) 2017 - 2019 | Wurst-Imperium | All rights reserved.
 *
 * This source code is subject to the terms of the GNU General Public
 * License, version 3. If a copy of the GPL was not distributed with this
 * file, You can obtain one at: https://www.gnu.org/licenses/gpl-3.0.txt
 */
package net.wurstclient.forge;

import java.awt.Color;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Date;

import org.lwjgl.opengl.GL11;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.client.event.RenderGameOverlayEvent.ElementType;
import net.minecraftforge.fml.client.FMLClientHandler;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.wurstclient.forge.clickgui.ClickGui;
import net.wurstclient.forge.clickgui.ClickGuiScreen;
import net.wurstclient.forge.compatibility.WMinecraft;
import net.wurstclient.forge.utils.management.FontManager;

public final class IngameHUD {
	public static FontManager fm = new FontManager();
	private final Minecraft mc = Minecraft.getMinecraft();
	private final HackList hackList;
	private final ClickGui clickGui;
	SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss");

	public IngameHUD(HackList hackList, ClickGui clickGui) {
		this.hackList = hackList;
		this.clickGui = clickGui;
	}

	@SubscribeEvent
	public void onRenderGUI(RenderGameOverlayEvent.Post event) {
		if (event.getType() != ElementType.ALL || mc.gameSettings.showDebugInfo)
			return;

		boolean blend = GL11.glGetBoolean(GL11.GL_BLEND);

		// color
		clickGui.updateColors();
		int textColor;
		if (hackList.rainbowUiHack.isEnabled()) {
			float[] acColor = clickGui.getAcColor();
			textColor = (int) (acColor[0] * 256) << 16 | (int) (acColor[1] * 256) << 8 | (int) (acColor[2] * 256);
		} else
			textColor = 0xffffff;

		// title
		GL11.glPushMatrix();
		/*
		 * GL11.glScaled(1.8, 1.8, 1);// 1.33333333
		 */	
		 final Color color = Color.getHSBColor(255.0f, 0.6f, 1.0f);
		            final int c = color.getRGB();
		            int renderColor = 0;
		            renderColor = c;

		
		  WMinecraft.getFontRenderer().drawStringWithShadow("[" +
		  sdf.format(new Date()) + "]", 60, 2f, c);
		  fm.getFont("SFB 11").drawStringWithShadow("HURRICANE" , 1, 1, c);
		/*
		 * final ResourceLocation logo =new
		 * ResourceLocation(ForgeWurst.MODID,"logo.png");
		 * mc.getTextureManager().bindTexture(logo); ScaledResolution sr = new
		 * ScaledResolution(mc); int x = sr.getScaledWidth() / 2 + 44; int y1 =
		 * sr.getScaledHeight() - 51; int w = 200; int h = 100;
		 * Gui.drawModalRectWithCustomSizedTexture(x, y1, 0, 0, w, h, w, h);
		 */
		GL11.glPopMatrix();

		// hack list
		int y = 19;
		ArrayList<Hack> hacks = new ArrayList<>();
		hacks.addAll(hackList.getValues());
		hacks.sort(Comparator.comparing(Hack::getName));

		for (Hack hack : hacks) {
			if (!hack.isEnabled())
				continue;
			WMinecraft.getFontRenderer().drawStringWithShadow(hack.getRenderName(), 2, y,textColor);
			/*
			 * fm.getFont("SFB 7").drawStringWithShadow(hack.getRenderName(), 2, y,
			 * textColor);
			 */
			y += 9;
		}

		// pinned windows
		if (!(mc.currentScreen instanceof ClickGuiScreen))
			clickGui.renderPinnedWindows(event.getPartialTicks());

		if (blend)
			GL11.glEnable(GL11.GL_BLEND);
		else
			GL11.glDisable(GL11.GL_BLEND);
	}
}
