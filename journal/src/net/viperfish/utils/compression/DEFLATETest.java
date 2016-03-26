package net.viperfish.utils.compression;

public final class DEFLATETest extends CompressorTest {

	@Override
	protected Compressor getCompressor() {
		return new DEFLATECompressor();
	}

}
