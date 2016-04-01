package net.viperfish.utils.file;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;

import org.apache.commons.compress.utils.IOUtils;

public final class IOFile {

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

	public void write(byte[] data) throws IOException {
		try (DataOutputStream out = handler.getOutputStream(src)) {
			out.write(data);
			out.flush();
		} catch (IOException e) {
			throw e;
		}
	}

	public void write(String data, Charset c) throws IOException {
		write(data.getBytes(c));
	}

	public byte[] read() throws IOException {
		try (DataInputStream in = handler.getInputStream(src)) {
			byte[] result = IOUtils.toByteArray(in);
			return result;
		} catch (IOException e) {
			throw e;
		}
	}

	public String read(Charset c) throws IOException {
		return new String(read(), c);
	}

	public void clear() throws IOException {
		write(new byte[0]);
	}

	public File getFile() {
		return this.src;
	}
}
