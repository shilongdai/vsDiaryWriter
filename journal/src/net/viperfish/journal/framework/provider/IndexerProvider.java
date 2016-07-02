package net.viperfish.journal.framework.provider;

import net.viperfish.framework.index.Indexer;
import net.viperfish.journal.framework.Journal;
import ro.fortsoft.pf4j.ExtensionPoint;

public interface IndexerProvider extends Provider<Indexer<Journal>>, ExtensionPoint {

}
