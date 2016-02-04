package net.viperfish.utils.compression;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public abstract class Compressor {

	protected abstract OutputStream createOutputStream(ByteArrayOutputStream out);

	protected abstract InputStream createInputStream(ByteArrayInputStream in);

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
		System.err.println("data length:" + data.length);
		ByteArrayInputStream in = new ByteArrayInputStream(data);
		try (InputStream depressor = createInputStream(in)) {
			ByteArrayOutputStream out = new ByteArrayOutputStream();
			final byte[] buffer = new byte[64];
			int n = 0;
			while (-1 != (n = depressor.read(buffer))) {
				out.write(buffer, 0, n);
			}
			return out.toByteArray();
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}
}
