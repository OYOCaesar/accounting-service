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
import com.oyo.accouting.service.SyncArAndApAndJournalEntryToSapService;

//同步ar and ap and JournalEntry 到sapcontroller
@RequestMapping("syncArAndApAndJournalEntryToSap")
@Controller
public class SyncArAndApJournalEntryController {
	private static Logger log = LoggerFactory.getLogger(SyncArAndApJob.class);

    @Autowired
    private SyncArAndApAndJournalEntryToSapService syncArAndApAndJournalEntryToSapService;

    @RequestMapping(value = "syncToSap", method = RequestMethod.GET)
    @ResponseBody
    public String syncArAndAp(HttpServletRequest request) {
    	String result = "";
    	try {
    		String yearMonth = request.getParameter("yearMonth");
			result = syncArAndApAndJournalEntryToSapService.syncArAndApAndJournalEntryToSap(yearMonth);
			log.info(result);
		} catch (Exception e) {
			result = "Synchronizing ar and ap and JournalEntry to SAP throwing exception!";
			log.error("Synchronizing ar and ap and JournalEntry to SAP throwing exception:{}", e);
		}
    	return result;
    }

}
