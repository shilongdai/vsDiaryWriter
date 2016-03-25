package net.viperfish.utils.compression;

public class XZTest extends CompressorTest {

	@Override
	protected Compressor getCompressor() {
		return new XZCompressor();
	}

}
