package net.viperfish.utils.file;

import java.io.File;
import java.io.IOException;

public class CommonFunctions {
	public static void initFile(File src) throws IOException {
		if (!src.exists()) {
			src.createNewFile();
		}
	}

	public static void delete(File root) {
		new RecursiveDelete().recursiveRm(root);
	}

	public static void initDir(File src) {
		if (!src.exists()) {
			src.mkdir();
		}
	}
}
