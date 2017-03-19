package FileManagement;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.util.List;

public class MergeChunks {

	public  MergeChunks(List<File> files, File into)throws IOException {

		try (BufferedOutputStream mergingStream = new BufferedOutputStream(new FileOutputStream(into))) {
			for (File f : files) {
				Files.copy(f.toPath(), mergingStream);

			}
			System.out.println("Merge of all chunks into the file " + into + " created");
		}
		catch (Exception e) {
			System.out.println("Can't merge all chunks into the file " + into);
		}
	}

}
