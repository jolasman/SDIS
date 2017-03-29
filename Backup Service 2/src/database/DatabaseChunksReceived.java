package database;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class DatabaseChunksReceived {
	private static ArrayList<String> receivedChunksID = new ArrayList<String>();
	private static ArrayList<String> receivedChunksIDFromCHUNK = new ArrayList<String>();
	

	public static ArrayList<String> getReceivedChunksID() {
		return receivedChunksID;
	}

	public static void setReceivedChunksID(String chunkID) {
		receivedChunksID.add(chunkID);
	}

	public static ArrayList<String> getReceivedChunksIDFromCHUNK() {
		return receivedChunksIDFromCHUNK;
	}

	public static void setReceivedChunksIDFromCHUNK(String chunkID_) {
		receivedChunksIDFromCHUNK.add(chunkID_);
	}
}


