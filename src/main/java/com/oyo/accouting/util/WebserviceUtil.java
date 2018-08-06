package com.oyo.accouting.util;


import org.apache.cxf.jaxws.JaxWsProxyFactoryBean;

import com.oyo.accouting.webservice.SAPWebServiceSoap;

public class WebserviceUtil {

public static void main(String[] args) {
	JaxWsProxyFactoryBean jwpfb = new JaxWsProxyFactoryBean();  
	jwpfb.setServiceClass(SAPWebServiceSoap.class);  
	jwpfb.setAddress("http://localhost:8080/SAPWebService.asmx");  
	SAPWebServiceSoap s = (SAPWebServiceSoap) jwpfb.create();
	
	
	String result = s.businessPartners("{}");
	
	
	System.out.println(result);
}	
}