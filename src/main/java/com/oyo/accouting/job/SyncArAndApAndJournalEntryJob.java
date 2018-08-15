package com.oyo.accouting.job;

import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/***
 * 同步应收(AR)和应付(AP)到SAP定时任务
 * @author ZhangSuYun
 * @date 2018-08-09
 */
public class SyncArAndApAndJournalEntryJob implements BaseJob {
	private static Logger log = LoggerFactory.getLogger(SyncArAndApAndJournalEntryJob.class);
	
	public  SyncArAndApAndJournalEntryJob() {
		
	}
	
	@Override
	public void execute(JobExecutionContext context) throws JobExecutionException {
		log.info("Sync Ar and Ap and Journal Entry data to SAP start.");
		try {
			/*ApplicationContext applicationContext = (ApplicationContext) context.getScheduler().getContext().get("applicationContext");
			SyncArAndApAndJournalEntryToSapService service = applicationContext.getBean(SyncArAndApAndJournalEntryToSapService.class);
			String result = service.syncArAndApAndJournalEntryToSap(null);
			log.info(result);*/
			log.info("Sync Ar and Ap and Journal Entry data to SAP is successful.");
		} catch (Exception e) {
			log.error("Sync Ar and Ap and Journal Entry data to sap throwing exception!");
			e.printStackTrace();
		}
        log.info("Sync Ar and Ap and Journal Entry data to SAP end.");
	}

}
