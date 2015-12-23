package net.viperfish.swtGui;

import java.util.Set;

import org.eclipse.jface.viewers.ListViewer;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.widgets.Text;

import net.viperfish.journal.JournalApplication;
import net.viperfish.journal.framework.Journal;
import net.viperfish.journal.framework.OperationExecutor;
import net.viperfish.journal.framework.OperationFactory;
import net.viperfish.journal.framework.OperationWithResult;

public class SearchJournal {
	private ListViewer renderer;
	private OperationExecutor e;
	private OperationFactory f;
	private Text search;

	public SearchJournal(Text searchField, ListViewer render) {
		this.search = searchField;
		this.renderer = render;
		f = JournalApplication.getOperationFactory();
		e = JournalApplication.getWorker();
	}

	public void searchJournals() {
		renderer.setInput(null);
		OperationWithResult<Set<Journal>> search = f.getSearchOperation(this.search.getText());
		e.submit(search);
		for (Journal i : search.getResult()) {
			renderer.add(i);
		}
	}

	private class SearchSelectionAdapter extends SelectionAdapter {

		@Override
		public void widgetSelected(SelectionEvent arg0) {
			searchJournals();
		}

	}

	private class SearchTextChangeAdapter implements ModifyListener {

		@Override
		public void modifyText(ModifyEvent arg0) {
			searchJournals();
		}

	}

	public SelectionListener createSelectAdapter() {
		return new SearchSelectionAdapter();
	}

	public ModifyListener createModifyAdapter() {
		return new SearchTextChangeAdapter();
	}
}
