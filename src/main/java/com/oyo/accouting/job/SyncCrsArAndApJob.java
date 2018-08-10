package com.oyo.accouting.job;

import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;

import com.oyo.accouting.service.SyncCrsArAndApService;

/***
 * 同步应收(AR)和应付(AP)到SAP定时任务
 * @author ZhangSuYun
 * @date 2018-08-09
 */
public class SyncCrsArAndApJob implements BaseJob {
	private static Logger log = LoggerFactory.getLogger(SyncCrsArAndApJob.class);
	
	public  SyncCrsArAndApJob() {
		
	}
	
	@Override
	public void execute(JobExecutionContext context) throws JobExecutionException {
		log.info("Sync AR and AP From CRS start.");
		try {
			ApplicationContext applicationContext = (ApplicationContext) context.getScheduler().getContext().get("applicationContext");
			SyncCrsArAndApService service = applicationContext.getBean(SyncCrsArAndApService.class);
			String result = service.syncCrsArAndAp();
			log.info(result);
		} catch (Exception e) {
			log.error("Sync AR and AP From CRS throwing exception!");
			e.printStackTrace();
		}
        log.info("Sync AR and AP From CRS end.");
	}

}
