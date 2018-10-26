package com.hoperun.excel;

import java.beans.IntrospectionException;
import java.beans.PropertyDescriptor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.hoperun.beans.Info;

public class Reflect {
	// 定义最大参数长度 根据Worker变量个数确定，从0开始
	public static final int MAX_PARMAM_LENGTH = 3;
	public static Map<String, String> weekMap = new HashMap<String, String>();

	static {
		weekMap.put("周日", "0");
		weekMap.put("周一", "1");
		weekMap.put("周二", "2");
		weekMap.put("周三", "3");
		weekMap.put("周四", "4");
		weekMap.put("周五", "5");
		weekMap.put("周六", "6");

	}

	public Object reflect(Object obj, int paramNumber, Object value, String titleValue)
			throws InstantiationException, IllegalAccessException, IntrospectionException, IllegalArgumentException,
			InvocationTargetException, java.beans.IntrospectionException, NoSuchMethodException, SecurityException {
		Field[] fields = obj.getClass().getDeclaredFields();// 获取属性名
		// 返回的是一个参数类型
		if (paramNumber >= MAX_PARMAM_LENGTH) {
			paramNumber = MAX_PARMAM_LENGTH;
		}
		String type = fields[paramNumber].getGenericType().toString();
		// 返回的是一个类对象
		// Class classType = field.getType();
		PropertyDescriptor pd = new PropertyDescriptor(fields[paramNumber].getName(), obj.getClass());
		Method setmd = pd.getWriteMethod();// 获取某个属性的set方法

		if (type.equals("class java.lang.String")) {
			setmd.invoke(obj, value);// 激活
		} else if (type.equals("int") || type.equals("class java.lang.Integer")) {
			String str = value.toString();
			if (str.indexOf(".") == -1) {
				setmd.invoke(obj, value);
			} else {
				setmd.invoke(obj, Integer.parseInt(str.substring(0, str.indexOf("."))));
			}
		} else if ("double".equals(type) || "class java.lang.Double".equals(type)) {
			setmd.invoke(obj, value);
		} else {
			List<Info> infos = getInfosList(obj, value, titleValue);
			setmd.invoke(obj, infos);
		}
		return obj;
	}

	@SuppressWarnings("unchecked")
	private List<Info> getInfosList(Object obj, Object value, String titleValue)
			throws NoSuchMethodException, IllegalAccessException, InvocationTargetException {
		Method getInfosMethod = obj.getClass().getMethod("getInfos");

		List<Info> invoke = (List<Info>) getInfosMethod.invoke(obj);
		List<Info> infos = invoke;
		if (infos == null) {
			infos = new ArrayList<Info>();
		}
		Info info = new Info();
		// 分割解析格式：2018-08-21(周二)
		if (titleValue.trim().contains("(")) {
			if (titleValue.trim().split("\\(").length == 2) {
				info.setDate(titleValue.trim().split("\\(")[0]);
				info.setWeek(weekMap.get(titleValue.trim().split("\\(")[1].replace(")", "")));
				info.setWeek1(titleValue.trim().split("\\(")[1].replace(")", ""));
			}
		}
		// 分割时间和项目名称

		String[] item = value.toString().split("\n");
		// 未打卡 全天调休
		if (item.length == 1) {
			if (item[0].equals("未打卡")) {
				info.setStartTime("未打卡");
				info.setEndTime("未打卡");
			}
			if (item[0].equals("调休")) {
				info.setStartTime("调休");
				info.setEndTime("调休");
			}
		}
		// 正常情况下
		if (item.length == 2) {
			String[] timeArr = item[0].split(" ~ ");
			if (timeArr.length == 2) {
				info.setStartTime(timeArr[0] + ":00");
				info.setEndTime(timeArr[1] + ":00");
			}
		}
		// 请假 （某个时间调休）或者上下午
		if (item.length == 3) {
			String[] timeArr = item[1].split(" ~ ");
			if (timeArr.length == 2) {
				info.setStartTime(timeArr[0] + ":00");
				info.setEndTime(timeArr[1] + ":00");
			}
		}
		infos.add(info);
		return infos;
	}

}