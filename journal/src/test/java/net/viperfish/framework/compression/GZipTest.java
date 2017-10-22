package net.viperfish.framework.compression;

public final class GZipTest extends CompressorTest {

    @Override
    protected Compressor getCompressor() {
        return new GZipCompressor();
    }

}
