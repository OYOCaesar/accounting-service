package com.oyo.accouting.util;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

/**
 * 读取yml文件中的值
 * @author EDZ
 *
 */
@Configuration
public class YmlUtil {
	
	// 获取sapWebServiceUrl属性
	public static String sapWebServiceUrl;

	public String getSapWebServiceUrl() {
		return sapWebServiceUrl;
	}

	@Value("${sapWebServiceUrl}")
	public void setSapWebServiceUrl(String sapWebServiceUrl) {
		YmlUtil.sapWebServiceUrl = sapWebServiceUrl;
	}
	
}
