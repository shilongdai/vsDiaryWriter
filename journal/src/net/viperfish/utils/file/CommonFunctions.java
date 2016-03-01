package net.viperfish.utils.file;

import java.io.File;
import java.io.IOException;

public class CommonFunctions {
	public static boolean initFile(File src) throws IOException {
		if (!src.exists()) {
			src.createNewFile();
			return true;
		}
		return false;
	}

	public static void delete(File root) {
		new RecursiveDelete().recursiveRm(root);
	}

	public static boolean initDir(File src) {
		if (!src.exists()) {
			src.mkdirs();
			return true;
		}
		return false;
	}
}
