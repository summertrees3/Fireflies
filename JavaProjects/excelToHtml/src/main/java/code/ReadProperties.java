package code;

import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Properties;


public class ReadProperties {
	public static String readPath(String key){
	    Properties properties = new Properties();
		try {
			properties.load(new InputStreamReader(new BufferedInputStream(new FileInputStream("config\\config.properties")),"utf-8"));
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	    
	    // 获取key对应的value值
		return properties.getProperty(key).toString();
	}
}
