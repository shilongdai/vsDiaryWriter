package net.viperfish.utils.file;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;

public class TextIOStreamHandler implements IOStreamHandler {

	@Override
	public DataOutputStream getOutputStream(File src) {
		try {
			return new DataOutputStream(new BufferedOutputStream(
					new FileOutputStream(src)));
		} catch (FileNotFoundException e) {
			throw new RuntimeException(e);
		}
	}

	@Override
	public DataInputStream getInputStream(File src) {
		try {
			return new DataInputStream(new BufferedInputStream(
					new FileInputStream(src)));
		} catch (FileNotFoundException e) {
			throw new RuntimeException(e);
		}
	}

}
