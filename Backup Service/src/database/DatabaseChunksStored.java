package database;

import java.util.HashMap;
import java.util.Map;

public class DatabaseChunksStored {

	public static Map<String, Integer> chunksStoredInfo = new HashMap<String, Integer>();

	
	public static void StoreChunkID(String chunkID, Integer senderID){
		chunksStoredInfo.put(chunkID, senderID);
	}
	
	public static Map<String, Integer> getChunkIDStored(){
		return chunksStoredInfo;
	}
}
