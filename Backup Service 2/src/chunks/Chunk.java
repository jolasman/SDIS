package chunks;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.ArrayList;

import message.MessageManager;

public class Chunk implements Comparable<Chunk>,Serializable{
	private String chunkID = "";
	private int replication_degree;
	private static int MAX_BYTES = 64000;
	private byte[] chunkData;
	private String fileID;
	private int chunkNo;
	private String path;
	private String type;
	private static ArrayList<Chunk> ChunksCreated = new ArrayList<Chunk>();
	private static ArrayList<Chunk> ChunksRestore = new ArrayList<Chunk>();



	public Chunk(String fileID, int chunkNo, byte[] fileData, int replication_degree) { 

		chunkID += fileID + chunkNo; //ID do Chunk e' o fileId + o chunckNo
		this.chunkData = fileData;
		this.replication_degree = replication_degree;
		this.fileID = fileID;
		this.chunkNo = chunkNo;
	}

	public Chunk(String fileID, int chunkNo, byte[] fileData, int replication_degree, String path){
		chunkID += fileID + chunkNo; //ID do Chunk e' o fileId + o chunckNo
		this.chunkData = fileData;
		this.replication_degree = replication_degree;
		this.fileID = fileID;
		this.chunkNo = chunkNo;
		this.path = path;

		File directory = new File(String.valueOf(path));
		if(! directory.exists()){
			directory.mkdir();
		}
		File newChunk = new File(path, fileID + chunkNo);

		try (ObjectOutputStream out = new ObjectOutputStream (new FileOutputStream(newChunk))) {
			out.writeObject(this); 
			out.close();
			System.out.println("Chunk " + newChunk + " created");
		}
		catch (IOException e) {
			System.out.println("Error when we try to write into a new Chunk");
			e.printStackTrace();
		}
	}

	public Chunk(String fileID, int chunkNo, byte[] fileData, String path){
		chunkID += fileID + chunkNo; //ID do Chunk e' o fileId + o chunckNo
		this.chunkData = fileData;
		this.fileID = fileID;
		this.chunkNo = chunkNo;
		this.path = path;

		File directory = new File(String.valueOf(path));
		if(! directory.exists()){
			directory.mkdir();
		}
		File newChunk = new File(path, fileID + chunkNo);

		try (ObjectOutputStream out = new ObjectOutputStream (new FileOutputStream(newChunk))) {
			out.writeObject(this); 
			out.close();
			System.out.println("Chunk " + newChunk + " created");
		}
		catch (IOException e) {
			System.out.println("Error when we try to write into a new Chunk");
			e.printStackTrace();
		}

	}



	public static ArrayList<Chunk> getChunksCreated() {
		return ChunksCreated;
	}
	public void setChunksCreated(Chunk chunk) {
		ChunksCreated.add(chunk);
	}

	public String getPath() {
		return path;
	}
	public void setPath(String path) {
		this.path = path;
	}
	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
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

	public byte[] getChunkData() {
		return chunkData;
	}

	public String getChunkDataString(){
		return new String(chunkData);
	}

	public void setChunkData(byte[] fileData) {
		this.chunkData = fileData;
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

	public static ArrayList<Chunk> getChunksRestore() {
		return ChunksRestore;
	}

	public static void setChunksRestore(Chunk chunks) {
		ChunksRestore.add(chunks);
	}


	@Override
	public int compareTo(Chunk o){
		int cNo = o.getChunkNo();
		if(getChunkNo() > cNo){
			return 1;
		}else{
			if(cNo > getChunkNo()){
				return -1;
			}else{
				return 0;
			}
		}

	}

}
