package net.viperfish.journal.framework.provider;

import net.viperfish.journal.framework.AuthenticationManager;
import ro.fortsoft.pf4j.ExtensionPoint;

public interface AuthProvider extends Provider<AuthenticationManager>, ExtensionPoint {

}
