package net.viperfish.journal.swtGui;

import org.eclipse.jface.viewers.DoubleClickEvent;
import org.eclipse.jface.viewers.IDoubleClickListener;
import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.ListViewer;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.DisposeEvent;
import org.eclipse.swt.events.DisposeListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.List;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;
import org.eclipse.swt.widgets.ToolBar;
import org.eclipse.swt.widgets.ToolItem;

import net.viperfish.journal.JournalApplication;
import net.viperfish.journal.framework.Journal;
import net.viperfish.journal.framework.OperationExecutor;
import net.viperfish.journal.framework.OperationFactory;

public class JournalWindow {
	private static class ContentProvider implements IStructuredContentProvider {
		@Override
		public Object[] getElements(Object inputElement) {
			return new Object[0];
		}

		@Override
		public void dispose() {
		}

		@Override
		public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {
		}
	}

	private Text text;
	private Display display;
	private Shell shell;
	private Button searchButton;
	private ToolBar toolBar;
	private ToolItem newJournal;
	private ToolItem deleteJournal;
	private List journalList;
	private ListViewer listBinder;
	private SearchJournal search;
	private OperationExecutor e;
	private OperationFactory f;

	public JournalWindow() {
		e = JournalApplication.getWorker();
		f = JournalApplication.getOperationFactory();
	}

	/**
	 * Open the window.
	 * 
	 * @wbp.parser.entryPoint
	 */
	public void open() {
		display = Display.getDefault();
		shell = new Shell();
		shell.setSize(450, 400);
		shell.setText("vJournal");
		shell.setLayout(new GridLayout(13, false));
		shell.addDisposeListener(new DisposeListener() {

			@Override
			public void widgetDisposed(DisposeEvent e) {
				JournalApplication.cleanUp();
			}
		});
		text = new Text(shell, SWT.BORDER);
		text.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 12, 1));

		searchButton = new Button(shell, SWT.NONE);
		searchButton.setText("Search");

		toolBar = new ToolBar(shell, SWT.FLAT | SWT.RIGHT);
		toolBar.setLayoutData(new GridData(SWT.LEFT, SWT.CENTER, false, false, 13, 1));

		newJournal = new ToolItem(toolBar, SWT.NONE);
		newJournal.setText("New");

		deleteJournal = new ToolItem(toolBar, SWT.NONE);
		deleteJournal.setText("Delete");

		journalList = new List(shell, SWT.BORDER);
		GridData gd_journalList = new GridData(SWT.LEFT, SWT.CENTER, false, false, 13, 1);
		gd_journalList.heightHint = 289;
		gd_journalList.widthHint = 433;
		journalList.setLayoutData(gd_journalList);
		listBinder = new ListViewer(journalList);

		search = new SearchJournal(text, listBinder);
		listBinder.setContentProvider(new ContentProvider());
		searchButton.addSelectionListener(search.createSelectAdapter());
		text.addModifyListener(search.createModifyAdapter());

		newJournal.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent arg0) {
				Journal result = new JournalEditor(shell, SWT.None).open(new Journal());
				if (result == null) {
					return;
				}
				e.submit(f.getAddOperation(result));
			}

		});

		listBinder.addDoubleClickListener(new IDoubleClickListener() {

			@Override
			public void doubleClick(DoubleClickEvent arg0) {
				StructuredSelection selected = (StructuredSelection) arg0.getSelection();
				if (selected.isEmpty()) {
					return;
				}
				Journal result = new JournalEditor(shell, SWT.None).open((Journal) selected.getFirstElement());
				if (result == null) {
					return;
				}
				e.submit(f.getEditContentOperation(result.getId(), result.getContent()));
				e.submit(f.getEditSubjectOperation(result.getId(), result.getSubject()));
			}
		});

		deleteJournal.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent arg0) {
				StructuredSelection selected = (StructuredSelection) listBinder.getSelection();
				if (selected.isEmpty()) {
					return;
				}
				Journal s = (Journal) selected.getFirstElement();
				e.submit(f.getDeleteOperation(s.getId()));
				search.searchJournals();
			}

		});

		shell.open();
		shell.layout();
		while (!shell.isDisposed()) {
			if (!display.readAndDispatch()) {
				display.sleep();
			}
		}
	}
}
