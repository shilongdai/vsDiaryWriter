package net.viperfish.journal.secureProvider;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import org.tukaani.xz.LZMA2Options;
import org.tukaani.xz.XZInputStream;
import org.tukaani.xz.XZOutputStream;

public class XZCompressor extends Compressor {

	@Override
	protected OutputStream createOutputStream(ByteArrayOutputStream out) {
		try {
			XZOutputStream result = new XZOutputStream(out, new LZMA2Options());
			return result;
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

	@Override
	protected InputStream createInputStream(ByteArrayInputStream in) {
		XZInputStream result = null;
		try {
			result = new XZInputStream(in);
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
		return result;
	}

}
