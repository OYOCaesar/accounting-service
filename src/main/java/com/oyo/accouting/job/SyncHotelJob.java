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

		_log.info("aaaaa");
	}

}
