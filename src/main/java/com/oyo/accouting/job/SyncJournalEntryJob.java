package com.oyo.accouting.job;

import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;

import com.oyo.accouting.service.SyncJournalEntryToSapService;

/***
 * 同步日记账分录(Journal Entry)到SAP定时任务
 * @author ZhangSuYun
 * @date 2018-08-09
 */
public class SyncJournalEntryJob implements BaseJob {
	private static Logger log = LoggerFactory.getLogger(SyncJournalEntryJob.class);
	
	public  SyncJournalEntryJob() {
		
	}
	
	@Override
	public void execute(JobExecutionContext context) throws JobExecutionException {
		log.info("Sync Journal Entry data to SAP start.");
		try {
			ApplicationContext applicationContext = (ApplicationContext) context.getScheduler().getContext().get("applicationContext");
			SyncJournalEntryToSapService service = applicationContext.getBean(SyncJournalEntryToSapService.class);
			String result = service.syncJournalEntryToSap();
			log.info(result);
		} catch (Exception e) {
			log.error("Sync Journal Entry to SAP throwing exception!");
			e.printStackTrace();
		}
        log.info("Sync Journal Entry data to SAP end.");
	}

}
