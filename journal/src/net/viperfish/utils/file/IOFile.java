package net.viperfish.utils.file;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.EOFException;
import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.LinkedList;
import java.util.List;

public class IOFile {

	private final File src;
	private IOStreamHandler handler;

	public IOFile(File theFile) {
		this.src = theFile;
	}

	public IOFile(File theFile, IOStreamHandler handler) {
		this(theFile);
		this.handler = handler;
	}

	public IOStreamHandler getHandler() {
		return handler;
	}

	public void setHandler(IOStreamHandler handler) {
		this.handler = handler;
	}

	public void write(byte[] data) {
		try (DataOutputStream out = handler.getOutputStream(src)) {
			out.write(data);
			out.flush();
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

	public void write(String data, Charset c) {
		write(data.getBytes(c));
	}

	public byte[] read() {
		try (DataInputStream in = handler.getInputStream(src)) {
			List<Byte> buffer = new LinkedList<Byte>();
			while (in.available() != 0) {
				try {
					buffer.add(in.readByte());
				} catch (EOFException e) {
					break;
				}
			}
			byte[] result = new byte[buffer.size()];
			int current = 0;
			for (Byte i : buffer) {
				result[current++] = i;
			}
			return result;
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

	public String read(Charset c) {
		return new String(read(), c);
	}

	public void clear() {
		write(new byte[0]);
	}
}
