package com.oyo.accouting.job;

import com.oyo.accouting.service.CouponService;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;

public class CouponExpireJob implements BaseJob {
	private static Logger _log = LoggerFactory.getLogger(CouponExpireJob.class);


	public CouponExpireJob() {
		
	}
	@Override
	public void execute(JobExecutionContext context) throws JobExecutionException {
		ApplicationContext applicationContext;
		try {
			Long startTime = System.currentTimeMillis();
			applicationContext = (ApplicationContext) context.getScheduler().getContext().get("applicationContext");
			CouponService service = applicationContext.getBean(CouponService.class);
			service.expire();
			Long endTime = System.currentTimeMillis();
			_log.info("Coupon expire execute cost time = {} ms",endTime - startTime);
		} catch (Exception e) {
			_log.error("Coupon expire execute error!",e);
		}
	}

}
