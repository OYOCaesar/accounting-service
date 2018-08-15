package com.oyo.accouting.job;

import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SyncHotelJob implements BaseJob {
	private static Logger _log = LoggerFactory.getLogger(SyncHotelJob.class);

	
	public  SyncHotelJob() {
		
	}
	@Override
	public void execute(JobExecutionContext context) throws JobExecutionException {
		try {
			_log.info("Sync hotel is successful.");
		} catch (Exception e) {
			_log.error("Sync Hotel to sap throwing exception!");
			e.printStackTrace();
		}
	}

}
