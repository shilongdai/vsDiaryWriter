package test.java.compressorTests;

import net.viperfish.utils.compression.BZ2Compressor;
import net.viperfish.utils.compression.Compressor;

public class BZ2Test extends CompressorTest {

	@Override
	protected Compressor getCompressor() {
		return new BZ2Compressor();
	}

}
