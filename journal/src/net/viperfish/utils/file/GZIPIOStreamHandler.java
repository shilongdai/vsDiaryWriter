package net.viperfish.utils.file;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

public final class GZIPIOStreamHandler implements IOStreamHandler {

	@Override
	public DataOutputStream getOutputStream(File src) {
		try {
			CommonFunctions.initFile(src);
			return new DataOutputStream(new GZIPOutputStream(new FileOutputStream(src)));
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

	@Override
	public DataInputStream getInputStream(File src) {
		try {
			CommonFunctions.initFile(src);
			return new DataInputStream(new GZIPInputStream(new FileInputStream(src)));
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

}
