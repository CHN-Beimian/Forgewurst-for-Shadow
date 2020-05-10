package net.wurstclient.forge.loader;

import java.io.File;
import java.util.ArrayList;
import java.util.Iterator;

import net.minecraftforge.fml.client.FMLClientHandler;
import net.wurstclient.forge.utils.FileOperation;

public class ModFriendsLoader {
	public static final ArrayList friendList = new ArrayList();
	   private static File friendsFile;
	   private static volatile ModFriendsLoader instance;

	   public ModFriendsLoader() {
	      friendsFile = new File(FMLClientHandler.instance().getClient().mcDataDir, "Shadow/FriendsList.txt");
	      friendList.add("huang_bai");
	      friendList.add("Yellow_White");
	      if(!friendsFile.exists()) {
	         friendsFile.getParentFile().mkdirs();

	         try {
	            friendsFile.createNewFile();
	            writeFriendList();
	         } catch (Exception var2) {
	            ;
	         }
	      }

	      loadFriendList();
	   }

	   public static void writeFriendList() {

	      try {
	         StringBuffer var3 = new StringBuffer();
	         Iterator var1 = friendList.iterator();

	         while(var1.hasNext()) {
	            String s = (String)var1.next();
	            if(!s.trim().equals("")) {
	               var3.append(s + "\r\n");
	            }
	         }

	         FileOperation.writeTxtFile(friendsFile.getAbsolutePath(), var3.toString());
	      } catch (Exception var31) {
	         
	      }

	   }

	   public static void loadFriendList() {
	      try {
	         String var5 = FileOperation.readTxtFile(friendsFile.getAbsolutePath());
	       
	         String[] var1 = var5.split("\r\n");
	         int var2 = var1.length;

	         for(int var3 = 0; var3 < var2; ++var3) {
	            String f = var1[var3];
	            if(!f.trim().equals("")) {
	               friendList.add(f);
	              
	            }
	         }
	      } catch (Exception var51) {
	         
	         var51.printStackTrace();
	         writeFriendList();
	      }

	   }

	   public boolean isFriendInList(String s) {
	      return friendList.contains(s);
	   }

	   public static ModFriendsLoader getInstance() {
	      if(instance == null) {
	         instance = new ModFriendsLoader();
	      }

	      return instance;
	   }
}
