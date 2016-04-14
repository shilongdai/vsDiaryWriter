package net.viperfish.journal.authProvider;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.security.SecureRandom;
import java.util.Arrays;

import org.bouncycastle.crypto.generators.SCrypt;

import net.viperfish.journal.framework.AuthenticationManager;
import net.viperfish.journal.framework.errors.FailToLoadCredentialException;
import net.viperfish.journal.framework.errors.FailToStoreCredentialException;
import net.viperfish.utils.file.IOFile;
import net.viperfish.utils.file.TextIOStreamHandler;

final class SCryptAuthManager implements AuthenticationManager {

	private SecureRandom rand;
	private String password;
	private PasswordFile passCont;
	private IOFile passwdFile;

	public SCryptAuthManager(File passwdFile) {
		rand = new SecureRandom();
		this.passwdFile = new IOFile(passwdFile, new TextIOStreamHandler());
		passCont = null;
	}

	@Override
	public synchronized void clear() {
		try {
			passwdFile.write("", StandardCharsets.UTF_16);
		} catch (IOException e) {
			throw new RuntimeException(e);
		}

	}

	@Override
	public synchronized void setPassword(String pass) {
		byte[] salt = new byte[8];
		rand.nextBytes(salt);
		byte[] crypt = SCrypt.generate(pass.getBytes(StandardCharsets.UTF_16), salt, 262144, 10, 3, 256);
		passCont = new PasswordFile(crypt, salt);
		try {
			passwdFile.write(passCont.toString(), StandardCharsets.US_ASCII);
		} catch (IOException e) {
			FailToStoreCredentialException fs = new FailToStoreCredentialException(
					"Cannot store password to passwd file:" + e.getMessage());
			fs.initCause(e);
			throw new RuntimeException(fs);
		}
		this.password = pass;
	}

	@Override
	public synchronized void reload() {
		try {
			String content = passwdFile.read(StandardCharsets.US_ASCII);
			if (content.length() == 0) {
				return;
			}
			passCont = PasswordFile.valueOf(content);
		} catch (IOException e) {
			FailToLoadCredentialException fl = new FailToLoadCredentialException(
					"Cannot load credential from file:" + e.getMessage());
			fl.initCause(e);
			throw new RuntimeException(fl);
		}
	}

	@Override
	public synchronized String getPassword() {
		return password;
	}

	@Override
	public synchronized boolean verify(String pass) {
		byte[] crypt = SCrypt.generate(pass.getBytes(StandardCharsets.UTF_16), passCont.getSalt(), 262144, 10, 3, 256);
		boolean match = Arrays.equals(crypt, passCont.getCredentialInfo());
		if (match) {
			this.password = pass;
		}
		return match;
	}

}
