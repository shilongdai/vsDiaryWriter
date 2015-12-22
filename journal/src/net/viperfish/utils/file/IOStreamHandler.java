package net.viperfish.utils.file;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;

public interface IOStreamHandler {
	public DataOutputStream getOutputStream(File src);

	public DataInputStream getInputStream(File src);
}
