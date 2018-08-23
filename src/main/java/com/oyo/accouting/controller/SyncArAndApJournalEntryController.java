package com.oyo.accouting.controller;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.oyo.accouting.job.SyncArAndApJob;
import com.oyo.accouting.service.SyncArAndApAndJournalEntryToSapService;

//同步ar and ap and JournalEntry 到sapcontroller
@RequestMapping("syncArAndApAndJournalEntryToSap")
@Controller
public class SyncArAndApJournalEntryController {
	private static Logger log = LoggerFactory.getLogger(SyncArAndApJob.class);

    @Autowired
    private SyncArAndApAndJournalEntryToSapService syncArAndApAndJournalEntryToSapService;

    @RequestMapping(value = "syncToSap")
    @ResponseBody
    public String syncArAndAp(HttpServletRequest request) {
    	String result = "";
    	try {
    		String yearMonth = request.getParameter("yearMonth");
    		Integer hotelId = StringUtils.isNotEmpty(request.getParameter("hotelId")) ? Integer.valueOf(request.getParameter("hotelId")) : null;
			result = syncArAndApAndJournalEntryToSapService.syncArAndApAndJournalEntryToSap(yearMonth,hotelId);
			log.info(result);
		} catch (Exception e) {
			result = "Synchronizing ar and ap and JournalEntry to SAP throwing exception：" + e.getMessage();
			log.error("Synchronizing ar and ap and JournalEntry to SAP throwing exception:{}", e);
		}
    	return result;
    }

}
