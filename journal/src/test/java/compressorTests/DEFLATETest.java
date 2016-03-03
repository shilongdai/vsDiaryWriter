package test.java.compressorTests;

import net.viperfish.utils.compression.Compressor;
import net.viperfish.utils.compression.DEFLATECompressor;

public class DEFLATETest extends CompressorTest {

	@Override
	protected Compressor getCompressor() {
		return new DEFLATECompressor();
	}

}
