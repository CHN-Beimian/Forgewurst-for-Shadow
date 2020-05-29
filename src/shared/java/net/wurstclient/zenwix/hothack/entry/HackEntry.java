package net.wurstclient.zenwix.hothack.entry;

import java.util.ArrayList;
import java.util.Iterator;

import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;

public  class HackEntry {
	public static volatile HackEntry instance=new HackEntry();
	
	public ArrayList hacks =new ArrayList();
	public ArrayList activeHacks = new ArrayList();
	
	public void enable(Hack hack) {
		this.hacks.add(hack);
	}
	public void disable(Hack hack) {
		this.hacks.remove(hack);
	}
	
	public void addActive(Hack mod)
	  {
	    this.activeHacks.add(mod);
	  }
	  
	  public void removeActive(Hack mod)
	  {
	    this.activeHacks.remove(mod);
	  }
	public Hack call(Class clazz)
	  {
	    try
	    {
	      Iterator var3 = this.hacks.iterator();
	      while (var3.hasNext())
	      {
	        Hack exception = (Hack)var3.next();
	        if (exception.getClass() == clazz) {
	          return exception;
	        }
	      }
	      return null;
	    }
	    catch (Exception var4)
	    {
	      throw new IllegalStateException("Why you use this for a non-module class?");
	    }
	  }
	
}
