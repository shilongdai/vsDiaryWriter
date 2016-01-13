package net.viperfish.journal.secureProvider;

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
		try (OutputStream compressor = createOutputStream(out)) {
			compressor.write(data);
			compressor.flush();
			return out.toByteArray();
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

	public byte[] deflate(byte[] data) {
		ByteArrayInputStream in = new ByteArrayInputStream(data);
		try (InputStream depressor = createInputStream(in)) {
			ByteArrayOutputStream bf = new ByteArrayOutputStream();
			System.err.println("depressor available:" + depressor.available());
			int n = 0;
			byte[] buffer = new byte[64];
			while ((n = depressor.read(buffer)) != -1) {
				bf.write(buffer, 0, n);
			}
			bf.flush();
			return bf.toByteArray();
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}
}
