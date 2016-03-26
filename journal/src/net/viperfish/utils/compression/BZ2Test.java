package net.viperfish.utils.compression;

public final class BZ2Test extends CompressorTest {

	@Override
	protected Compressor getCompressor() {
		return new BZ2Compressor();
	}

}
