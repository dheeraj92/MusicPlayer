package com.musicplayer;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import android.os.Environment;

public class SongsFilter {
	final String MEDIA_PATH = new String("/sdcard/");
	File dir = Environment.getExternalStorageDirectory();
	private ArrayList<HashMap<String, String>> songsList = new ArrayList<HashMap<String, String>>();
	
	public SongsFilter(){
		
	}
	
	public ArrayList<HashMap<String, String>> getPlayList(){
		File home = new File(MEDIA_PATH);
		
		walkDir(home);
		walkDir(dir);
		
		return songsList;
	}
	
	public void walkDir(File dir){
		String pattern=".mp3";
		File listFile[]=dir.listFiles();
		if(listFile!=null){
			for(int i=0;i<listFile.length;i++){
				if(listFile[i].isDirectory()){
					walkDir(listFile[i]);
				}
				else{
					if(listFile[i].getName().endsWith(pattern)){
						HashMap<String, String> song = new HashMap<String, String>();
						song.put("songTitle", listFile[i].getName().substring(0, (listFile[i].getName().length() - 4)));
						song.put("songPath", listFile[i].getPath());
						
						songsList.add(song);
					}
				}
			}
		}
	}
	
	
}
