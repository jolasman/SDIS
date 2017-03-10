package inicializacao;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.util.Arrays;

//import peer.Peer;
import EspacoEmDisco.FileManager;
import utils.Utils;
import chunk.Chunk;
import basededados.FileInfo;


public class inicializacaoBackup implements Runnable{

	private File file;
	private int replicationDegree;

	public inicializacaoBackup(File file, int replicationDegree) {
		this.file = file;
		this.replicationDegree = replicationDegree;

	}



	@Override
	public void run() {
		
		String fileID = Utils.getFileID(file);

		try {
			byte[] fileData = FileManager.loadFile(file);

			int numChunks = fileData.length / Chunk.MAX_SIZE + 1;

			System.out.println(file.getName() + " vai ser dividido em " + numChunks + " chunks.");

			ByteArrayInputStream stream = new ByteArrayInputStream(fileData);
			byte[] streamConsumer = new byte[Chunk.MAX_SIZE];

			for (int i = 0; i < numChunks; i++) {
				/*
				 * First step: get a chunk of the file
				 */

				byte[] chunkData;

				if (i == numChunks - 1 && fileData.length % Chunk.MAX_SIZE == 0) {
					chunkData = new byte[0];
				} else {
					int numBytesRead = stream.read(streamConsumer, 0,
							streamConsumer.length);

					chunkData = Arrays.copyOfRange(streamConsumer, 0,
							numBytesRead);
				}

				Chunk chunk = new Chunk(fileID, i, replicationDegree, chunkData);

				/*
				 * Second step: backup that chunk
				 */

				Thread t = new Thread(new InicializacaoBackupChunk(chunk));
				t.start();

				/*
				 * TODO can chunks be backed up in parallel? not working for big
				 * files...
				 */
				try {
					t.join();
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}

			Peer.getDatabase().addRestorableFile(file.getName(),
					new FileInfo(fileID, numChunks));
		} catch (FileNotFoundException e) {
			System.out.println("file not found");
		}
	}

}