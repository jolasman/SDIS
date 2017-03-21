package fileManagement;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import chunks.Chunk;
import message.*;;
/*
 * class que divide um determinado ficheiro em chunks de 64000 bytes
 */
public class FileToCkunk {
	private String fileID;
	private int replication_degree = 3;
	private static int sizeOfFiles = 64000;

	@SuppressWarnings("unused")
	public FileToCkunk(File file, String type) throws IOException, NoSuchAlgorithmException {
		
		MakeChunks(file, type);
	}
	
	public void MakeChunks(File file,String type) throws IOException{
		int partCounter = 1;	
		byte[] buffer = new byte[sizeOfFiles];

		//sha256 algorithm ****************************************
		String stringToHash = file.getName() + file.lastModified();
		MessageDigest digest = null;
		  try {
		   digest = MessageDigest.getInstance("SHA-256");
		  } catch (NoSuchAlgorithmException e) {
		   e.printStackTrace();
		  }

		  byte[] hashedBytes = digest.digest(stringToHash.getBytes(StandardCharsets.UTF_8));
		  String fileID = String.format("%064x", new java.math.BigInteger(1,hashedBytes));
		  
		//end *************************************************************

		try (BufferedInputStream file_data = new BufferedInputStream(new FileInputStream(file))) {
			int tmp = 0;

			while ((tmp = file_data.read(buffer)) > 0) { //create each chunk while file have some bytes with data

				//Chunk newChunkObject = new Chunk(fileID, partCounter, buffer, replication_degree);
				Chunk newChunkObject = new Chunk(fileID, partCounter, buffer, replication_degree, "./Chunks");
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


