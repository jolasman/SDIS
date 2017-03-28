package fileManagement;

import java.io.FileOutputStream;
import java.io.IOException;
import java.util.HashMap;
import chunks.Chunk;

public class MergeChunks {

	public static void  MergeChunks(HashMap<String,Chunk> chunks, String path)throws IOException {
	
		
		try (FileOutputStream mergingStream = new FileOutputStream(path)) {
			
			for(int i = 0; i< chunks.size(); i++){
				System.out.println("\nMerge chunk : " + chunks.get((i + 1) + "").getChunkID() + " chunkNo:  " + chunks.get((i + 1) + "").getChunkNo() + "tamanho : " + chunks.get((i + 1) + "").getChunkData().length);
				mergingStream.write(chunks.get((i + 1) + "").getChunkData());
			}
			mergingStream.flush();
			mergingStream.close();
			
			System.out.println("\nMerge of all chunks into the file " + path + " created");
		}
		catch (Exception e) {
			System.out.println("Can't merge all chunks into the file " + path);
		}
	}

}
