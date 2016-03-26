package net.viperfish.utils.compression;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import org.apache.commons.compress.compressors.bzip2.BZip2CompressorInputStream;
import org.apache.commons.compress.compressors.bzip2.BZip2CompressorOutputStream;

/**
 * compressor with BZip 2 algorithm
 * 
 * @author sdai
 *
 */
final class BZ2Compressor extends Compressor {

	@Override
	protected OutputStream createOutputStream(ByteArrayOutputStream out) throws IOException {
		return new BZip2CompressorOutputStream(out);
	}

	@Override
	protected InputStream createInputStream(ByteArrayInputStream in) throws IOException {
		return new BZip2CompressorInputStream(in);
	}

}
