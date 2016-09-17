package net.viperfish.journal2.core;

import org.eclipse.jface.preference.PreferencePage;
import org.eclipse.jface.resource.ImageDescriptor;

public abstract class CopiablePreferencePage extends PreferencePage {

	public CopiablePreferencePage() {
		super();
	}

	public CopiablePreferencePage(String title) {
		super(title);
	}

	public CopiablePreferencePage(String title, ImageDescriptor image) {
		super(title, image);
	}

	public abstract PreferencePage copy();

}
