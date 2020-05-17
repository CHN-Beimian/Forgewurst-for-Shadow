package net.wurstclient.hothack.loader;

import net.wurstclient.hothack.entry.Hack;
import net.wurstclient.hothack.entry.HackEntry;
import net.wurstclient.hothack.hacks.movement.Fly;
import net.wurstclient.hothack.hacks.player.Nofall;

public class HackLoader {
	public static volatile HackLoader instance = new HackLoader();
	public HackLoader() {
	    init();
	}
	  
	  
	  private void add(Hack mod)
	  {
	    HackEntry.instance.enable(mod);
	  }
	  
	  public void init()
	  {
	    add(new Fly());
	    add(new Nofall());
	  }
	  
	  public static HackLoader instance()
	  {
	    return instance;
	  }
	}

