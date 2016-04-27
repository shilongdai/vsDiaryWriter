package net.viperfish.framework.file;

import java.io.File;

public final class RecursiveDelete {

	public void recursiveRm(File dir) {
		if (dir.isDirectory()) {
			for (File i : dir.listFiles()) {
				recursiveRm(i);
			}
		}
		dir.delete();
	}

}
