package net.wurstclient.forge.loader;

import java.io.File;
import java.util.ArrayList;
import java.util.Iterator;

import net.minecraftforge.fml.client.FMLClientHandler;
import net.wurstclient.forge.utils.FileOperation;

public class ModEnemyLoader {
	public static final ArrayList enemyList = new ArrayList();
	   private static File enemyFile;
	   private static volatile ModEnemyLoader instance;

	   public ModEnemyLoader() {
	      enemyFile = new File(FMLClientHandler.instance().getClient().mcDataDir, "Shadow/EnemyList.txt");
	      enemyList.add("huang_bai");
	      enemyList.add("Yellow_White");
	      if(!enemyFile.exists()) {
	         enemyFile.getParentFile().mkdirs();

	         try {
	            enemyFile.createNewFile();
	            writeEnemyList();
	         } catch (Exception var2) {
	            ;
	         }
	      }

	      loadFriendList();
	   }

	   public static void writeEnemyList() {

	      try {
	         StringBuffer var3 = new StringBuffer();
	         Iterator var1 = enemyList.iterator();

	         while(var1.hasNext()) {
	            String s = (String)var1.next();
	            if(!s.trim().equals("")) {
	               var3.append(s + "\r\n");
	            }
	         }

	         FileOperation.writeTxtFile(enemyFile.getAbsolutePath(), var3.toString());
	      } catch (Exception var31) {
	         
	      }

	   }

	   public static void loadFriendList() {
	      try {
	         String var5 = FileOperation.readTxtFile(enemyFile.getAbsolutePath());
	       
	         String[] var1 = var5.split("\r\n");
	         int var2 = var1.length;

	         for(int var3 = 0; var3 < var2; ++var3) {
	            String f = var1[var3];
	            if(!f.trim().equals("")) {
	               enemyList.add(f);
	              
	            }
	         }
	      } catch (Exception var51) {
	         
	         var51.printStackTrace();
	         writeEnemyList();
	      }

	   }

	   public boolean isEnemyInList(String s) {
	      return enemyList.contains(s);
	   }

	   public static ModEnemyLoader getInstance() {
	      if(instance == null) {
	         instance = new ModEnemyLoader();
	      }

	      return instance;
	   }
}

