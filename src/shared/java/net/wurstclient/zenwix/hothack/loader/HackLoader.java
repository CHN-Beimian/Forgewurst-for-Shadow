package net.wurstclient.zenwix.hothack.loader;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.NoSuchFileException;
import java.nio.file.Path;
import java.util.Map;
import java.util.Map.Entry;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;

import net.wurstclient.forge.settings.Setting;
import net.wurstclient.forge.utils.JsonUtils;
import net.wurstclient.zenwix.hothack.entry.Hack;
import net.wurstclient.zenwix.hothack.entry.HackEntry;
import net.wurstclient.zenwix.hothack.hacks.combat.KillAura;
import net.wurstclient.zenwix.hothack.hacks.games.BedFucker;
import net.wurstclient.zenwix.hothack.hacks.movement.Fly;
import net.wurstclient.zenwix.hothack.hacks.movement.Speed;
import net.wurstclient.zenwix.hothack.hacks.player.Nofall;
import net.wurstclient.zenwix.hothack.hacks.player.Scaffold;

public class HackLoader {


	public static volatile HackLoader instance = new HackLoader();
	public HackLoader() {
		init();
	}

	  
	  //volatile
	  private static void add(Hack mod)
	  {
	    HackEntry.instance.enable(mod);
	  }
	  
	  public static void init()
	  {
	    add(new Fly());
	    add(new Nofall());
	    add(new KillAura());
	    add(new Speed());
	    add(new BedFucker());
	    add(new Scaffold());
	  }
	  
	
	  public static HackLoader instance() { 
		  return instance; 
	  }
	 
	
	}

