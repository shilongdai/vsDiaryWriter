package test.java.compressorTests;

import net.viperfish.utils.compression.Compressor;
import net.viperfish.utils.compression.Compressors;
import net.viperfish.utils.compression.FailToInitCompressionException;

public class GZipTest extends CompressorTest {

	@Override
	protected Compressor getCompressor() {
		try {
			return Compressors.getCompressor("gz");
		} catch (FailToInitCompressionException e) {
			throw new RuntimeException(e);
		}
	}

}
