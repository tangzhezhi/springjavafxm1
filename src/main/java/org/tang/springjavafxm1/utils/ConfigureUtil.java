package org.tang.springjavafxm1.utils;

import java.io.File;

public class ConfigureUtil {

	public static String getConfigurePath(String fileName) {
		return System.getProperty("user.home") + "/tang/" + fileName;
	}

	public static File getConfigureFile(String fileName) {
		return new File(getConfigurePath(fileName));
	}
}
