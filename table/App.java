package com.hoperun.excel;

import java.io.File;

import com.hoperun.beans.Worker;
import com.hoperun.util.ConfigUtil;

public class App {
	public static void main(String[] args) {
		if (args.length != 1) {
			System.out.println("提示: java -jar xxx.jar 文件名.xls");
			return;
		}
		String excelName = args[0];
		try {
			new Handler().handle(ConfigUtil.getPathConfig("excelDir") + File.separator + excelName, Worker.class);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	// /**
	// * 初始化路径目录
	// *
	// * @param pathkey
	// */
	// public static void createDir() {
	// File excelDir = new File(ConfigUtil.getPathConfig(EXCELDIR));
	// File logFileDir = new File(ConfigUtil.getPathConfig(LOGFILEDIR));
	// File idExcelDir = new File(ConfigUtil.getPathConfig(IDEXCELDIR));
	// if (!excelDir.exists()) {
	// excelDir.mkdir();
	// if (!logFileDir.exists()) {
	// logFileDir.mkdir();
	// }
	// if (!idExcelDir.exists()) {
	// idExcelDir.mkdir();
	// }
	// }
	//
	// }
}
