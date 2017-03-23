package database;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class DatabaseChunksReceived {

	private static Map<String, Integer> chunksReceivedInfo = new HashMap<String, Integer>();
	private static ArrayList<String> receivedChunksID = new ArrayList<String>();

	public static void StoreReceivedChunkID_Sender(String chunkID, Integer senderID){
		chunksReceivedInfo.put(chunkID, senderID);
	}

	public static Map<String, Integer> getRecievedChunkID_Sender(){
		return chunksReceivedInfo;
	}

	public static ArrayList<String> getReceivedChunksID() {
		return receivedChunksID;
	}

	public static void setReceivedChunksID(String chunkID) {
		receivedChunksID.add(chunkID);
	}
}


