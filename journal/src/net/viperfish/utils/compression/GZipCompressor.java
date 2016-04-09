package net.viperfish.utils.compression;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import org.apache.commons.compress.compressors.CompressorException;
import org.apache.commons.compress.compressors.CompressorStreamFactory;

/**
 * compressor with GZip algorithm
 * 
 * @author sdai
 *
 */
final class GZipCompressor extends Compressor {

	@Override
	protected OutputStream createOutputStream(ByteArrayOutputStream out) throws IOException, CompressorException {
		return getFactory().createCompressorOutputStream(CompressorStreamFactory.GZIP, out);
	}

	@Override
	protected InputStream createInputStream(ByteArrayInputStream in) throws IOException, CompressorException {
		return getFactory().createCompressorInputStream(CompressorStreamFactory.GZIP, in);
	}

}
