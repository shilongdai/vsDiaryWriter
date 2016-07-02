package net.viperfish.journal.framework.provider;

import net.viperfish.journal.framework.JournalTransformer;
import ro.fortsoft.pf4j.ExtensionPoint;

public interface TransformerProvider extends Provider<JournalTransformer>, ExtensionPoint {

}
