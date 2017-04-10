package database;

import java.util.ArrayList;

public class DatabasePeerID {

		public static ArrayList<Integer> peerIDs = new ArrayList<Integer>();
		
		public static void StorePeerID(int peerID){
			peerIDs.add(peerID);
		}
		
		public static ArrayList<Integer> getPeerIDStored(){
			return peerIDs;
		}
		
}
