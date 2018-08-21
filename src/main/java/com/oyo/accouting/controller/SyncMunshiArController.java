package com.oyo.accouting.controller;

import javax.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.oyo.accouting.job.SyncArAndApJob;
import com.oyo.accouting.service.SyncMunshiOrderService;

//从Munshi同步AR数据controller
@RequestMapping("syncMunshiAr")
@Controller
public class SyncMunshiArController {
	private static Logger log = LoggerFactory.getLogger(SyncArAndApJob.class);

    @Autowired
    private SyncMunshiOrderService syncMunshiOrderService;

    @RequestMapping(value = "syncMunshiAr")
    @ResponseBody
    public String syncArAndAp(HttpServletRequest request) {
    	String result = "";
    	try {
    		String yearMonth = request.getParameter("yearMonth");
			result = syncMunshiOrderService.syncMunshiAr(yearMonth);
			log.info(result);
		} catch (Exception e) {
			result = "Synchronizing munshi ar throwing exception：" + e.getMessage();
			log.error("Synchronizing munshi ar throwing exception:{}", e);
		}
    	return result;
    }

}
