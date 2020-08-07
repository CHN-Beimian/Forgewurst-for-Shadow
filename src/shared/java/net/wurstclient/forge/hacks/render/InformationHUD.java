package net.wurstclient.forge.hacks.render;

import java.awt.Color;

import org.lwjgl.opengl.GL11;

import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.settings.GameSettings;
import net.minecraft.entity.EntityLivingBase;
import net.minecraftforge.client.event.RenderGameOverlayEvent.Text;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.eventhandler.Event;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.wurstclient.forge.Category;
import net.wurstclient.forge.Hack;
import net.wurstclient.forge.compatibility.WMinecraft;
import net.wurstclient.forge.settings.CheckboxSetting;
import net.wurstclient.forge.settings.EnumSetting;
import net.wurstclient.forge.utils.ChatUtils;
import net.wurstclient.forge.utils.ColourHolder;
import net.wurstclient.forge.utils.ColourUtils;

public class InformationHUD extends Hack{
	private final EnumSetting<Mode> mode=new EnumSetting<InformationHUD.Mode>("Mode", Mode.values(), Mode.Number);
	public final CheckboxSetting Health =new CheckboxSetting("Health", true);
	public InformationHUD() {
		super("InformationHUD","");
		setCategory(Category.RENDER);
		addSetting(Health);
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
	public void onRenderHealth(Text event) {
		if(mc.player==null)
			return;
		if(!Health.isChecked())
			return;
		if(!wurst.getHax().killAuraHack.isEnabled())
			return;
		if(wurst.getHax().killAuraHack.gettarget()==null)
			return;
		if(mode.getSelected()==Mode.Number) {
		GL11.glPushMatrix();
		int health=(int) wurst.getHax().killAuraHack.gettarget().getHealth();
		/*
		 * WMinecraft.getFontRenderer().drawStringWithShadow("\u00a7l"+health,50f ,120f,
		 * Color.RED.getRGB());
		 */
		EntityLivingBase target =wurst.getHax().killAuraHack.gettarget();
		ScaledResolution sr = new ScaledResolution(mc);
		String P1 = "" + (int) target.getHealth();
        int width = sr.getScaledWidth()/2;
        int height=sr.getScaledHeight()/2;
     
        int P2 = mc.fontRenderer.getStringWidth(P1);
        if (health > 20) {
            health = 20;
        }
        int red = (int) Math.abs((((health * 5) * 0.01f) * 0) + ((1 - (((health * 5) * 0.01f))) * 255));
        int green = (int) Math.abs((((health * 5) * 0.01f) * 255) + ((1 - (((health * 5) * 0.01f))) * 0));
        Color customColor = new Color(red, green, 0).brighter();
        WMinecraft.getFontRenderer().drawStringWithShadow("\u00a7l"+P1, ((-P2 / 2) + width), height - 17, customColor.getRGB());
		GL11.glPopMatrix();
	}else if(mode.getSelected()==Mode.M1) {
		

	}
	}
	private enum Mode{
		Number,M1
	}
	
}
