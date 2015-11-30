package net.viperfish.journal.authentications;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.security.SecureRandom;
import java.util.Arrays;

import net.viperfish.journal.auth.AuthenticationManager;
import net.viperfish.journal.secureAlgs.JCEDigester;

import org.apache.commons.codec.binary.Base64;

public class HashAuthManager implements AuthenticationManager {

	private JCEDigester dig;
	private File passwdFile;
	private File dataDir;
	private boolean passwordSet;
	private byte[] hash;
	private byte[] salt;
	private SecureRandom rand;

	private byte[] hashWithSalt(byte[] data, byte[] salt, int iter) {
		byte[] buffer = new byte[data.length + salt.length];
		byte[] initial;
		System.arraycopy(data, 0, buffer, 0, data.length);
		System.arraycopy(salt, 0, buffer, data.length, salt.length);
		initial = dig.digest(buffer);
		buffer = new byte[initial.length + salt.length];
		byte[] hash = initial;
		for (int i = 0; i < iter - 1; ++i) {
			System.arraycopy(hash, 0, buffer, 0, hash.length);
			System.arraycopy(salt, 0, buffer, hash.length, salt.length);
			hash = dig.digest(buffer);
		}
		return hash;
	}

	private String hashAuthFormat() {
		String hash64 = Base64.encodeBase64String(hash);
		String salt64 = Base64.encodeBase64String(salt);
		String result = hash64 + "$" + salt64;
		return result;
	}

	private void generateSalt(int length) {
		salt = new byte[length];
		rand.nextBytes(salt);
	}

	private void writePasswdFile(String formatted) {
		byte[] data = formatted.getBytes(StandardCharsets.UTF_8);
		DataOutputStream out = null;
		try {
			out = new DataOutputStream(new BufferedOutputStream(
					new FileOutputStream(passwdFile)));
			out.write(data);
		} catch (IOException e) {
			throw new RuntimeException(e);
		} finally {
			if (out != null) {
				try {
					out.close();
				} catch (IOException e) {
					e.fillInStackTrace();
					throw new RuntimeException(e);
				}
			}
		}
	}

	private String readPasswdFile() {
		DataInputStream in = null;
		try {
			in = new DataInputStream(new BufferedInputStream(
					new FileInputStream(passwdFile)));
			int estLength = in.available();
			byte[] buffer = new byte[estLength + 256];
			int actualLength = 0;
			while (!(in.available() == 0)) {
				buffer[actualLength] = in.readByte();
				actualLength++;
			}
			byte[] finalArray = new byte[actualLength];
			System.arraycopy(buffer, 0, finalArray, 0, actualLength);
			return new String(finalArray, StandardCharsets.UTF_8);
		} catch (IOException e) {
			throw new RuntimeException(e);
		} finally {
			if (in != null) {
				try {
					in.close();
				} catch (IOException e) {
					e.fillInStackTrace();
					throw new RuntimeException(e);
				}
			}
		}
	}

	private void loadPasswd() {
		try {
			passwdFile = new File(dataDir.getCanonicalPath() + "/passwd");
			if (!passwdFile.exists()) {
				passwdFile.createNewFile();
				passwordSet = false;
				return;
			}
			String formatedPasswd = readPasswdFile();
			String[] parts = formatedPasswd.split("\\$");
			String hash = parts[0];
			String salt = parts[1];
			this.hash = Base64.decodeBase64(hash);
			this.salt = Base64.decodeBase64(salt);
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

	public HashAuthManager(File dataDir) {
		this.dataDir = dataDir;
		dig = new JCEDigester();
		rand = new SecureRandom();
		passwordSet = true;
		loadPasswd();
	}

	public byte[] getSalt() {
		return salt;
	}

	@Override
	public void clear() {
		passwdFile.delete();

	}

	@Override
	public void setPassword(String pass) {
		byte[] bytes = pass.getBytes(StandardCharsets.UTF_16);

		generateSalt(12);
		hash = hashWithSalt(bytes, salt, 3000);
		String formatted = hashAuthFormat();
		writePasswdFile(formatted);
	}

	public byte[] getHash() {
		return hash;
	}

	@Override
	public void reload() {
		passwdFile = null;
		hash = null;
		salt = null;
		loadPasswd();
	}

	@Override
	public boolean verify(String pass) {
		byte[] bytes = pass.getBytes(StandardCharsets.UTF_16);

		byte[] providedHash = hashWithSalt(bytes, salt, 3000);

		return Arrays.equals(providedHash, hash);
	}

	@Override
	public boolean isPasswordSet() {
		return passwordSet;
	}
}
