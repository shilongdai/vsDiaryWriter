package net.viperfish.utils.file;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;

public final class TextIOStreamHandler implements IOStreamHandler {

	@Override
	public DataOutputStream getOutputStream(File src) {
		try {
			CommonFunctions.initFile(src);
			return new DataOutputStream(new BufferedOutputStream(new FileOutputStream(src)));
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

	@Override
	public DataInputStream getInputStream(File src) {
		try {
			CommonFunctions.initFile(src);
			return new DataInputStream(new BufferedInputStream(new FileInputStream(src)));
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

}
