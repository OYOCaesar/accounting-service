package com.oyo.accouting.controller;

import javax.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import com.alibaba.druid.util.StringUtils;
import com.oyo.accouting.job.SyncArAndApJob;
import com.oyo.accouting.service.SyncArAndApToSapService;

@RequestMapping("arAndAp")
@Controller
public class SyncArAndApController {
	private static Logger log = LoggerFactory.getLogger(SyncArAndApJob.class);

    @Autowired
    private SyncArAndApToSapService syncArAndApToSapService;

    @RequestMapping(value = "syncArAndAp", method = RequestMethod.GET)
    public void syncArAndAp(HttpServletRequest request) {
    	try {
			String result = syncArAndApToSapService.syncArAndApToSap();
			//String result = syncArAndApToSapService.test();
			if (StringUtils.isEmpty(result)) {
				log.info("Synchronization AR and AP to sap success.");
			} else {
				log.info("Synchronization AR and AP to sap failure!");
			}
		} catch (Exception e) {
			log.error("Synchronizing AR and AP to sap throwing exception!");
			e.printStackTrace();
		}
    }

}
