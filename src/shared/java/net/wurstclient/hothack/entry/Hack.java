package net.wurstclient.hothack.entry;

import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.client.settings.KeyBinding;
import net.minecraftforge.fml.client.registry.ClientRegistry;

public abstract class Hack {
	protected int keybored;
	protected KeyBinding key;
	protected boolean enable;
	public static Minecraft mc=Minecraft.getMinecraft();
	public static EntityPlayerSP player=mc.player;
	
	
	public Hack() {
		
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
	      HackEntry.instance.addActive(this);
	    }
	    else
	    {
	      onDisable();
	      HackEntry.instance.removeActive(this);
	    }
	  }
	
	
}
