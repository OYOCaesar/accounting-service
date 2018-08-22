package com.oyo.accouting.controller;

import java.time.LocalDate;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.oyo.accouting.bean.AccountPeriodDto;
import com.oyo.accouting.bean.PageResult;
import com.oyo.accouting.bean.QueryAccountPeriodDto;
import com.oyo.accouting.job.SyncArAndApJob;
import com.oyo.accouting.service.QueryCrsAccountPeriodService;

//查询CRS中账单数据controller
@RequestMapping("queryCrsAccountPeriod")
@Controller
public class QueryCrsAccountPeriodController {
	private static Logger log = LoggerFactory.getLogger(SyncArAndApJob.class);

    @Autowired
    private QueryCrsAccountPeriodService queryCrsAccountPeriodService;

    @RequestMapping(value = "query")
    @ResponseBody
    public PageResult query(@RequestBody QueryAccountPeriodDto queryAccountPeriodDto) {
    	PageResult result = new PageResult();
    	try {
    		PageHelper.startPage(queryAccountPeriodDto.getPageNum(), queryAccountPeriodDto.getPageSize());
    		LocalDate localDate = LocalDate.now();
    		if (StringUtils.isEmpty(queryAccountPeriodDto.getStartYearAndMonthQuery())) {
    			queryAccountPeriodDto.setStartYearAndMonthQuery(localDate.getYear() + "-" + localDate.getMonthValue());
    		}
    		if (StringUtils.isEmpty(queryAccountPeriodDto.getEndYearAndMonthQuery())) {
    			queryAccountPeriodDto.setEndYearAndMonthQuery(localDate.getYear() + "-" + localDate.getMonthValue());
    		}
    		List<AccountPeriodDto> list = queryCrsAccountPeriodService.queryCrsAccountPeriod(queryAccountPeriodDto);
    		result.setRows(list);
			PageInfo<AccountPeriodDto> pageInfo = new PageInfo<>(list);
			result.setTotal(pageInfo.getTotal());
		} catch (Exception e) {
			log.error("Query crs account period throwing exception:{}", e);
		}
    	return result;
    }

}
