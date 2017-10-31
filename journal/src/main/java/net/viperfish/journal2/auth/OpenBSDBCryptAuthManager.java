package net.viperfish.journal2.auth;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.security.SecureRandom;
import net.viperfish.journal2.core.Observable;
import org.bouncycastle.crypto.generators.OpenBSDBCrypt;

/**
 * An authentication manager that uses BCrypt to hash password.
 *
 * This class uses BCrypt used on OpenBSD to hash user password with a 16 byte
 * salt. The hashing process takes a while to thwart brute force cracking. The
 * current cost factor for BCrypt is 16.
 *
 * This class <b>IS</b> thread safe.
 *
 * @author sdai
 *
 */
public class OpenBSDBCryptAuthManager extends Observable<String>
        implements net.viperfish.journal2.core.AuthenticationManager {

    private final Path passwdFile;
    private final SecureRandom rand;
    private String current;
    private String password;

    /**
     * creates an {@link OpenBSDBCryptAuthManager} given the location of the
     * password storage file
     *
     * @param passwdFile the file to store password informations in
     */
    public OpenBSDBCryptAuthManager(Path passwdFile) {
        this.passwdFile = passwdFile;
        rand = new SecureRandom();
    }

    /**
     * clears the content of the password file
     *
     * This method clears the current hash and deletes the password file
     *
     * @throws IOException if failed to write an empty string
     *
     *
     */
    @Override
    public void clear() throws IOException {
        try {
            if (!passwdFile.toFile().delete()) {
                throw new IOException("Cannot delete password file");
            }
        } finally {
            this.current = null;
            this.password = null;
        }
    }

    /**
     * sets a password
     *
     * This method sets the password of the user in this
     * {@link AuthenticationManager}. The password is hashed with BCrypt using
     * the format in OpenBSD with a cost factor of 16. The final output will be
     * written to the password file as an ASCII string. This method will ready
     * this authenticator for {@link OpenBSDBCryptAuthManager#verify(String)}
     * and {@link OpenBSDBCryptAuthManager#getPassword()}.
     *
     * @param pass the password to set
     *
     * @throws IOException if failed to write the hashed password to the
     * password file
     */
    @Override
    public void setPassword(String pass) throws IOException {
        byte[] salt = new byte[16];
        rand.nextBytes(salt);
        current = OpenBSDBCrypt.generate(pass.toCharArray(), salt, 16);
        try {
            Files.write(passwdFile, current.getBytes(StandardCharsets.US_ASCII), StandardOpenOption.CREATE,
                    StandardOpenOption.WRITE, StandardOpenOption.TRUNCATE_EXISTING);
        } catch (IOException e) {
            current = null;
            throw e;
        }
        this.password = pass;
        this.notifyObservers(password);
    }

    /**
     * load the password hash from the password file
     *
     * This method loads the credential in the password file into buffer. If the
     * password file does not exists, it will be created. If the password exists
     * and contains valid information, this method will ready the authenticator
     * for {@link OpenBSDBCryptAuthManager#verify(String)}.
     *
     * @throws IOException if failed to read hash from password file
     */
    @Override
    public void load() throws IOException {
        try {
            current = new String(Files.readAllBytes(passwdFile), StandardCharsets.US_ASCII);
        } catch (IOException e) {
            throw e;
        }

    }

    /**
     * gets the set password
     *
     * This method returns the valid password of the user. It can only return a
     * valid result if the {@link OpenBSDBCryptAuthManager#setPassword(String)}
     * or a valid {@link OpenBSDBCryptAuthManager#verify(String)} is called.
     *
     * @return the valid user password if conditions met, otherwise null
     */
    @Override
    public String getPassword() {
        return password;
    }

    /**
     * verifies a password
     *
     * This method verifies a password input. It can only be called after a
     * successful {@link OpenBSDBCryptAuthManager#setPassword(String)} or a
     * {@link OpenBSDBCryptAuthManager#load()} with valid password file. If the
     * password is valid, the plain text password will be stored, which can be
     * accessed by {@link OpenBSDBCryptAuthManager#getPassword()}.
     *
     * @param pass the password to verify
     * @return true if valid, false otherwise
     *
     */
    @Override
    public boolean verify(String pass) {
        if (current == null) {
            return false;
        }
        boolean result = OpenBSDBCrypt.checkPassword(current, pass.toCharArray());
        if (result) {
            this.password = pass;
            this.notifyObservers(password);
        }
        return result;
    }

    @Override
    public boolean isSetup() {
        return this.passwdFile.toFile().exists();
    }

}
