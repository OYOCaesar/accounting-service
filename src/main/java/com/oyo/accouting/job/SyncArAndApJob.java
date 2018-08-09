package com.oyo.accouting.job;

import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;

import com.alibaba.druid.util.StringUtils;
import com.oyo.accouting.service.SyncArAndApToSapService;

/***
 * 同步应收(AR)和应付(AP)到SAP定时任务
 * @author ZhangSuYun
 * @date 2018-08-09
 */
public class SyncArAndApJob implements BaseJob {
	private static Logger log = LoggerFactory.getLogger(SyncArAndApJob.class);
	
	public  SyncArAndApJob() {
		
	}
	
	@Override
	public void execute(JobExecutionContext context) throws JobExecutionException {
		log.info("Sync Ar and Ap data to SAP start.");
		try {
			ApplicationContext applicationContext = (ApplicationContext) context.getScheduler().getContext().get("applicationContext");
			SyncArAndApToSapService service = applicationContext.getBean(SyncArAndApToSapService.class);
			String result = service.syncArAndApToSap();
			if (StringUtils.isEmpty(result)) {
				log.info("Sync AR and AP to sap success.");
			} else {
				log.info("Sync AR and AP to sap failure!");
			}
		} catch (Exception e) {
			log.error("Sync AR and AP to sap throwing exception!");
			e.printStackTrace();
		}
        log.info("Sync Ar and Ap data to SAP end.");
	}

}
