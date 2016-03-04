package net.viperfish.utils.compression;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import org.apache.commons.compress.utils.IOUtils;

public abstract class Compressor {

	protected abstract OutputStream createOutputStream(ByteArrayOutputStream out) throws IOException;

	protected abstract InputStream createInputStream(ByteArrayInputStream in) throws IOException;

	public byte[] compress(byte[] data) {
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		try {
			OutputStream compressor = createOutputStream(out);
			compressor.write(data);
			compressor.flush();
			compressor.close();
			return out.toByteArray();
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

	public byte[] deflate(byte[] data) {
		ByteArrayInputStream in = new ByteArrayInputStream(data);
		try (InputStream depressor = createInputStream(in)) {
			return IOUtils.toByteArray(depressor);
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}
}
