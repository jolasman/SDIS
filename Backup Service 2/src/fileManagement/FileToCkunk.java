package fileManagement;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import algorithms.SHA256;
import chunks.Chunk;
import database.*;
/*
 * class que divide um determinado ficheiro em chunks de 64000 bytes
 */
public class FileToCkunk {
	private String fileID;
	private static int sizeOfFiles = 64000;

	@SuppressWarnings("unused")
	public FileToCkunk(File file, String type, int replication_degree) throws IOException, NoSuchAlgorithmException {

		MakeChunks(file, type , replication_degree);
	}

	public void MakeChunks(File file,String type, int replication_degree) throws IOException{
		int partCounter = 1;	
		byte[] buffer = new byte[sizeOfFiles];

		fileID = SHA256.ToSha256(file); 

		try (BufferedInputStream file_data = new BufferedInputStream(new FileInputStream(file))) {
			int tmp = 0;

			while ((tmp = file_data.read(buffer)) > 0) { //create each chunk while file have some bytes with data

				//Chunk newChunkObject = new Chunk(fileID, partCounter, buffer, replication_degree);
				Chunk newChunkObject = new Chunk(fileID, partCounter, buffer, replication_degree, "./Chunks");
				DatabaseChunksStored.StoreChunkID(fileID + partCounter);
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


