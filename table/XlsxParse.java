package com.hoperun.excel;

import java.beans.IntrospectionException;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.LinkedList;

import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

public class XlsxParse {

	@SuppressWarnings("rawtypes")
	public LinkedList<Object> parse(String filePath, Class classObject) throws FileNotFoundException, IOException,
			InvalidFormatException, InstantiationException, IllegalAccessException, IllegalArgumentException,
			InvocationTargetException, IntrospectionException, NoSuchMethodException, SecurityException {
		LinkedList<Object> list = new LinkedList<Object>();
		Reflect reflect = new Reflect();
		File file = new File(filePath);// 获取文件
		XSSFWorkbook xw = new XSSFWorkbook(file);
		XSSFSheet xs = xw.getSheetAt(0);// 获取xsl
		int rowStart = xs.getFirstRowNum();// 获取第一行的行数 0
		int rowEnd = xs.getLastRowNum();// 获取最后一行的行数-1
		XSSFRow firstRow = xs.getRow(0);
		for (int i = rowStart + 1; i <= rowEnd; i++) {// 行的遍历
			Object obj = classObject.newInstance();

			XSSFRow hr = xs.getRow(i);// 获取行
			if (hr == null) {
				continue;
			}
			int cellStart = hr.getFirstCellNum();// 获取第一列
			int cellEnd = hr.getLastCellNum();// 获取列的总数
			for (int k = cellStart; k < cellEnd; k++) {// 列的遍历
				XSSFCell hc = hr.getCell(k);// 获取列
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