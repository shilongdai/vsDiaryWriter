package net.viperfish.journal.auth;

public interface AuthenticationManager {

	void clear();

	void setPassword(String pass);

	void reload();

	boolean verify(String string);

	boolean isPasswordSet();

}
