package net.viperfish.utils.compression;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import org.apache.commons.compress.compressors.gzip.GzipCompressorInputStream;
import org.apache.commons.compress.compressors.gzip.GzipCompressorOutputStream;

/**
 * compressor with GZip algorithm
 * 
 * @author sdai
 *
 */
final class GZipCompressor extends Compressor {

	@Override
	protected OutputStream createOutputStream(ByteArrayOutputStream out) throws IOException {
		GzipCompressorOutputStream result;
		result = new GzipCompressorOutputStream(out);
		return result;
	}

	@Override
	protected InputStream createInputStream(ByteArrayInputStream in) throws IOException {
		GzipCompressorInputStream result;
		result = new GzipCompressorInputStream(in);
		return result;
	}

}
