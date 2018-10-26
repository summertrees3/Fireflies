package com.hoperun.excel;

import java.beans.IntrospectionException;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.LinkedList;

import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.poifs.filesystem.POIFSFileSystem;

public class XslParse {

	@SuppressWarnings("rawtypes")
	public LinkedList<Object> parse(String filePath, Class classObject) throws FileNotFoundException, IOException,
			IntrospectionException, IllegalAccessException, IllegalArgumentException, InvocationTargetException,
			InstantiationException, NoSuchMethodException, SecurityException {
		LinkedList<Object> list = new LinkedList<Object>();
		Reflect reflect = new Reflect();
		File file = new File(filePath);// 获取文件
		// 解析xsl文件
		POIFSFileSystem pfs = new POIFSFileSystem(new FileInputStream(file));
		HSSFWorkbook hwb = new HSSFWorkbook(pfs);
		HSSFSheet hs = hwb.getSheetAt(0);// 获取第0个sheet页的数据
		int rowStart = hs.getFirstRowNum();// 获取第一行的行数 0
		int rowEnd = hs.getLastRowNum();// 获取最后一行的行数-1

		HSSFRow firstRow = hs.getRow(0);
		for (int i = rowStart + 1; i <= rowEnd; i++) {// 行的遍历
			Object obj = classObject.newInstance();

			HSSFRow hr = hs.getRow(i);// 获取行
			if (hr == null) {
				continue;
			}
			int cellStart = hr.getFirstCellNum();// 获取第一列
			int cellEnd = hr.getLastCellNum();// 获取列的总数

			for (int k = cellStart; k < cellEnd; k++) {// 列的遍历
				HSSFCell hc = hr.getCell(k);// 获取列
				if (hc == null || hc.equals("")) {
					continue;
				}
				switch (hc.getCellType()) {
				case HSSFCell.CELL_TYPE_STRING:// 字符串
					obj = reflect.reflect(obj, k, hc.getStringCellValue(), firstRow.getCell(k).getStringCellValue());
					break;
				case HSSFCell.CELL_TYPE_BOOLEAN:// boolean类型
					obj = reflect.reflect(obj, k, hc.getBooleanCellValue(), firstRow.getCell(k).getStringCellValue());
					break;
				case HSSFCell.CELL_TYPE_NUMERIC:// 数字
					obj = reflect.reflect(obj, k, hc.getNumericCellValue(), firstRow.getCell(k).getStringCellValue());
					break;
				case HSSFCell.CELL_TYPE_FORMULA:// 公式
					obj = reflect.reflect(obj, k, hc.getCellFormula(), firstRow.getCell(k).getStringCellValue());
					break;
				case HSSFCell.CELL_TYPE_BLANK:// 空值
					obj = reflect.reflect(obj, k, null, firstRow.getCell(k).getStringCellValue());
					break;
				case HSSFCell.CELL_TYPE_ERROR:// 错误
					obj = reflect.reflect(obj, k, null, firstRow.getCell(k).getStringCellValue());
					break;
				default:
					break;
				}
			}
			list.add(obj);
		}
		return list;
	}
}
