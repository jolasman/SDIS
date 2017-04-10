package fileManagement;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;

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
		int chunkNo = 1;	
		fileID = SHA256.ToSha256(file); 
		Path path = Paths.get(file.getPath());
		byte[] fileData = Files.readAllBytes(path);
		
		for(int i = 0; i < fileData.length; i+=sizeOfFiles){
			if(fileData.length < (i+sizeOfFiles)){// ultimo chunk é menor que os 64000
				int lengthLastChunk = fileData.length -i;
				Chunk newChunkObject = new Chunk(fileID, chunkNo, Arrays.copyOfRange(fileData,i,(i+lengthLastChunk)), replication_degree, "./Chunks");
				DatabaseChunksStored.StoreChunkID(fileID + chunkNo);
				
			}		
			else if(fileData.length > (i+sizeOfFiles)){// ultimo chunk é menor que os 64000
				Chunk newChunkObject = new Chunk(fileID, chunkNo, Arrays.copyOfRange(fileData,i, (i+sizeOfFiles)), replication_degree, "./Chunks");
				DatabaseChunksStored.StoreChunkID(fileID + chunkNo);
			}
			else{
				Chunk newChunkObject = new Chunk(fileID, chunkNo, new byte[0], replication_degree, "./Chunks");
				DatabaseChunksStored.StoreChunkID(fileID + chunkNo);
			}
			chunkNo++;
		}
		
			
	}

	public String getFileID(){
		return fileID;
	}
}


