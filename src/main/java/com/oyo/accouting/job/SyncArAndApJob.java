package com.oyo.accouting.job;

import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import com.alibaba.druid.util.StringUtils;
import com.oyo.accouting.service.SyncArAndApToSapService;

public class SyncArAndApJob implements BaseJob {
	private static Logger log = LoggerFactory.getLogger(SyncArAndApJob.class);

	@Autowired  
    private SyncArAndApToSapService syncArAndApToSapService; 
	
	public  SyncArAndApJob() {
		
	}
	
	@Override
	public void execute(JobExecutionContext context) throws JobExecutionException {
		log.info("Sync Ar and Ap data to SAP start.");
		try {
			String result = syncArAndApToSapService.syncArAndApToSap();
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
