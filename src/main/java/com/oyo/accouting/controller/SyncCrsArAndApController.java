package com.oyo.accouting.controller;

import javax.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.oyo.accouting.job.SyncArAndApJob;
import com.oyo.accouting.service.SyncCrsArAndApService;

//从CRS同步AR和AP数据controller
@RequestMapping("syncCrsArAp")
@Controller
public class SyncCrsArAndApController {
	private static Logger log = LoggerFactory.getLogger(SyncArAndApJob.class);

    @Autowired
    private SyncCrsArAndApService syncCrsArAndApService;

    @RequestMapping(value = "syncArApFromCrs", method = RequestMethod.GET)
    @ResponseBody
    public String syncArAndAp(HttpServletRequest request) {
    	String result = "";
    	try {
    		String yearMonth = request.getParameter("yearMonth");
			result = syncCrsArAndApService.syncCrsArAndAp(yearMonth);
			log.info(result);
		} catch (Exception e) {
			result = "Synchronizing crs ar and ap throwing exception!";
			log.error("Synchronizing crs ar and ap throwing exception:{}", e);
		}
    	return result;
    }

}
