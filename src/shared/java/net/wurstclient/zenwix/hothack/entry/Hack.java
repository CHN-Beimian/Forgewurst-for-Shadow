package net.wurstclient.zenwix.hothack.entry;

import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.fml.client.registry.ClientRegistry;
import net.wurstclient.forge.ForgeWurst;
import net.wurstclient.forge.utils.ChatUtils;


public abstract class Hack {
	private static Configuration configuration;
	protected int keybored;
	protected KeyBinding key;
	protected boolean enable;
	public static Minecraft mc=Minecraft.getMinecraft();
	public static EntityPlayerSP player=mc.player;
	public  final String name;
	public  final String desc;
	
	public Hack(String name,String desc) {
		this.name=name;
		this.desc=desc;
	}
	public String getName()
	  {
	    return this.name;
	  }
	public void setKeyBored(int keyBinding) {
		this.keybored=keyBinding;
	}
	public void setKey(KeyBinding keyBinding) {
		this.key=keyBinding;
		ClientRegistry.registerKeyBinding(keyBinding);
	}
	public KeyBinding getKey() {
		return this.key;
	}
	public int getKeyBored() {
		return this.keybored;
	}
	public boolean isEnable() {
		return this.enable;
	}
	public void onRenderIngame() {}
	public void onWorldRender() {}
	public void onTicks() {}
	public void onUpdate() {}
	public void onTick() {}
	
	public void onEnable() {}
	public void onDisable() {}
	
	public void reset() {
		onEnable();
		onDisable();
		HackEntry.instance.addActive(this);
	}
	
	public void on() {
		this.enable=true;
		onEnable();
		HackEntry.instance.removeActive(this);
	}
	
	public void off() {
		this.enable=false;
		onDisable();
	}

	 public void toggle()
	  {
	    this.enable = (!this.enable);
	    if (isEnable())
	    {
	      onEnable();
	      ChatUtils.message("\u00a7l"+getName()+"  has been enabled");
	      HackEntry.instance.addActive(this);
	    }
	    else
	    {
	      onDisable();
	      ChatUtils.message("\u00a7l"+getName()+"  has been disabled");
	      HackEntry.instance.removeActive(this);
	    }
	  }
	
	
}
