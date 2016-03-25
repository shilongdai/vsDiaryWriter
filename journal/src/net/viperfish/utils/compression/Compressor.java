package net.viperfish.utils.compression;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import org.apache.commons.compress.utils.IOUtils;

/**
 * A compressor utility that compresses bytes with a compression algorithm
 * 
 * @author sdai
 *
 */
public abstract class Compressor {

	/**
	 * create a compression output stream used to compress data
	 * 
	 * @param out
	 *            the byte array to store compressed data
	 * @return the usable compression output stream
	 * @throws IOException
	 */
	protected abstract OutputStream createOutputStream(ByteArrayOutputStream out) throws IOException;

	/**
	 * create a compression input stream used to depress date
	 * 
	 * @param in
	 *            the byte array to read from
	 * @return the usable compression input stream
	 * @throws IOException
	 */
	protected abstract InputStream createInputStream(ByteArrayInputStream in) throws IOException;

	/**
	 * compress data with a compression algorithm
	 * 
	 * @param data
	 *            the bytes to compress
	 * @return the compressed bytes
	 */
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

	/**
	 * uncompress data with a compression algorithm
	 * 
	 * @param data
	 *            the compressed bytes
	 * @return the uncompressed bytes
	 */
	public byte[] deflate(byte[] data) {
		ByteArrayInputStream in = new ByteArrayInputStream(data);
		try (InputStream depressor = createInputStream(in)) {
			return IOUtils.toByteArray(depressor);
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}
}
