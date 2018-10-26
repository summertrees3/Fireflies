package com.hoperun.excel;

import java.beans.IntrospectionException;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.lang.reflect.InvocationTargetException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.apache.poi.openxml4j.exceptions.InvalidFormatException;

import com.hoperun.beans.ID;
import com.hoperun.beans.Info;
import com.hoperun.beans.Worker;
import com.hoperun.openapi.HttpSoapClient;
import com.hoperun.util.ConfigUtil;

public class Handler {
	public static final String COMPANY_CODE = "AZ2212";// 供应商编码
	List<Object> list = null;
	List<Object> idList = null;
	Map<String, String> idMap = new HashMap<String, String>();
	StringBuilder sb = new StringBuilder();
	int total = 0;

	/**
	 * 同步数据
	 * 
	 * @param filePath
	 * @param clazz
	 * @throws FileNotFoundException
	 * @throws IllegalAccessException
	 * @throws IllegalArgumentException
	 * @throws InvocationTargetException
	 * @throws InstantiationException
	 * @throws IOException
	 * @throws IntrospectionException
	 * @throws InvalidFormatException
	 * @throws NoSuchMethodException
	 * @throws SecurityException
	 */
	@SuppressWarnings("rawtypes")
	public void handle(String filePath, Class clazz) throws FileNotFoundException, IllegalAccessException,
			IllegalArgumentException, InvocationTargetException, InstantiationException, IOException,
			IntrospectionException, InvalidFormatException, NoSuchMethodException, SecurityException {
		String str = filePath.substring(filePath.lastIndexOf(".") + 1);
		if (str.equals("xls")) {
			// 03
			XslParse xp = new XslParse();
			idList = xp.parse(ConfigUtil.getPathConfig("idConfigDir"), ID.class);
			// 获取身份证映射关系
			getIdMap(idMap, idList);
			list = xp.parse(filePath, clazz);
		} else {
			// 07
			XlsxParse xp = new XlsxParse();
			list = xp.parse(filePath, clazz);
			getIdMap(idMap, idList);
			idList = xp.parse(ConfigUtil.getPathConfig("idConfigDir"), ID.class);
		}
		// 循环组装所有员工信息的报文
		for (int i = 0; i < list.size(); i++) {
			Worker worker = (Worker) list.get(i);
			getParamsStr(worker, sb);
		}
		int retCode = HttpSoapClient.sendRequest(getMsg(sb).toString());
		if (retCode == 0) {
			// 请求成功
			printSucessInfo(list);
		} else {
			printErrorInfo();
		}
	}

	/**
	 * 打印具体人员考勤信息
	 * 
	 * @param list
	 */
	public void printSucessInfo(List<Object> list) {
		System.out.println("***********考勤任务同步eResource成功,同步信息如下：**************");
		for (int i = 0; i < list.size(); i++) {
			Worker worker = (Worker) list.get(i);
			System.out.println("***********  " + worker.getName() + "  ************");
			List<Info> infos = worker.getInfos();
			// for (Info item : infos) {
			for (int j = 0; j < infos.size(); j++) {
				Info item = infos.get(j);
				if (!item.getStartTime().contains("未打卡") && !item.getStartTime().contains("调休")
						&& !item.getEndTime().contains("未打卡") && !item.getEndTime().contains("调休")
						&& !item.getStartTime().equals("") && !item.getEndTime().equals("")) {
					total++;
					System.out.println("考勤日期：  " + item.getDate() + "(" + item.getWeek1() + ")\t\t打卡时间: "
							+ item.getStartTime() + " ~ " + item.getEndTime());
				}
			}
		}
		System.out.println("-----------------------------------------------------------------");
		System.out.println("\t\t\t\t\t\t总计:" + total + "条记录");
	}

	/**
	 * 打印失败信息
	 */
	public void printErrorInfo() {
		Map<String, String> map = new LinkedHashMap<String, String>();
		map.put("E00", "传入内容不能为空!");
		map.put("E01", "第N行数据, 供应商编码为空或长度过长！");
		map.put("E02", "第N行数据, 刷卡时间不能为空,且格式应为yyyy-MM-dd HH:mm:ss");
		map.put("E03", "第N行数据, 刷卡日期不能为空,且数据格式应为yyyy-MM-dd！\t\t\t");
		map.put("E04", "第N行数据, 周的值不正确（0[周日],1[周一],2[周二],3[周三],4[周四],5[周五],6[周六]）！");
		map.put("E05", "第N行数据, 身份证号为空或长度过长！");
		map.put("E06", "第N行数据, 员工姓名为空或长度过长！");
		map.put("E07", "第N行数据, 刷卡时间的日期SwipeDate与时间SwipeTime不匹配！");
		map.put("E08", "第N行数据, 供应商工号长度过长！");
		map.put("E09", "第N行数据, 供应商卡证号长度过长！");
		map.put("E10", "第N行数据, 华为工号长度过长！");
		map.put("E11", "第N行数据, 华为卡证号长度过长！");
		map.put("E12", "第N行数据, 供应商编码验证不通过！");
		map.put("E13", "未知错误！");
		map.put("E14", "第N行数据,插入失败");
		map.put("E15", "传入数据过多。");
		System.out
				.println("******************************************************************************************");
		System.out.println("                           对不起，考勤任务失败，接口具体返回状态列表如下：");
		System.out
				.println("******************************************************************************************");
		for (Map.Entry<String, String> entry : map.entrySet()) {
			System.out.println("\t" + entry.getKey() + " : " + entry.getValue());
		}
		System.out
				.println("******************************************************************************************");
	}

	/**
	 * 获取身份证映射关系
	 * 
	 * @param idMap
	 * @param idList
	 * @return
	 */
	public Map<String, String> getIdMap(Map<String, String> idMap, List<Object> idList) {
		for (int j = 0; j < idList.size(); j++) {
			ID idObj = (ID) idList.get(j);
			idMap.put(idObj.getName(), idObj.getIdNo());
		}
		return idMap;
	}

	/**
	 * 拼接所有员工组成的参数信息
	 * 
	 * @param sb
	 * @return
	 * @throws IOException
	 */
	public StringBuilder getMsg(StringBuilder sb) throws IOException {
		StringBuilder msg = new StringBuilder();
		msg.append("		<com:importSupplierCardtime>\n");
		msg.append("			<cardTimeInputList>\n");
		msg.append(sb);
		msg.append("			</cardTimeInputList>\n");
		msg.append("		</com:importSupplierCardtime>\n");
		BufferedWriter out = null;
		out = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(new File(ConfigUtil.getPathConfig("logDir")
				+ File.separator + new SimpleDateFormat("YYYYMMddHHmmss").format(new Date()) + ".log"))));
		out.write(msg.toString());
		out.close();
		return msg;
	}

	/**
	 * 过滤未打卡，调休，空白考勤
	 * 
	 * @param worker
	 * @param sb
	 * @return
	 */
	public String getParamsStr(Worker worker, StringBuilder sb) {
		List<Info> infos = worker.getInfos();
		for (Info item : infos) {
			if (!item.getStartTime().contains("未打卡") && !item.getStartTime().contains("调休")
					&& !item.getEndTime().contains("未打卡") && !item.getEndTime().contains("调休")
					&& !item.getStartTime().equals("") && !item.getEndTime().equals("")) {
				// 考勤
				sb.append(assemblyMsg(worker.getName(), item, 0));
				// 退勤
				sb.append(assemblyMsg(worker.getName(), item, 1));
			}
		}
		return sb.toString();
	}

	/**
	 * 组装报文数据
	 * 
	 * @param name
	 *            名字
	 * @param info
	 * @param flag
	 *            0 ： 考勤标志位；1：退勤标志位
	 * @return
	 */
	public String assemblyMsg(String name, Info info, int flag) {
		StringBuilder sb = new StringBuilder();
		sb.append("			<TimeSheetCardTimeParameters>\n");
		sb.append("				<companyCode>" + COMPANY_CODE + "</companyCode>\n");
		sb.append("				<staffCardNo></staffCardNo>\n");
		sb.append("				<staffHWNo></staffHWNo>\n");
		sb.append("				<staffIdNo>" + idMap.get(name.trim()) + "</staffIdNo>\n");
		sb.append("				<staffName>" + name + "</staffName>\n");
		sb.append("				<staffNo></staffNo>\n");
		sb.append("				<statffHWCardNo></statffHWCardNo>\n");
		sb.append("				<swipeDate>" + info.getDate() + "</swipeDate>\n");
		sb.append("				<swipeTime>" + info.getDate() + " "
				+ (flag == 0 ? info.getStartTime() : info.getEndTime()) + "</swipeTime>\n");
		sb.append("				<week>" + info.getWeek() + "</week>\n");
		sb.append("			</TimeSheetCardTimeParameters>\n");
		return sb.toString();
	}
}
