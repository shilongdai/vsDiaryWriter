package net.viperfish.journal2.core;

import java.io.IOException;
import net.viperfish.journal2.error.CannotClearPasswordException;
import net.viperfish.journal2.error.FailToLoadCredentialException;
import net.viperfish.journal2.error.FailToStoreCredentialException;

/**
 * An authenticator responsible for storing and validating a password
 *
 * A class implementing this interface should be able to store and validate
 * password for authenticating user unlocking the journal as well as other
 * components who needs to use the plain text password.
 *
 * This class <b>DOES NOT</b> have to be thread safe.
 *
 * @author sdai
 *
 */
public interface AuthenticationManager {

    /**
     * clear the current password
     *
     * This method should clear any stored password information and reset this
     * object to the state before the first
     * {@link AuthenticationManager#setPassword(String)} is called.
     *
     * @throws CannotClearPasswordException if cannot clear password
     */
    void clear() throws IOException;

    /**
     * set a new password
     *
     * This method should set the password so that a call to
     * {@link AuthenticationManager#getPassword()} returns the valid plain text
     * password
     *
     * @param pass the password to set
     *
     * @throws FailToStoreCredentialException if cannot store password
     */
    void setPassword(String pass) throws IOException;

    /**
     * load the stored password information
     *
     * This method should load password information so that
     * {@link AuthenticationManager#verify(String)} is available.
     *
     * @throws FailToLoadCredentialException if failed to load information
     */
    void load() throws IOException;

    /**
     * get the plain text password
     *
     * This method should return the plain text password set by the last
     * {@link AuthenticationManager#setPassword(String)}.
     *
     * @return the plain password
     */
    public String getPassword();

    /**
     * authenticates a password
     *
     * This method should authenticate a plain text password, verifying that it
     * matches the password set by the last
     * {@link AuthenticationManager#setPassword(String)}
     *
     * @param string the password
     * @return whether the password is correct
     */
    boolean verify(String string);

    boolean isSetup();

}
