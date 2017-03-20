package fileManagement;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;

import chunks.Chunk;
/*
 * class que divide um determinado ficheiro em chunks de 64000 bytes
 */
public class FileToCkunk {
	private String fileID;
	private int replication_degree = 3;
	private static int sizeOfFiles = 64000;

	@SuppressWarnings("unused")
	public FileToCkunk(File file, String type) throws IOException, NoSuchAlgorithmException {
		int partCounter = 1;	
		byte[] buffer = new byte[sizeOfFiles];

		//sha256 algorithm ****************************************
		MessageDigest md = MessageDigest.getInstance( "SHA-512" );
		md.update( file.getName().getBytes("UTF-8"));
		byte[] aMessageDigest = md.digest();

		fileID = Base64.getEncoder().encodeToString( aMessageDigest );
		//end *************************************************************

		try (BufferedInputStream file_data = new BufferedInputStream(new FileInputStream(file))) {
			int tmp = 0;

			while ((tmp = file_data.read(buffer)) > 0) { //create each chunk while file have some bytes with data

				//Chunk newChunkObject = new Chunk(fileID, partCounter, buffer, replication_degree);
				Chunk newChunkObject = new Chunk(fileID, partCounter, buffer, replication_degree, "./Chunks", type);
				System.out.println("\n\nChunkObject ID : " + newChunkObject.getChunkID());
				System.out.println("ChunkObject No : " + newChunkObject.getChunkNo());
				System.out.println("ChunkObject FileID : " + newChunkObject.getFileID());
				System.out.println("ChunkObject Replication Degree : " + newChunkObject.getReplication_degree());
				System.out.println("ChunkObject FileData : " + newChunkObject.getChunkData());
				//System.out.println("ChunkObject FileDataString : " + newChunkObject.getChunkDataString());
				partCounter++;

			}
		} 
		catch (FileNotFoundException e) {
			System.out.println("Error when we try to get file data");
			e.printStackTrace();

		}
	}

	public String getFileID(){
		return fileID;
	}
}


