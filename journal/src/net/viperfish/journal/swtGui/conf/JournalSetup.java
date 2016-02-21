package net.viperfish.journal.swtGui.conf;

import java.util.LinkedList;

import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.ListViewer;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.List;
import org.eclipse.swt.widgets.Shell;

import net.viperfish.journal.framework.ConfigMapping;
import net.viperfish.journal.framework.Configuration;

public class JournalSetup {

	private ListViewer preferenceList;
	private GridData displayLayout;
	private Composite current;
	private Shell setupWindow;
	private LinkedList<ConfigPage> pages;
	private Button doneButton;
	private boolean isComplete;

	public JournalSetup() {
		isComplete = false;
		current = null;
		pages = new LinkedList<>();
	}

	/**
	 * Open the window.
	 * 
	 * @wbp.parser.entryPoint
	 */
	public boolean open() {
		Display display = Display.getDefault();
		setupWindow = new Shell();
		setupWindow.setSize(700, 345);
		setupWindow.setText("Setup");
		setupWindow.setLayout(new GridLayout(17, false));

		preferenceList = new ListViewer(setupWindow, SWT.BORDER | SWT.V_SCROLL);
		List list = preferenceList.getList();
		GridData gd_list = new GridData(SWT.LEFT, SWT.CENTER, false, false, 1, 1);
		gd_list.widthHint = 151;
		gd_list.heightHint = 258;
		list.setLayoutData(gd_list);
		displayLayout = new GridData(SWT.LEFT, SWT.CENTER, true, false, 15, 1);
		displayLayout.widthHint = 500;
		displayLayout.exclude = false;

		preferenceList.setLabelProvider(new LabelProvider() {

			@Override
			public String getText(Object element) {
				return ((ConfigPage) element).getName();
			}

		});

		preferenceList.addSelectionChangedListener(new ISelectionChangedListener() {

			@Override
			public void selectionChanged(SelectionChangedEvent arg0) {
				ConfigPage p = ((ConfigPage) ((StructuredSelection) arg0.getSelection()).getFirstElement());
				if (current != null) {
					hideCurrent();
				}
				current = p.getDisplay();
				showCurrent();

			}
		});

		for (String i : Configuration.getStringArray(ConfigMapping.CONFIG_PAGES)) {
			try {
				ConfigPage p = (ConfigPage) Class.forName(i).newInstance();
				p.setParent(setupWindow);
				current = p.getDisplay();
				current.setLayoutData(this.displayLayout);
				hideCurrent();
				preferenceList.add(p);
				pages.add(p);
			} catch (InstantiationException | IllegalAccessException | ClassNotFoundException e) {
				System.err.println("Failed to load preference page " + i + ":" + e.getMessage());
				e.printStackTrace();
			}
		}

		current = pages.getFirst().getDisplay();
		showCurrent();
		doneButton = new Button(setupWindow, SWT.NONE);
		doneButton.setAlignment(SWT.BOTTOM | SWT.RIGHT);
		doneButton.setText("Done");
		doneButton.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent e) {
				for (ConfigPage p : pages) {
					p.done();
				}
				isComplete = true;
				setupWindow.dispose();
			}

		});

		preferenceList.getList().select(0);
		setupWindow.open();
		setupWindow.layout();
		while (!setupWindow.isDisposed()) {
			if (!display.readAndDispatch()) {
				display.sleep();
			}
		}
		return isComplete;
	}

	private void hideCurrent() {
		current.setVisible(false);
		current.setLayoutData(new GridData(0, 0));
		setupWindow.layout();
	}

	private void showCurrent() {
		current.setVisible(true);
		current.setLayoutData(displayLayout);
		current.pack();
		setupWindow.layout();
	}

}
