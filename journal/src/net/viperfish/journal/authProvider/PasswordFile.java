package net.viperfish.journal.authProvider;

import java.util.Arrays;

import org.apache.commons.codec.binary.Base64;

/**
 * A structure representing a stored password containing the password and salt.
 * This class is immutable and its value cannot be changed upon creation. It can
 * be created from pre-existing password and salt data, or it can be created by
 * parsing a string with 2 base64 encoded segments.
 * 
 * <p>
 * This class is NOT thread safe
 * </p>
 * 
 * @author sdai
 *
 */
final class PasswordFile {
	private final byte[] credentialInfo;
	private final byte[] salt;

	/**
	 * creates a password file structure with the credential and the salt. This
	 * method creates a new {@link PasswordFile} representing a stored password
	 * with credential information(hash, or other) and the salt. The
	 * representation is a copy of the original values.
	 * 
	 * @param credentialInfo
	 *            the credential information(ex. hash) to store
	 * @param salt
	 *            the salt
	 * 
	 * @throws NullPointerException
	 *             if the parameters are null
	 */
	PasswordFile(byte[] credentialInfo, byte[] salt) {
		super();
		this.credentialInfo = Arrays.copyOf(credentialInfo, credentialInfo.length);
		this.salt = Arrays.copyOf(salt, salt.length);

	}

	/**
	 * gets a copy of the stored credential
	 * 
	 * @return the stored credential
	 */
	byte[] getCredentialInfo() {
		return credentialInfo.clone();
	}

	/**
	 * gets a copy of the stored salt
	 * 
	 * @return the stored salt
	 */
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

	/**
	 * gets the encoded string representation of this class
	 * 
	 * This method encodes the internal data into base64 string in the format of
	 * base64(credential)$base64(salt).
	 * 
	 * @return the encoded string in the form of credential$salt
	 */
	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append(Base64.encodeBase64String(credentialInfo)).append('$').append(Base64.encodeBase64String(salt));
		return sb.toString();
	}

	/**
	 * creates a new {@link PasswordFile} from a string representation.
	 * 
	 * This method converts a string in the format of
	 * base64(credentialInfo)$base64(salt) into a {@link PasswordFile}.
	 * 
	 * @throws IllegalArgumentException
	 *             if the format of the string is not credential$salt
	 * 
	 * @param passwordContent
	 *            the string representation
	 * @return the created {@link PasswordFile}
	 */
	static PasswordFile valueOf(String passwordContent) {
		String[] content = passwordContent.split("\\$");
		if (content.length != 2) {
			throw new IllegalArgumentException("Password file must be in format of credential$salt");
		}

		byte[] credential = Base64.decodeBase64(content[0]);
		byte[] salt = Base64.decodeBase64(content[1]);

		return new PasswordFile(credential, salt);
	}

}
