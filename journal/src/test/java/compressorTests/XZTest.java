package test.java.compressorTests;

import net.viperfish.journal.secureProvider.Compressor;
import net.viperfish.journal.secureProvider.XZCompressor;

public class XZTest extends CompressorTest {

	@Override
	protected Compressor getCompressor() {
		return new XZCompressor();
	}

}
