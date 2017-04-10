package database;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class DatabaseChunksStored {
	private static ArrayList<String> chunksStoredInfo = new ArrayList<String>();

	public static void StoreChunkID(String chunkID){
		chunksStoredInfo.add(chunkID);
	}
	
	public static ArrayList<String> getChunkIDStored(){
		return chunksStoredInfo;
	}
}
