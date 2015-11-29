package net.viperfish.journal.fileDatabase;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;

public class TextFileEntryDatabase extends FileEntryDatabase {

	public TextFileEntryDatabase() {
		// TODO Auto-generated constructor stub
	}

	@Override
	protected String readFile(File path) {
		DataInputStream in = null;
		String result = new String();
		try {
			in = new DataInputStream(new BufferedInputStream(
					new FileInputStream(path)));
			while (in.available() != 0) {
				result += in.readUTF();
			}
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
			out = new DataOutputStream(new BufferedOutputStream(
					new FileOutputStream(path)));
			out.writeUTF(data);
		} catch (IOException e) {
			throw new RuntimeException(e);
		} finally {
			if (out != null) {
				try {
					out.close();
				} catch (IOException e) {
					throw new RuntimeException(e);
				}
			}
		}

	}

}
