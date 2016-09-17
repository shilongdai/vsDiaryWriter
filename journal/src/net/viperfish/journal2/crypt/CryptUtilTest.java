package net.viperfish.journal2.crypt;

import java.nio.charset.StandardCharsets;
import java.security.SecureRandom;

import org.bouncycastle.crypto.DataLengthException;
import org.bouncycastle.crypto.InvalidCipherTextException;
import org.bouncycastle.crypto.digests.WhirlpoolDigest;
import org.bouncycastle.util.Arrays;
import org.junit.Assert;
import org.junit.Test;

public class CryptUtilTest {

	public CryptUtilTest() {

	}

	@Test
	public void testECBCrypt() throws DataLengthException, IllegalStateException, InvalidCipherTextException {
		byte[] data = "Test Cipher".getBytes(StandardCharsets.UTF_8);
		byte[] key = new byte[32];
		for (int i = 0; i < key.length; ++i) {
			key[i] = 0x2A;
		}

		MarsEngine engine = new MarsEngine();

		byte[] encrypted = CryptUtils.INSTANCE.ecbCrypt(data, key, engine);

		boolean match = Arrays.areEqual(data, encrypted);
		Assert.assertEquals(false, match);

		byte[] decrypted = CryptUtils.INSTANCE.ecbDecrypt(encrypted, key, engine);
		Assert.assertArrayEquals(data, decrypted);
	}

	@Test
	public void testKDF() {
		SecureRandom rand = new SecureRandom();
		byte[] salt = new byte[10];
		rand.nextBytes(salt);

		byte[] key = CryptUtils.INSTANCE.kdfKey("password", salt, 256, new WhirlpoolDigest());
		Assert.assertEquals(32, key.length);
		byte[] key2 = CryptUtils.INSTANCE.kdfKey("password", salt, 256, new WhirlpoolDigest());
		Assert.assertArrayEquals(key, key2);
	}

}
