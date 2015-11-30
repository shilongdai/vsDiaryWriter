package net.viperfish.journal.fileDatabase;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.EOFException;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.LinkedList;
import java.util.List;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

public class GZippedFileEntryDatabase extends FileEntryDatabase {

	public GZippedFileEntryDatabase(File dataDir) {
		super(dataDir);
	}

	private Byte[] readAllByte(DataInputStream in) throws IOException {
		List<Byte> buffer = new LinkedList<Byte>();
		while (true) {
			try {
				buffer.add(in.readByte());
			} catch (EOFException e) {
				break;
			}
		}
		return buffer.toArray(new Byte[0]);
	}

	private byte[] BytesTobytes(Byte[] b) {
		byte[] result = new byte[b.length];
		int count = 0;
		for (Byte i : b) {
			result[count++] = i;
		}
		return result;
	}

	@Override
	protected String readFile(File path) {
		DataInputStream in = null;
		String result = new String();
		try {
			in = new DataInputStream(new GZIPInputStream(new FileInputStream(
					path)));
			Byte[] data = readAllByte(in);

			result = new String(BytesTobytes(data), StandardCharsets.UTF_16);
		} catch (IOException e) {
			throw new RuntimeException(e);
		} finally {
			if (in != null) {
				try {
					in.close();
				} catch (IOException e) {
					throw new RuntimeException(e);
				}
			}
		}
		return result;
	}

	@Override
	protected void writeFile(File path, String data) {
		DataOutputStream out = null;
		try {
			out = new DataOutputStream(new GZIPOutputStream(
					new FileOutputStream(path)));
			out.write(data.getBytes(StandardCharsets.UTF_16));
		} catch (IOException e) {
			throw new RuntimeException(e);
		} finally {
			try {
				out.close();
			} catch (IOException e) {
				throw new RuntimeException(e);
			}
		}

	}

}
