package net.wurstclient.hothack.loader;

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
import net.wurstclient.hothack.entry.Hack;
import net.wurstclient.hothack.entry.HackEntry;
import net.wurstclient.hothack.hacks.combat.KillAura;
import net.wurstclient.hothack.hacks.movement.Fly;
import net.wurstclient.hothack.hacks.movement.Speed;
import net.wurstclient.hothack.hacks.player.Nofall;

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
	  }
	  
	
	  public static HackLoader instance() { 
		  return instance; 
	  }
	 
	
	}

