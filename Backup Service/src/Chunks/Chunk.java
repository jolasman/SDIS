package Chunks;

public class Chunk {

	private String chunkID;

	private int replication_degree;

	public Chunk(String fileID, int chunkN, int replication_degree) { // byte data pode levar?

		//chunkID = fileID + chunkN
		
		this.replication_degree = replication_degree;
		
		
		
		
		
		
		
	}

	public String getChunkID() {
		return chunkID;
	}

	public void setChunkID(String chunkID) {
		this.chunkID = chunkID;
	}

	public int getReplication_degree() {
		return replication_degree;
	}

	public void setReplication_degree(int replication_degree) {
		this.replication_degree = replication_degree;
	}

}
