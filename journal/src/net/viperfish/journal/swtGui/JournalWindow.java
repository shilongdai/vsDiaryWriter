package net.viperfish.journal.swtGui;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.MultiStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.dialogs.ErrorDialog;
import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.ColumnLabelProvider;
import org.eclipse.jface.viewers.DoubleClickEvent;
import org.eclipse.jface.viewers.IDoubleClickListener;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.TableViewerColumn;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ControlAdapter;
import org.eclipse.swt.events.ControlEvent;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
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
		shell.setText("vJournal - release 1.1");
		shell.setLayout(new GridLayout(13, false));
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
		final TableViewerColumn titles = new TableViewerColumn(tableViewer, SWT.NONE);
		titles.getColumn().setWidth(200);
		titles.getColumn().setText("Title");
		titles.getColumn().setResizable(true);
		titles.setLabelProvider(new ColumnLabelProvider() {
			@Override
			public String getText(Object element) {
				Journal j = (Journal) element;
				return j.getSubject();
			}
		});
		final TableViewerColumn dates = new TableViewerColumn(tableViewer, SWT.NONE);
		dates.getColumn().setWidth(200);
		dates.getColumn().setResizable(true);
		dates.getColumn().setText("Date");
		dates.setLabelProvider(new ColumnLabelProvider() {
			@Override
			public String getText(Object element) {
				Journal j = (Journal) element;
				DateFormat df = new SimpleDateFormat("EEE h:mm a MM/dd/yyyy");
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

		shell.addControlListener(new ControlAdapter() {

			@Override
			public void controlResized(ControlEvent arg0) {
				// TODO Auto-generated method stub
				super.controlResized(arg0);
				Rectangle area = shell.getClientArea();
				Point preferredSize = table.computeSize(SWT.DEFAULT, SWT.DEFAULT);
				int width = area.width - table.getBorderWidth() * 2;
				if (preferredSize.y > area.height + table.getHeaderHeight()) {
					// Subtract the scrollbar width from the total column width
					// if a vertical scrollbar will be required
					Point vBarSize = table.getVerticalBar().getSize();
					width -= vBarSize.x;
				}
				Point oldSize = table.getSize();
				if (oldSize.x > area.width) {
					// table is getting smaller so make the columns
					// smaller first and then resize the table to
					// match the client area width
					titles.getColumn().setWidth(width / 2);
					dates.getColumn().setWidth(width - titles.getColumn().getWidth() - 20);
				} else {
					// table is getting bigger so make the table
					// bigger first and then make the columns wider
					// to match the client area width
					titles.getColumn().setWidth(width / 2);
					dates.getColumn().setWidth(width - titles.getColumn().getWidth() - 20);
				}
			}

		});

		shell.setDefaultButton(searchButton);
		shell.open();
		shell.layout();
		while (!shell.isDisposed())

		{
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

	private MultiStatus createMultiStatus(String msg, Throwable t) {

		List<Status> childStatuses = new ArrayList<>();
		StackTraceElement[] stackTraces = Thread.currentThread().getStackTrace();

		for (StackTraceElement stackTrace : stackTraces) {
			Status status = new Status(IStatus.ERROR, "net.viperfish.journal.swtGui", stackTrace.toString());
			childStatuses.add(status);
		}

		MultiStatus ms = new MultiStatus("net.viperfish.journal.swtGui", IStatus.ERROR,
				childStatuses.toArray(new Status[] {}), t.toString(), t);
		return ms;
	}

	private void displayException(Throwable e) {
		MultiStatus status = createMultiStatus(e.getLocalizedMessage(), e);
		ErrorDialog.openError(shell, "Error Occured",
				"An Error has occured. If you would, please send the error to the developer at viperfish.net so we can improve the application",
				status);
	}
}
