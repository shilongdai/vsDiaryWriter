package net.viperfish.utils.compression;

public class DEFLATETest extends CompressorTest {

	@Override
	protected Compressor getCompressor() {
		return new DEFLATECompressor();
	}

}
