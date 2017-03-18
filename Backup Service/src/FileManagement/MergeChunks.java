package FileManagement;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.util.List;

public class MergeChunks {

public  MergeChunks(List<File> files, File into)throws IOException {
		System.out.println("Entrou no merge");
	    try (BufferedOutputStream mergingStream = new BufferedOutputStream(
	            new FileOutputStream(into))) {
	        for (File f : files) {
	            Files.copy(f.toPath(), mergingStream);
	        }
	    }
	}

}
