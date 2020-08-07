package net.wurstclient.forge.hacks.misc;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.UUID;

import com.google.common.collect.Ordering;
import com.mojang.authlib.GameProfile;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiPlayerTabOverlay;
import net.minecraft.client.network.NetHandlerPlayClient;
import net.minecraft.client.network.NetworkPlayerInfo;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.wurstclient.fmlevents.WUpdateEvent;
import net.wurstclient.forge.Category;
import net.wurstclient.forge.Hack;
import net.wurstclient.forge.loader.ModBotLoader;
import net.wurstclient.forge.settings.CheckboxSetting;
import net.wurstclient.forge.settings.EnumSetting;
import net.wurstclient.forge.utils.ChatUtils;
import net.wurstclient.forge.utils.EntityUtils;
import net.wurstclient.forge.utils.STimer;

public class AntiBotHack extends Hack {
	
	public static ArrayList<ModBotLoader> bots = new ArrayList<ModBotLoader>();
	
	
	
	public EnumSetting<Mode> mode = new EnumSetting<AntiBotHack.Mode>("Mode", Mode.values(), Mode.Hypixel);
	private CheckboxSetting DEAD = new CheckboxSetting("Dead", true);
	private CheckboxSetting KILLER = new CheckboxSetting("Killer", false);
	ArrayList entities = new ArrayList();
	private STimer timer = new STimer();
	STimer lastRemoved = new STimer();

	private static List invalid = new ArrayList();
	private static List removed = new ArrayList();

	public AntiBotHack() {
		super("AntiBot", "remove bots in your games,but only works on Killaura's M2!");
		setCategory(Category.COMBAT);
		addSetting(DEAD);
		addSetting(KILLER);
		addSetting(mode);
	}

	@Override
	protected void onEnable() {
		invalid.clear();
		MinecraftForge.EVENT_BUS.register(this);
	}

	@Override
	protected void onDisable() {
		invalid.clear();
		MinecraftForge.EVENT_BUS.unregister(this);
	}

	public static List getInvalid() {
		return invalid;
	}

	private enum Mode {
		Hypixel, Packet,M1
	}

	@SubscribeEvent
	public void onUpdate(WUpdateEvent event) {
		double diffX;
		double diffY;
		double diffZ;
		double diffH;
		String name;
		EntityPlayer ent;
		Object o1;
		Iterator entX1;
		if (mode.getSelected() == Mode.Hypixel) {
			if (KILLER.isChecked() && !removed.isEmpty() && this.lastRemoved.delay(1000.0F)) {
				if (removed.size() == 1) {
					ChatUtils.message("Watchdog Killer" + removed.size() + " bot has been removed");
				} else {
					ChatUtils.message("Watchdog Killer" + removed.size() + " bots have been removed");
				}

				this.lastRemoved.reset();
				removed.clear();
			}
			if (!invalid.isEmpty() && this.timer.delay(1000.0F)) {
				invalid.clear();
				this.timer.reset();
			}

			entX1 = mc.world.getLoadedEntityList().iterator();
			while (entX1.hasNext()) {
				o1 = entX1.next();

				if (o1 instanceof EntityPlayer) {
					ent = (EntityPlayer) o1;

					if (ent != mc.player && !invalid.contains(ent)) {

						if (mode.getSelected() == Mode.Hypixel) {
							String formated1 = ent.getDisplayName().getFormattedText();
							String custom1 = ent.getCustomNameTag();
							name = ent.getName();

							if (ent.isInvisible() && !formated1.startsWith("§c") && formated1.endsWith("§r")
									&& custom1.equals(name)) {
								diffX = Math.abs(ent.posX - mc.player.posX);
								diffY = Math.abs(ent.posY - mc.player.posY);
								diffZ = Math.abs(ent.posZ - mc.player.posZ);
								diffH = Math.sqrt(diffX * diffX + diffZ * diffZ);

								if (diffY < 13.0D && diffY > 10.0D && diffH < 3.0D) {
									List list1 = getTabPlayerList();

									if (!list1.contains(ent)) {
										if (KILLER.isChecked() ) {
											this.lastRemoved.reset();
											removed.add(ent);
											mc.world.removeEntity(ent);
										}

										invalid.add(ent);
									}
								}
							}

							if (!formated1.startsWith("§") && formated1.endsWith("§r")) {
								invalid.add(ent);
							}

							if (ent.isInvisible() && !custom1.equalsIgnoreCase("")
									&& custom1.toLowerCase().contains("§c§c") && name.contains("§c")) {
								if (KILLER.isChecked()) {
									this.lastRemoved.reset();
									removed.add(ent);
									mc.world.removeEntity(ent);
								}

								invalid.add(ent);
							}

							if (!custom1.equalsIgnoreCase("") && custom1.toLowerCase().contains("§c")
									&& custom1.toLowerCase().contains("§r")) {
								if (KILLER.isChecked() ) {
									this.lastRemoved.reset();
									removed.add(ent);
									mc.world.removeEntity(ent);
								}

								invalid.add(ent);
							}

							if (formated1.contains("§8[NPC]")) {
								invalid.add(ent);
							}

							if (!formated1.contains("§c") && !custom1.equalsIgnoreCase("")) {
								invalid.add(ent);
							}
						}
					}
				}
			}
		}else if(mode.getSelected()==Mode.M1) {
			
			
			
		}
	}

	/*
	 * if(KILLER.isChecked()) { if (this.timer.delay(400.0F)) {
	 * 
	 * this.timer.reset(); KILLER.setChecked(false); } }else { double diffX; double
	 * diffY; double diffZ; double diffH; if(mode.getSelected()==Mode.Packet) {
	 * 
	 * } } }
	 */
	public static List getTabPlayerList()
    {
		
        NetHandlerPlayClient var4 = mc.player.connection;
        ArrayList list = new ArrayList();
      
		  List players = (List) var4.getPlayerInfoMap();
        Iterator var41 = players.iterator();

        while (var41.hasNext())
        {
            Object o = var41.next();
            NetworkPlayerInfo info = (NetworkPlayerInfo)o;

            if (info != null)
            {
                list.add(mc.world.getPlayerEntityByName(info.getGameProfile().getName()));
            }
        }

        return list;
    }
	public static boolean Bot(EntityPlayer player) {
		for(ModBotLoader bot : bots) {
			if(bot.getName().equals((player.getName()))){
				if(player.isInvisible() != bot.isInvisible()) {
					return player.isInvisible();
				}
				return true;
			}
			else {
				if(bot.getId() == player.getEntityId() 
						|| bot.getUuid().equals(player.getGameProfile().getId())) {
					return true;
				}
			}
		}
		return false;
	}
	public boolean isBot(EntityPlayer bot) {
		if(Bot(bot)) {
			return true;
		}
		if(bot.getGameProfile() == null) {
			return true;
		}
		GameProfile botProfile = bot.getGameProfile();
		if(bot.getUniqueID() == null) {
			return true;
		}
		UUID botUUID = bot.getUniqueID();
		if(botProfile.getName() == null) {
			return true;
		}
		String botName = botProfile.getName();
		if(botName.contains("Body #") || botName.contains("NPC") 
				|| botName.equalsIgnoreCase(EntityUtils.getEntityNameColor(bot))) {
			return true;
		}
		return false;
	}
	public void addBot(EntityPlayer player) {
		if(!isBot(player)) {
			bots.add(new ModBotLoader(player));
		}
	}

}
