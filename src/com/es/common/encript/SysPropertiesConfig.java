package com.es.common.encript;

import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.beans.factory.config.PropertyPlaceholderConfigurer;

public class SysPropertiesConfig extends PropertyPlaceholderConfigurer {
	private static Map<String, Object> ctxPropertiesMap;
	private String[] encryptPropNames = {"jdbc.url", "jdbc.user", "jdbc.password","jdbc.overrideDefaultUser","jdbc.overrideDefaultPassword"};
	@Override
	protected void processProperties(ConfigurableListableBeanFactory beanFactoryToProcess,Properties props) throws BeansException {
		super.processProperties(beanFactoryToProcess, props);
		ctxPropertiesMap = new HashMap<String, Object>();
		for (Object key : props.keySet()) {
			String keyStr = key.toString();
			String value = props.getProperty(keyStr);
			ctxPropertiesMap.put(keyStr, value);
		}
	}
	protected String convertProperty(String propertyName, String propertyValue) {
		// 解密密钥-16进制
		String pass = "32179713140527518150536713505510";
		if (isEncryptProp(propertyName)) {
			String decryptValue = AESTools.decrypt(propertyValue.getBytes(), pass);
			return decryptValue;
		} else {
			return propertyValue;
		}
	}
	public static Object getContextProperty(String name) {
		return ctxPropertiesMap.get(name);
	}
	private boolean isEncryptProp(String propertyName) {
		for (String encryptName : encryptPropNames) {
			if (encryptName.equals(propertyName)) {
				return true;
			}
		}
		return false;
	}
	
	public static void main(String[] args) {
		String password = "32179713140527518150536713505510";
		System.out.println(AESTools.encrypt("jdbc:mysql://127.0.0.1:3306/apppay?useUnicode=true&characterEncoding=UTF-8", password));
		System.out.println(AESTools.encrypt("root", password));
		System.out.println(AESTools.encrypt("likun", password));
	}
}
