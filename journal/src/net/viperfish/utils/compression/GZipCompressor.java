package net.viperfish.utils.compression;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

public class GZipCompressor extends Compressor {

	protected GZipCompressor() {
		// TODO Auto-generated constructor stub
	}

	@Override
	protected OutputStream createOutputStream(ByteArrayOutputStream out) {
		GZIPOutputStream result;
		try {
			result = new GZIPOutputStream(out);
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
		return result;
	}

	@Override
	protected InputStream createInputStream(ByteArrayInputStream in) {
		GZIPInputStream result;
		try {
			result = new GZIPInputStream(in);
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
		return result;
	}

}
