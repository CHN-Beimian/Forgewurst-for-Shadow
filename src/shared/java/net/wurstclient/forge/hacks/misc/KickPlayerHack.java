package net.wurstclient.forge.hacks.misc;

import net.minecraft.client.multiplayer.ServerData;
import net.minecraftforge.common.MinecraftForge;
import net.wurstclient.forge.Category;
import net.wurstclient.forge.Hack;
import net.wurstclient.forge.commands.UnlockCmd;
import net.wurstclient.forge.utils.ChatUtils;
import net.wurstclient.forge.utils.KickUtils;
import net.wurstclient.forge.utils.system.Frame;

public class KickPlayerHack extends Hack{
	public static int AttackStart;
	public KickPlayerHack() {
		super("KickPlayer","Kick players");
		setCategory(Category.MISC);
	}

	@Override
	protected void onEnable() {
		if(UnlockCmd.LOCK==true) {
			Frame.main(null);
		}else {
			if(mc.player!=null)
			ChatUtils.error("You do not have permission to use this feature");
			this.setEnabled(false);
		}
	/*
	 * ServerData serverData = mc.getCurrentServerData(); if(serverData==null){
	 * ChatUtils.error("This player is not on the same server as you!");
	 * 
	 * }else{ String[] ip=serverData.serverIP.split(":"); if(ip.length != 2){
	 * ChatUtils.error("ip error"); }else{ AttackStart=1; ChatUtils.
	 * message("Start the attack, stop this feature or exit the server to stop");
	 * new KickUtils(ip[0],Integer.parseInt( ip[1]));
	 * 
	 * } } }
	 */
	}
	@Override
	protected void onDisable() {
		MinecraftForge.EVENT_BUS.unregister(this);
	}
	
}
