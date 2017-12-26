package net.viperfish.journal2.core;

import java.util.Map;
import net.viperfish.journal2.error.CipherException;
import net.viperfish.journal2.error.CompromisedDataException;

public interface Processor {

    public Map<String, byte[]> doProccess(Map<String, byte[]> data, CryptoInfo info)
            throws CipherException;

    public Map<String, byte[]> undoProccess(Map<String, byte[]> data, CryptoInfo info)
            throws CipherException, CompromisedDataException;

    public String getId();

    public CryptoInfoGenerator generator();

}
