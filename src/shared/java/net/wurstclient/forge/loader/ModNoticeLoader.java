package net.wurstclient.forge.loader;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;

public class ModNoticeLoader {
	public static List<String> sentences = new ArrayList<String>();
    public ModNoticeLoader(){
        try {
            init();
        }catch (IOException exception){
            exception.printStackTrace();
        }
    }
    public void init()throws IOException{
        saveDefaultConfig();
        File file = new File("Shadow/notice.txt");
        BufferedReader br = new BufferedReader(new FileReader(file));
        String str;
        while((str = br.readLine())!=null){
            sentences.add(str);
        }
        br.close();
    }
    public void saveDefaultConfig()throws IOException{
        File file = new File("Shadow/notice.txt");
        if(file.exists())return;
        file.createNewFile();
        OutputStream os = new FileOutputStream(file);
        BufferedOutputStream bos = new BufferedOutputStream(os);
        InputStream is = ModMessageLoader.class.getResourceAsStream("Shadow/notice.txt");
        BufferedInputStream bis = new BufferedInputStream(is);
        byte[] b = new byte[1024];
        int len;
        while((len = bis.read(b))!=-1){
            bos.write(b,0,len);
            bos.flush();
        }
        bis.close();
        is.close();
        bos.close();
        os.close();
    }
}
