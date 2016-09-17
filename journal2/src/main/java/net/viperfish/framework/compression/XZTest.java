package net.viperfish.framework.compression;

public final class XZTest extends CompressorTest {

	@Override
	protected Compressor getCompressor() {
		return new XZCompressor();
	}

}
