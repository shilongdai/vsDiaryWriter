package net.viperfish.journal.authProvider;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.security.SecureRandom;

import org.bouncycastle.crypto.generators.OpenBSDBCrypt;

import net.viperfish.journal.framework.AuthenticationManager;
import net.viperfish.journal.framework.errors.FailToLoadCredentialException;
import net.viperfish.journal.framework.errors.FailToStoreCredentialException;
import net.viperfish.utils.file.IOFile;
import net.viperfish.utils.file.TextIOStreamHandler;

final class OpenBSDBCryptAuthManager implements AuthenticationManager {

	private IOFile passwdFile;
	private SecureRandom rand;
	private String current;
	private String password;

	public OpenBSDBCryptAuthManager(File passwdFile) {
		this.passwdFile = new IOFile(passwdFile, new TextIOStreamHandler());
		rand = new SecureRandom();
	}

	@Override
	public synchronized void clear() {
		try {
			passwdFile.write("", StandardCharsets.US_ASCII);
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

	@Override
	public synchronized void setPassword(String pass) {
		byte[] salt = new byte[16];
		rand.nextBytes(salt);
		current = OpenBSDBCrypt.generate(pass.toCharArray(), salt, 15);
		try {
			passwdFile.write(current, StandardCharsets.US_ASCII);
		} catch (IOException e) {
			FailToStoreCredentialException fc = new FailToStoreCredentialException(
					"Cannot store the hashed password:" + e.getMessage());
			fc.initCause(e);
			throw new RuntimeException(fc);
		}
		this.password = pass;
	}

	@Override
	public synchronized void reload() {
		try {
			current = passwdFile.read(StandardCharsets.US_ASCII);
		} catch (IOException e) {
			FailToLoadCredentialException fl = new FailToLoadCredentialException(
					"Cannot load hashed password from file:" + e.getMessage());
			fl.initCause(e);
			throw new RuntimeException(e);
		}

	}

	@Override
	public synchronized String getPassword() {
		return password;
	}

	@Override
	public synchronized boolean verify(String pass) {
		boolean result = OpenBSDBCrypt.checkPassword(current, pass.toCharArray());
		if (result) {
			this.password = pass;
		}
		return result;
	}

}
