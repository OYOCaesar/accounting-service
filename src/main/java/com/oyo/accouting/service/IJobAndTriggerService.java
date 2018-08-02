package com.oyo.accouting.service;


import com.github.pagehelper.PageInfo;
import com.oyo.accouting.bean.JobAndTrigger;

public interface IJobAndTriggerService {
	public PageInfo<JobAndTrigger> getJobAndTriggerDetails(int pageNum, int pageSize);
}
