package net.viperfish.journal.framework.provider;

import net.viperfish.journal.framework.EntryDatabase;
import ro.fortsoft.pf4j.ExtensionPoint;

public interface DatabaseProvider extends Provider<EntryDatabase>, ExtensionPoint {

}
