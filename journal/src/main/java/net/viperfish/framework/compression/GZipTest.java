package net.viperfish.framework.compression;

public final class GZipTest extends CompressorTest {

	@Override
	protected Compressor getCompressor() {
		try {
			return Compressors.getCompressor("gz");
		} catch (FailToInitCompressionException e) {
			throw new RuntimeException(e);
		}
	}

}
