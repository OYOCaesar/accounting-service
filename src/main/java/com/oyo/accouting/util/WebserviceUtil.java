package com.oyo.accouting.util;

import org.apache.cxf.jaxws.JaxWsProxyFactoryBean;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Configuration;
import com.oyo.accouting.webservice.SAPWebServiceSoap;

/***
 * 获取sap service工具类
 * @author ZhangSuYun
 * @date 2018-08-09
 */
@Configuration
public class WebserviceUtil {
	
	private static Logger log = LoggerFactory.getLogger(WebserviceUtil.class);
	
	//sap service url
	private static String sapServiceUrl = "";
	
	static {
		YmlUtil ymlUtil = new YmlUtil();
		sapServiceUrl = ymlUtil.getSapWebServiceUrl();
	}
	
	//获取sap接口对象
	public static SAPWebServiceSoap getSapSapService() {
		SAPWebServiceSoap service = null;
		try {
			JaxWsProxyFactoryBean jwpfb = new JaxWsProxyFactoryBean();
	        jwpfb.setServiceClass(SAPWebServiceSoap.class);
	        jwpfb.setAddress(sapServiceUrl);
	        service = (SAPWebServiceSoap) jwpfb.create();
		} catch (Exception e) {
			log.error("Get sap interface throw Exception: {}", e);
		}
        return service;
	}
	
//	public static void main(String[] args) {
//		JaxWsProxyFactoryBean jwpfb = new JaxWsProxyFactoryBean();  
//		jwpfb.setServiceClass(SAPWebServiceSoap.class);  
//		jwpfb.setAddress("http://localhost:8080/SAPWebService.asmx");  
//		SAPWebServiceSoap s = (SAPWebServiceSoap) jwpfb.create();
//		String result = s.businessPartners("{}");
//		System.out.println(result);
//	}	
}