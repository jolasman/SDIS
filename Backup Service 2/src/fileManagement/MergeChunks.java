package fileManagement;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import chunks.Chunk;

public class MergeChunks {

	public static void  MergeChunks(ArrayList<Chunk> chunks, String path)throws IOException {

		Collections.sort(chunks);		
		
		try (FileOutputStream mergingStream = new FileOutputStream(path)) {
			
			
			for (Chunk c : chunks) {
				System.out.println(c.getChunkID() + " chunkNo:  " + c.getChunkNo() + "tamanho : " + c.getChunkData().length);
				mergingStream.write(c.getChunkData());
			}
			mergingStream.flush();
			mergingStream.close();
			
			System.out.println("Merge of all chunks into the file " + path + " created");
		}
		catch (Exception e) {
			System.out.println("Can't merge all chunks into the file " + path);
		}
	}

}
