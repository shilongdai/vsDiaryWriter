package test.java.compressorTests;

import net.viperfish.utils.compression.Compressor;
import net.viperfish.utils.compression.XZCompressor;

public class XZTest extends CompressorTest {

	@Override
	protected Compressor getCompressor() {
		return new XZCompressor();
	}

}
