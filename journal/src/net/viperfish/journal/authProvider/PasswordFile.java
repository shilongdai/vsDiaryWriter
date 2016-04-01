package net.viperfish.journal.authProvider;

import java.util.Arrays;

import org.apache.commons.codec.binary.Base64;

final class PasswordFile {
	private final byte[] credentialInfo;
	private final byte[] salt;

	PasswordFile(byte[] credentialInfo, byte[] salt) {
		super();
		this.credentialInfo = Arrays.copyOf(credentialInfo, credentialInfo.length);
		this.salt = Arrays.copyOf(salt, salt.length);

	}

	byte[] getCredentialInfo() {
		return credentialInfo.clone();
	}

	byte[] getSalt() {
		return salt.clone();
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + Arrays.hashCode(credentialInfo);
		result = prime * result + Arrays.hashCode(salt);
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		PasswordFile other = (PasswordFile) obj;
		if (!Arrays.equals(credentialInfo, other.credentialInfo))
			return false;
		if (!Arrays.equals(salt, other.salt))
			return false;
		return true;
	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append(Base64.encodeBase64String(credentialInfo)).append('$').append(Base64.encodeBase64String(salt));
		return sb.toString();
	}

	static PasswordFile getPasswordData(String passwordContent) {
		String[] content = passwordContent.split("\\$");
		if (content.length < 2) {
			throw new IllegalArgumentException("Password file must be in format of credential$salt");
		}

		byte[] credential = Base64.decodeBase64(content[0]);
		byte[] salt = Base64.decodeBase64(content[1]);

		return new PasswordFile(credential, salt);
	}

}
