package net.viperfish.journal.swtGui;

import java.io.File;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;

import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.preference.PreferenceDialog;
import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.ColumnLabelProvider;
import org.eclipse.jface.viewers.DoubleClickEvent;
import org.eclipse.jface.viewers.IDoubleClickListener;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.TableViewerColumn;
import org.eclipse.jface.window.Window;
import org.eclipse.nebula.widgets.pagination.collections.PageResultContentProvider;
import org.eclipse.nebula.widgets.pagination.collections.PageResultLoaderList;
import org.eclipse.nebula.widgets.pagination.renderers.navigation.ResultAndNavigationPageLinksRendererFactory;
import org.eclipse.nebula.widgets.pagination.table.PageableTable;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ControlAdapter;
import org.eclipse.swt.events.ControlEvent;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.DateTime;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.Text;
import org.eclipse.swt.widgets.ToolBar;
import org.eclipse.swt.widgets.ToolItem;
import org.eclipse.wb.swt.SWTResourceManager;

import net.viperfish.journal.framework.Journal;
import net.viperfish.journal.framework.JournalPointer;
import net.viperfish.journal.framework.OperationExecutor;
import net.viperfish.journal.framework.OperationExecutors;
import net.viperfish.journal.framework.OperationFactory;
import net.viperfish.journal.framework.OperationWithResult;
import net.viperfish.journal.framework.provider.PreferenceGUIManager;
import net.viperfish.journal.operation.OperationFactories;
import net.viperfish.journal.operation.StoreConfigurationBufferOperation;
import net.viperfish.utils.time.TimeUtils;

public class JournalWindow {

	private class SearchJournal {

		private Date datePickerToDate(DateTime dt) {
			Calendar cal = Calendar.getInstance();
			cal.set(dt.getYear(), dt.getMonth(), dt.getDay());
			return cal.getTime();
		}

		private void setPagination(Collection<JournalPointer> data) {
			pgTable.setPageLoader(new PageResultLoaderList<>(new LinkedList<>(data)));
			pgTable.refreshPage(true);
		}

		public void displayAll() {
			tableViewer.setInput(null);
			OperationWithResult<? extends Collection<JournalPointer>> result = f.getListAllOperation();
			e.submit(result);
			Date min = null;
			List<JournalPointer> resultList = new LinkedList<>();
			for (JournalPointer i : result.getResult()) {
				if (min == null || min.after(i.getDate())) {
					min = i.getDate();
				}
				resultList.add(i);
			}
			setPagination(resultList);
			Calendar cal = Calendar.getInstance();
			if (min != null) {
				cal.setTime(min);
			}
			lowerBound.setDate(cal.get(Calendar.YEAR), cal.get(Calendar.MONTH), cal.get(Calendar.DAY_OF_MONTH));
			cal.setTime(new Date());
			upperBoound.setDate(cal.get(Calendar.YEAR), cal.get(Calendar.MONTH), cal.get(Calendar.DAY_OF_MONTH));
		}

		public void displayFiltered() {
			tableViewer.setInput(null);
			Date lower = TimeUtils.truncDate(datePickerToDate(lowerBound));
			Date upper = TimeUtils.truncDate(datePickerToDate(upperBoound));
			OperationWithResult<? extends Collection<JournalPointer>> getRange = f.getDateRangeOperation(lower, upper);
			e.submit(getRange);
			setPagination(getRange.getResult());
		}

		public void searchJournals() {
			if (searchText.getText().length() == 0) {
				displayFiltered();
				return;
			}
			tableViewer.setInput(null);
			Date lower = TimeUtils.truncDate(datePickerToDate(lowerBound));
			Date upper = TimeUtils.truncDate(datePickerToDate(upperBoound));
			OperationWithResult<? extends Collection<JournalPointer>> search = f
					.getDateRangeSearchOperation(searchText.getText(), lower, upper);
			e.submit(search);
			setPagination(search.getResult());
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

	private Text searchText;
	private Display display;
	private Shell shell;
	private ToolBar operationBar;
	private ToolItem newJournal;
	private ToolItem deleteJournal;
	private SearchJournal search;
	private OperationExecutor e;
	private OperationFactory f;
	private Table searchResults;
	private TableViewer tableViewer;
	private ExceptionDisplayer errorReporter;
	private Label recentLabel;
	private DateTime lowerBound;
	private DateTime upperBoound;
	private PageableTable pgTable;

	public JournalWindow() {
		e = OperationExecutors.getExecutor();
		f = OperationFactories.getOperationFactory();
	}

	private void newJournal() {
		Journal result = new JournalEditor().open(new Journal());
		if (result == null) {
			return;
		}
		e.submit(f.getAddOperation(result));
		search.searchJournals();
	}

	private void deleteJournal() {
		StructuredSelection selected = (StructuredSelection) tableViewer.getSelection();
		if (selected.isEmpty()) {
			return;
		}
		boolean toDelete = MessageDialog.openConfirm(shell, "Confirm", "THIS DELETION CANNOT BE UNDONE. Delete?");
		if (toDelete) {
			JournalPointer s = (JournalPointer) selected.getFirstElement();
			e.submit(f.getDeleteOperation(s.getId()));
			search.searchJournals();
		}
	}

	/**
	 * Open the window.
	 * 
	 * @wbp.parser.entryPoint
	 */
	public void open() {
		display = Display.getDefault();
		shell = new Shell();
		shell.setImage(SWTResourceManager.getImage(JournalWindow.class, "/logo.ico"));
		shell.setSize(495, 480);
		shell.setText("vsDiary - 4.2.1");
		shell.setLayout(new GridLayout(8, false));

		errorReporter = new ExceptionDisplayer(shell);

		e.addObserver(errorReporter);

		searchText = new Text(shell, SWT.BORDER);
		searchText.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 8, 1));

		operationBar = new ToolBar(shell, SWT.FLAT | SWT.RIGHT);
		operationBar.setLayoutData(new GridData(SWT.LEFT, SWT.CENTER, false, false, 8, 1));

		newJournal = new ToolItem(operationBar, SWT.NONE);
		newJournal.setText("New");

		deleteJournal = new ToolItem(operationBar, SWT.NONE);
		deleteJournal.setText("Delete");

		recentLabel = new Label(shell, SWT.NONE);
		recentLabel.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
		recentLabel.setText("Entries:");

		lowerBound = new DateTime(shell, SWT.DROP_DOWN);

		Label lblTo = new Label(shell, SWT.NONE);
		lblTo.setText("To");

		upperBoound = new DateTime(shell, SWT.DROP_DOWN);

		search = new SearchJournal();

		lowerBound.addSelectionListener(search.createSelectAdapter());
		upperBoound.addSelectionListener(search.createSelectAdapter());
		searchText.addModifyListener(search.createModifyAdapter());
		newJournal.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent arg0) {
				newJournal();
			}

		});

		deleteJournal.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent arg0) {
				deleteJournal();
			}

		});

		Menu mainMenu = new Menu(shell, SWT.BAR);
		shell.setMenuBar(mainMenu);

		MenuItem fileMenu = new MenuItem(mainMenu, SWT.CASCADE);
		fileMenu.setText("File");

		Menu menu = new Menu(fileMenu);
		fileMenu.setMenu(menu);

		MenuItem newEntryMenu = new MenuItem(menu, SWT.NONE);
		newEntryMenu.setText("New Entry");
		newEntryMenu.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent e) {
				super.widgetSelected(e);
				newJournal();
			}

		});

		MenuItem exportMenu = new MenuItem(menu, SWT.NONE);
		exportMenu.setText("Export");
		exportMenu.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent e) {
				super.widgetSelected(e);
				FileDialog exportSelector = new FileDialog(shell);
				exportSelector.setOverwrite(true);
				exportSelector.setText("Enter a location to export");
				exportSelector.open();
				if (exportSelector.getFileName() == null || exportSelector.getFileName().length() == 0) {
					return;
				}
				String toExport = new File(exportSelector.getFilterPath(), exportSelector.getFileName())
						.getAbsolutePath();
				if (toExport != null) {
					JournalWindow.this.e.submit(f.getExportEntriesOperation(toExport));
				}
			}

		});

		MenuItem mntmImport = new MenuItem(menu, SWT.NONE);
		mntmImport.setText("Import");

		MenuItem exit = new MenuItem(menu, SWT.NONE);
		exit.setText("Exit");
		exit.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent arg0) {
				shell.dispose();
			}

		});

		mntmImport.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent e) {
				super.widgetSelected(e);
				FileDialog selecter = new FileDialog(shell);
				selecter.setText("Select the file to import");
				selecter.open();
				if (selecter.getFileName() == null || selecter.getFileName().length() == 0) {
					return;
				}
				String selected = new File(selecter.getFilterPath(), selecter.getFileName()).getAbsolutePath();
				if (selected == null || selected.length() == 0) {
					return;
				}
				JournalWindow.this.e.submit(f.getImportEntriesOperation(selected));
				search.displayAll();
				searchText.setText("");
			}

		});

		MenuItem editMenu = new MenuItem(mainMenu, SWT.CASCADE);
		editMenu.setText("Edit");

		Menu menu_1 = new Menu(editMenu);
		editMenu.setMenu(menu_1);

		MenuItem deleteEntryMenu = new MenuItem(menu_1, SWT.NONE);
		deleteEntryMenu.setText("Delete Entry");
		deleteEntryMenu.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent e) {
				super.widgetSelected(e);
				deleteJournal();
			}

		});

		MenuItem clearEntrieMenu = new MenuItem(menu_1, SWT.NONE);
		clearEntrieMenu.setText("Clear All");
		clearEntrieMenu.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent e) {
				super.widgetSelected(e);
				boolean toClear = MessageDialog.openConfirm(shell, "Confirm",
						"THIS ACTION CANNOT BE UNDONE. Clear All?");
				if (toClear) {
					JournalWindow.this.e.submit(f.getClearEntriesOperation());
					search.searchJournals();
				}
			}

		});

		MenuItem showAllMenu = new MenuItem(menu_1, SWT.NONE);
		showAllMenu.setText("Show All");
		showAllMenu.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent arg0) {
				search.displayAll();
			}

		});

		MenuItem preferenceMenu = new MenuItem(mainMenu, SWT.CASCADE);
		preferenceMenu.setText("Settings");

		Menu settingMenu = new Menu(preferenceMenu);
		preferenceMenu.setMenu(settingMenu);

		MenuItem changeConfigMenu = new MenuItem(settingMenu, SWT.NONE);
		changeConfigMenu.setText("Configuration");
		changeConfigMenu.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent arg0) {
				super.widgetSelected(arg0);
				PreferenceDialog pd = new PreferenceDialog(shell, PreferenceGUIManager.getMger());
				int result = pd.open();
				if (result == Window.OK) {
					JournalWindow.this.e.submit(StoreConfigurationBufferOperation.applyOperation());
				}
			}

		});

		MenuItem passwordMenu = new MenuItem(settingMenu, SWT.NONE);
		passwordMenu.setText("Password");
		passwordMenu.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent arg0) {
				super.widgetSelected(arg0);
				ChangePasswordPrompt prompt = new ChangePasswordPrompt();
				prompt.open();
			}

		});

		MenuItem helpMenu = new MenuItem(mainMenu, SWT.CASCADE);
		helpMenu.setText("Help");

		Menu menu_2 = new Menu(helpMenu);
		helpMenu.setMenu(menu_2);

		MenuItem aboutMenu = new MenuItem(menu_2, SWT.NONE);
		aboutMenu.setText("About");
		new Label(shell, SWT.NONE);
		new Label(shell, SWT.NONE);
		new Label(shell, SWT.NONE);
		new Label(shell, SWT.NONE);

		pgTable = new PageableTable(shell, SWT.NONE, SWT.BORDER | SWT.MULTI | SWT.H_SCROLL | SWT.V_SCROLL, 10,
				PageResultContentProvider.getInstance(), null,
				ResultAndNavigationPageLinksRendererFactory.getFactory());
		GridData gd_pgTable = new GridData(SWT.FILL, SWT.FILL, true, true);
		gd_pgTable.horizontalSpan = 8;
		pgTable.setLayoutData(gd_pgTable);

		tableViewer = pgTable.getViewer();
		searchResults = tableViewer.getTable();
		searchResults.setLayoutData(new GridData(GridData.FILL_BOTH));
		tableViewer.setContentProvider(new ArrayContentProvider());
		searchResults.setHeaderVisible(true);
		searchResults.setLinesVisible(true);
		final TableViewerColumn titles = new TableViewerColumn(tableViewer, SWT.NONE);
		titles.getColumn().setWidth(200);
		titles.getColumn().setText("Title");
		titles.getColumn().setResizable(true);
		titles.setLabelProvider(new ColumnLabelProvider() {
			@Override
			public String getText(Object element) {
				JournalPointer j = (JournalPointer) element;
				return j.getTitle();
			}
		});
		final TableViewerColumn dates = new TableViewerColumn(tableViewer, SWT.NONE);
		dates.getColumn().setWidth(200);
		dates.getColumn().setResizable(true);
		dates.getColumn().setText("Date");
		dates.setLabelProvider(new ColumnLabelProvider() {
			@Override
			public String getText(Object element) {
				JournalPointer j = (JournalPointer) element;
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
				JournalPointer pointer = (JournalPointer) selected.getFirstElement();
				OperationWithResult<Journal> get = f.getGetEntryOperation(pointer.getId());
				e.submit(get);
				Journal result = new JournalEditor().open(get.getResult());
				if (result == null) {
					return;
				}
				e.submit(f.getEditContentOperation(result.getId(), result.getContent()));
				e.submit(f.getEditSubjectOperation(result.getId(), result.getSubject()));
				search.searchJournals();
			}
		});
		aboutMenu.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent e) {
				super.widgetSelected(e);
				MessageDialog.openInformation(shell, "About",
						"This is an application to write secure and authenticated diary digitally");
			}

		});

		shell.addControlListener(new ControlAdapter() {

			@Override
			public void controlResized(ControlEvent arg0) {
				// TODO Auto-generated method stub
				super.controlResized(arg0);
				Rectangle area = shell.getClientArea();
				Point preferredSize = searchResults.computeSize(SWT.DEFAULT, SWT.DEFAULT);
				int width = area.width - searchResults.getBorderWidth() * 2;
				if (preferredSize.y > area.height + searchResults.getHeaderHeight()) {
					// Subtract the scrollbar width from the total column width
					// if a vertical scrollbar will be required
					Point vBarSize = searchResults.getVerticalBar().getSize();
					width -= vBarSize.x;
				}
				Point oldSize = searchResults.getSize();
				if (oldSize.x > area.width) {
					// table is getting smaller so make the columns
					// smaller first and then resize the table to
					// match the client area width
					titles.getColumn().setWidth(width / 2);
					dates.getColumn().setWidth(width - titles.getColumn().getWidth() - 25);
				} else {
					// table is getting bigger so make the table
					// bigger first and then make the columns wider
					// to match the client area width
					titles.getColumn().setWidth(width / 2);
					dates.getColumn().setWidth(width - titles.getColumn().getWidth() - 25);
				}
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
}
