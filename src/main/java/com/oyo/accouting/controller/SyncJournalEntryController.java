package com.oyo.accouting.controller;

import javax.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import com.oyo.accouting.job.SyncArAndApJob;
import com.oyo.accouting.service.SyncJournalEntryToSapService;

//日记账分录controller
@RequestMapping("journalEntry")
@Controller
public class SyncJournalEntryController {
	private static Logger log = LoggerFactory.getLogger(SyncArAndApJob.class);

    @Autowired
    private SyncJournalEntryToSapService syncJournalEntryToSapService;

    @RequestMapping(value = "syncJournalEntry", method = RequestMethod.GET)
    public void syncArAndAp(HttpServletRequest request) {
    	try {
			//String result = syncJournalEntryToSapService.syncJournalEntryToSap();
			String result = syncJournalEntryToSapService.test();
			log.info(result);
		} catch (Exception e) {
			log.error("Synchronizing JournalEntry to SAP throwing exception!");
			e.printStackTrace();
		}
    }

}
