package com.oyo.accouting.controller;

import javax.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import com.oyo.accouting.job.SyncArAndApJob;
import com.oyo.accouting.service.SyncArAndApAndJournalEntryToSapService;

//日记账分录controller
@RequestMapping("syncArAndApAndJournalEntryToSap")
@Controller
public class SyncArAndApJournalEntryController {
	private static Logger log = LoggerFactory.getLogger(SyncArAndApJob.class);

    @Autowired
    private SyncArAndApAndJournalEntryToSapService syncArAndApAndJournalEntryToSapService;

    @RequestMapping(value = "syncToSap", method = RequestMethod.GET)
    public void syncArAndAp(HttpServletRequest request) {
    	try {
			String result = syncArAndApAndJournalEntryToSapService.syncArAndApAndJournalEntryToSap();
			log.info(result);
		} catch (Exception e) {
			log.error("Synchronizing ar and ap and JournalEntry to SAP throwing exception!");
			e.printStackTrace();
		}
    }

}
