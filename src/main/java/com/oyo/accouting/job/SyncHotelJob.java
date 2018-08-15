package com.oyo.accouting.job;

import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.quartz.SchedulerException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;
import com.oyo.accouting.service.SyncHotelToSapService;

public class SyncHotelJob implements BaseJob {
	private static Logger _log = LoggerFactory.getLogger(SyncHotelJob.class);

	
	public  SyncHotelJob() {
		
	}
	@Override
	public void execute(JobExecutionContext context) throws JobExecutionException {
		ApplicationContext applicationContext;
		try {
			applicationContext = (ApplicationContext) context.getScheduler().getContext().get("applicationContext");
			SyncHotelToSapService service = applicationContext.getBean(SyncHotelToSapService.class);
			String result = service.syncHotelToSap();
			_log.info(result);
		} catch (SchedulerException e) {
			_log.error("Sync Ar and Ap and Journal Entry data to sap throwing exception!");
			e.printStackTrace();
		}
	}

}
