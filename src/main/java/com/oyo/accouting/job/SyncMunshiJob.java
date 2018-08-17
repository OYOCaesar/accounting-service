package com.oyo.accouting.job;

import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;

import com.oyo.accouting.service.SyncMunshiOrderService;

/***
 * 同步Munshi ar数据定时任务
 * @author ZhangSuYun
 * @date 2018-08-09
 */
public class SyncMunshiJob implements BaseJob {
	private static Logger log = LoggerFactory.getLogger(SyncMunshiJob.class);
	
	public  SyncMunshiJob() {
		
	}
	
	@Override
	public void execute(JobExecutionContext context) throws JobExecutionException {
		log.info("Sync munshi ar start.");
		try {
			ApplicationContext applicationContext = (ApplicationContext) context.getScheduler().getContext().get("applicationContext");
			SyncMunshiOrderService service = applicationContext.getBean(SyncMunshiOrderService.class);
			String result = service.syncMunshiAr(null);
			log.info(result);
		} catch (Exception e) {
			log.error("Sync munshi throwing exception,exception is:{}", e);
			e.printStackTrace();
		}
        log.info("Sync munshi ar end.");
	}

}
