package net.viperfish.utils.compression;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import org.apache.commons.compress.compressors.deflate.DeflateCompressorInputStream;
import org.apache.commons.compress.compressors.deflate.DeflateCompressorOutputStream;

class DEFLATECompressor extends Compressor {

	@Override
	protected OutputStream createOutputStream(ByteArrayOutputStream out) throws IOException {
		return new DeflateCompressorOutputStream(out);
	}

	@Override
	protected InputStream createInputStream(ByteArrayInputStream in) throws IOException {
		return new DeflateCompressorInputStream(in);
	}

}
