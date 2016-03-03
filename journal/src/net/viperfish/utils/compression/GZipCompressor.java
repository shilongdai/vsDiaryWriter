package net.viperfish.utils.compression;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import org.apache.commons.compress.compressors.gzip.GzipCompressorInputStream;
import org.apache.commons.compress.compressors.gzip.GzipCompressorOutputStream;

public class GZipCompressor extends Compressor {

	protected GZipCompressor() {
		// TODO Auto-generated constructor stub
	}

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
