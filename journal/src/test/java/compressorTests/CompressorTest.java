package test.java.compressorTests;

import org.junit.Assert;
import org.junit.Test;

import net.viperfish.journal.secureProvider.Compressor;

public abstract class CompressorTest {

	protected abstract Compressor getCompressor();

	@Test
	public void testCompressor() {
		Compressor com = getCompressor();
		byte[] uncompressed = "The stern of the ship faced the Solar System, where the sun was by now no more than a yellow star just a bit brighter than the rest. The peripheral spiral arm of the Milky Way lay in this direction, its stars sparse. The depth and expanse of deep space exhibited an arrogance that left no support for the mind or the eyes. “Dark. It’s so fucking dark,” the captain murmured, and then shot himself."
				.getBytes();
		System.err.println("uncompressed size:" + uncompressed.length);
		byte[] compressed = com.compress(uncompressed);
		System.err.println("compressed size:" + compressed.length);
		byte[] depressed = com.deflate(compressed);
		Assert.assertArrayEquals(
				"The stern of the ship faced the Solar System, where the sun was by now no more than a yellow star just a bit brighter than the rest. The peripheral spiral arm of the Milky Way lay in this direction, its stars sparse. The depth and expanse of deep space exhibited an arrogance that left no support for the mind or the eyes. “Dark. It’s so fucking dark,” the captain murmured, and then shot himself."
						.getBytes(),
				depressed);
	}

}
