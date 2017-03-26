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

	public static void  MergeChunks(ArrayList<Chunk> chunks, File into)throws IOException {

		Collections.sort(chunks);		
		
		try (FileOutputStream mergingStream = new FileOutputStream(into)) {
			
			
			for (Chunk c : chunks) {
				mergingStream.write(c.getChunkData());
			}
			mergingStream.flush();
			mergingStream.close();
			
			System.out.println("Merge of all chunks into the file " + into + " created");
		}
		catch (Exception e) {
			System.out.println("Can't merge all chunks into the file " + into);
		}
	}

}
