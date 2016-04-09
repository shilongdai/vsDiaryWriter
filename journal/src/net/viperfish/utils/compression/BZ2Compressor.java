package net.viperfish.utils.compression;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import org.apache.commons.compress.compressors.CompressorException;
import org.apache.commons.compress.compressors.CompressorStreamFactory;

/**
 * compressor with BZip 2 algorithm
 * 
 * @author sdai
 *
 */
final class BZ2Compressor extends Compressor {

	@Override
	protected OutputStream createOutputStream(ByteArrayOutputStream out) throws IOException, CompressorException {
		return getFactory().createCompressorOutputStream(CompressorStreamFactory.BZIP2, out);
	}

	@Override
	protected InputStream createInputStream(ByteArrayInputStream in) throws IOException, CompressorException {
		return getFactory().createCompressorInputStream(CompressorStreamFactory.BZIP2, in);
	}

}
