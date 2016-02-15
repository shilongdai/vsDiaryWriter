package net.viperfish.journal.swtGui;

import java.text.DateFormat;
import java.text.SimpleDateFormat;

import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.ColumnLabelProvider;
import org.eclipse.jface.viewers.DoubleClickEvent;
import org.eclipse.jface.viewers.IDoubleClickListener;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.TableViewerColumn;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.DisposeEvent;
import org.eclipse.swt.events.DisposeListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.Text;
import org.eclipse.swt.widgets.ToolBar;
import org.eclipse.swt.widgets.ToolItem;

import net.viperfish.journal.JournalApplication;
import net.viperfish.journal.framework.Journal;
import net.viperfish.journal.ui.OperationExecutor;
import net.viperfish.journal.ui.OperationFactory;

public class JournalWindow {

	private Text text;
	private Display display;
	private Shell shell;
	private Button searchButton;
	private ToolBar toolBar;
	private ToolItem newJournal;
	private ToolItem deleteJournal;
	private SearchJournal search;
	private OperationExecutor e;
	private OperationFactory f;
	private Table table;
	private TableViewer tableViewer;

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
		shell.setText("vJournal - special, alpha version");
		shell.setLayout(new GridLayout(13, false));
		shell.addDisposeListener(new DisposeListener() {

			@Override
			public void widgetDisposed(DisposeEvent e) {
				JournalApplication.cleanUp();
				// System.exit(0);
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

		tableViewer = new TableViewer(shell, SWT.MULTI | SWT.H_SCROLL | SWT.V_SCROLL | SWT.FULL_SELECTION | SWT.BORDER);
		table = tableViewer.getTable();
		GridData gd_table = new GridData(SWT.FILL, SWT.FILL, true, true, 13, 1);
		gd_table.widthHint = 368;
		table.setLayoutData(gd_table);
		tableViewer.setContentProvider(new ArrayContentProvider());
		table.setHeaderVisible(true);
		table.setLinesVisible(true);
		TableViewerColumn titles = new TableViewerColumn(tableViewer, SWT.NONE);
		titles.getColumn().setWidth(300);
		titles.getColumn().setText("Title");
		titles.getColumn().setResizable(true);
		titles.setLabelProvider(new ColumnLabelProvider() {
			@Override
			public String getText(Object element) {
				Journal j = (Journal) element;
				return j.getSubject();
			}
		});
		TableViewerColumn dates = new TableViewerColumn(tableViewer, SWT.NONE);
		dates.getColumn().setWidth(100);
		dates.getColumn().setResizable(true);
		dates.getColumn().setText("Date");
		dates.setLabelProvider(new ColumnLabelProvider() {
			@Override
			public String getText(Object element) {
				Journal j = (Journal) element;
				DateFormat df = new SimpleDateFormat("MM/dd/yyyy");
				return df.format(j.getDate());
			}
		});

		tableViewer.addDoubleClickListener(new IDoubleClickListener() {

			@Override
			public void doubleClick(DoubleClickEvent arg0) {
				StructuredSelection selected = (StructuredSelection) arg0.getSelection();
				if (selected.isEmpty()) {
					return;
				}
				Journal result = new JournalEditor().open((Journal) selected.getFirstElement());
				if (result == null) {
					return;
				}
				e.submit(f.getEditContentOperation(result.getId(), result.getContent()));
				e.submit(f.getEditSubjectOperation(result.getId(), result.getSubject()));
				handleException();
			}
		});

		search = new SearchJournal(text, tableViewer);

		searchButton.addSelectionListener(search.createSelectAdapter());
		text.addModifyListener(search.createModifyAdapter());
		newJournal.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent arg0) {
				Journal result = new JournalEditor().open(new Journal());
				if (result == null) {
					return;
				}
				e.submit(f.getAddOperation(result));
				search.searchJournals();
				handleException();
			}

		});

		deleteJournal.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent arg0) {
				StructuredSelection selected = (StructuredSelection) tableViewer.getSelection();
				if (selected.isEmpty()) {
					return;
				}
				Journal s = (Journal) selected.getFirstElement();
				e.submit(f.getDeleteOperation(s.getId()));
				search.searchJournals();
				handleException();
			}

		});

		search.displayAll();

		shell.open();
		shell.layout();
		while (!shell.isDisposed()) {
			if (!display.readAndDispatch()) {
				display.sleep();
			}
		}
	}

	private void handleException() {
		while (e.hasException()) {
			displayException(e.getNextError());
		}
	}

	private void displayException(Throwable e) {
		ErrorDialog dlg = new ErrorDialog(shell, SWT.None);
		dlg.open(e);
	}
}
