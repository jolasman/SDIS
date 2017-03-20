package Chunks;

public class Chunk {

	private String chunkID = "";
	private int replication_degree;
	private static int MAX_BYTES = 64000;
	private byte[] fileData;
	private String fileID;
	private int chunkNo;

	public Chunk(String fileID, int chunkNo, byte[] fileData, int replication_degree) { 

		chunkID += fileID + chunkNo; //ID do Chunk e' o fileId + o chunckNo
		
		this.fileData = fileData;
		this.replication_degree = replication_degree;
		this.fileID = fileID;
		this.chunkNo = chunkNo;
				
	}

	public String getChunkID() {
		return chunkID;
	}

	public void setChunkID(String chunkID) {
		this.chunkID = chunkID;
	}

	public static int getMAX_BYTES() {
		return MAX_BYTES;
	}

	public byte[] getFileData() {
		return fileData;
	}

	public void setFileData(byte[] fileData) {
		this.fileData = fileData;
	}

	public String getFileID() {
		return fileID;
	}

	public void setFileID(String fileID) {
		this.fileID = fileID;
	}

	public int getChunkNo() {
		return chunkNo;
	}

	public void setChunkNo(int chunkNo) {
		this.chunkNo = chunkNo;
	}

	public int getReplication_degree() {
		return replication_degree;
	}

	public void setReplication_degree(int replication_degree) {
		this.replication_degree = replication_degree;
	}

}
