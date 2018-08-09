package com.oyo.accouting.job;

import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import com.alibaba.druid.util.StringUtils;
import com.oyo.accouting.service.SyncJournalEntryToSapService;

public class SyncJournalEntryJob implements BaseJob {
	private static Logger log = LoggerFactory.getLogger(SyncJournalEntryJob.class);

	@Autowired  
    private SyncJournalEntryToSapService syncJournalEntryToSapService; 
	
	public  SyncJournalEntryJob() {
		
	}
	
	@Override
	public void execute(JobExecutionContext context) throws JobExecutionException {
		log.info("Sync Journal Entry data to SAP start.");
		try {
			String result = syncJournalEntryToSapService.syncJournalEntryToSap();
			if (StringUtils.isEmpty(result)) {
				log.info("Sync Journal Entry to SAP success.");
			} else {
				log.info("Sync Journal Entry to SAP failure!");
			}
		} catch (Exception e) {
			log.error("Sync Journal Entry to SAP throwing exception!");
			e.printStackTrace();
		}
        log.info("Sync Journal Entry data to SAP end.");
	}

}
