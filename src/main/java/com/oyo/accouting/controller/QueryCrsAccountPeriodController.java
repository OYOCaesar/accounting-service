package com.oyo.accouting.controller;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import javax.servlet.http.HttpServletResponse;

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
import com.oyo.accouting.util.ExcelUtils;

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
    		//如果开始结束账期都为空，那么开始结束账期均为当前月所在的账期
    		if (StringUtils.isEmpty(queryAccountPeriodDto.getStartYearAndMonthQuery()) && StringUtils.isEmpty(queryAccountPeriodDto.getEndYearAndMonthQuery())) {
    			queryAccountPeriodDto.setStartYearAndMonthQuery(localDate.getYear() + "-" + (localDate.getMonthValue() < 10 ? "0" + localDate.getMonthValue() : localDate.getMonthValue()));
    			queryAccountPeriodDto.setEndYearAndMonthQuery(localDate.getYear() + "-" + (localDate.getMonthValue() < 10 ? "0" + localDate.getMonthValue() : localDate.getMonthValue()));
    		} else {
    			if (StringUtils.isNotEmpty(queryAccountPeriodDto.getStartYearAndMonthQuery()) && StringUtils.isEmpty(queryAccountPeriodDto.getEndYearAndMonthQuery())) {
        			queryAccountPeriodDto.setEndYearAndMonthQuery(queryAccountPeriodDto.getStartYearAndMonthQuery());
        		}
        		if (StringUtils.isEmpty(queryAccountPeriodDto.getStartYearAndMonthQuery()) && StringUtils.isNotEmpty(queryAccountPeriodDto.getEndYearAndMonthQuery())) {
        			queryAccountPeriodDto.setStartYearAndMonthQuery(queryAccountPeriodDto.getEndYearAndMonthQuery());
        		}
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
    
    //商户对账单导出
	@RequestMapping("exportMerchantAccount")
	public void exportMerchantAccount(HttpServletResponse response, QueryAccountPeriodDto queryAccountPeriodDto) {
		try {
			// 此处为标题，excel首行的title，按照此格式即可，格式无需改动，但是可以增加或者减少项目。
			String export = "OYO订单编号#orderNo,顾客名字#guestName,预定人名称#bookingGuestName,预定人名称2#bookingSecondaryGuestName,订单来源#orderChannel,"
					      + "OYO酒店编号#oyoId,OYO酒店名称#hotelName,营业收入#orderTotalAmount,入住时间#checkInDate,离店时间#checkOutDate,间/夜#currentMonthRoomsNumber,"
					      + "费率#currentMonthRate,顾客选择方式#paymentType,平台名称#otaName,平台订单号#otaId,城市#city,区域#region,营收核对结果#revenueCheckResults,"
					      + "营收差异原因#reasonsForRevenueDifference,提成比例#proportions,顾客选择方式核对结果#paymentTypeCheckingResult,平台费承担方#platformFeePayableParty,"
					      + "备注#remarks";
			String[] excelHeader = export.split(",");
			
			LocalDate localDate = LocalDate.now();
    		//如果开始结束账期都为空，那么开始结束账期均为当前月所在的账期
    		if (StringUtils.isEmpty(queryAccountPeriodDto.getStartYearAndMonthQuery()) && StringUtils.isEmpty(queryAccountPeriodDto.getEndYearAndMonthQuery())) {
    			queryAccountPeriodDto.setStartYearAndMonthQuery(localDate.getYear() + "-" + (localDate.getMonthValue() < 10 ? "0" + localDate.getMonthValue() : localDate.getMonthValue()));
    			queryAccountPeriodDto.setEndYearAndMonthQuery(localDate.getYear() + "-" + (localDate.getMonthValue() < 10 ? "0" + localDate.getMonthValue() : localDate.getMonthValue()));
    		} else {
    			if (StringUtils.isNotEmpty(queryAccountPeriodDto.getStartYearAndMonthQuery()) && StringUtils.isEmpty(queryAccountPeriodDto.getEndYearAndMonthQuery())) {
        			queryAccountPeriodDto.setEndYearAndMonthQuery(queryAccountPeriodDto.getStartYearAndMonthQuery());
        		}
        		if (StringUtils.isEmpty(queryAccountPeriodDto.getStartYearAndMonthQuery()) && StringUtils.isNotEmpty(queryAccountPeriodDto.getEndYearAndMonthQuery())) {
        			queryAccountPeriodDto.setStartYearAndMonthQuery(queryAccountPeriodDto.getEndYearAndMonthQuery());
        		}
    		}
    		List<AccountPeriodDto> list = queryCrsAccountPeriodService.queryCrsAccountPeriod(queryAccountPeriodDto);
    		Map<Integer,List<AccountPeriodDto>> hotelGroupMap = list.stream().collect(Collectors.groupingBy(AccountPeriodDto::getUniqueCode));
    		for (Map.Entry<Integer, List<AccountPeriodDto>> entry : hotelGroupMap.entrySet()) {
				String className = entry.getKey() + "-" + queryAccountPeriodDto.getStartYearAndMonthQuery().replace("-", "") + "-商户对账单"; // className:生成的excel默认文件名和sheet页
				ExcelUtils.export(response, className, excelHeader, entry.getValue());// 调用封装好的导出方法
		    }
		} catch (Exception e) {
			log.error("Export Merchant Account throwing exception:{}", e);
		}
	}
	
	//汇总统计导出
	@RequestMapping("exportSummaryStatistics")
	public void exportSummaryStatistics(HttpServletResponse response, QueryAccountPeriodDto queryAccountPeriodDto) {
		try {
			// 此处为标题，excel首行的title，按照此格式即可，格式无需改动，但是可以增加或者减少项目。
			String export = "OYO订单编号#orderNo,顾客名字#guestName,预定人名称#bookingGuestName,预定人名称2#bookingSecondaryGuestName,订单来源#orderChannel,"
					      + "OYO酒店编号#oyoId,OYO酒店名称#hotelName,营业收入#orderTotalAmount,入住时间#checkInDate,离店时间#checkOutDate,间/夜#currentMonthRoomsNumber,"
					      + "费率#currentMonthRate,顾客选择方式#paymentType,平台名称#otaName,平台订单号#otaId,城市#city,区域#region,营收核对结果#revenueCheckResults,"
					      + "营收差异原因#reasonsForRevenueDifference,提成比例#proportions,顾客选择方式核对结果#paymentTypeCheckingResult,平台费承担方#platformFeePayableParty,"
					      + "备注#remarks";
			String[] excelHeader = export.split(",");
			
			LocalDate localDate = LocalDate.now();
    		//如果开始结束账期都为空，那么开始结束账期均为当前月所在的账期
    		if (StringUtils.isEmpty(queryAccountPeriodDto.getStartYearAndMonthQuery()) && StringUtils.isEmpty(queryAccountPeriodDto.getEndYearAndMonthQuery())) {
    			queryAccountPeriodDto.setStartYearAndMonthQuery(localDate.getYear() + "-" + (localDate.getMonthValue() < 10 ? "0" + localDate.getMonthValue() : localDate.getMonthValue()));
    			queryAccountPeriodDto.setEndYearAndMonthQuery(localDate.getYear() + "-" + (localDate.getMonthValue() < 10 ? "0" + localDate.getMonthValue() : localDate.getMonthValue()));
    		} else {
    			if (StringUtils.isNotEmpty(queryAccountPeriodDto.getStartYearAndMonthQuery()) && StringUtils.isEmpty(queryAccountPeriodDto.getEndYearAndMonthQuery())) {
        			queryAccountPeriodDto.setEndYearAndMonthQuery(queryAccountPeriodDto.getStartYearAndMonthQuery());
        		}
        		if (StringUtils.isEmpty(queryAccountPeriodDto.getStartYearAndMonthQuery()) && StringUtils.isNotEmpty(queryAccountPeriodDto.getEndYearAndMonthQuery())) {
        			queryAccountPeriodDto.setStartYearAndMonthQuery(queryAccountPeriodDto.getEndYearAndMonthQuery());
        		}
    		}
    		List<AccountPeriodDto> list = queryCrsAccountPeriodService.queryCrsAccountPeriod(queryAccountPeriodDto);
    		Map<Integer,List<AccountPeriodDto>> hotelGroupMap = list.stream().collect(Collectors.groupingBy(AccountPeriodDto::getUniqueCode));
    		for (Map.Entry<Integer, List<AccountPeriodDto>> entry : hotelGroupMap.entrySet()) {
				String className = entry.getKey() + "-" + queryAccountPeriodDto.getStartYearAndMonthQuery().replace("-", "") + "-商户对账单"; // className:生成的excel默认文件名和sheet页
				ExcelUtils.export(response, className, excelHeader, entry.getValue());// 调用封装好的导出方法
		    }
		} catch (Exception e) {
			log.error("Export Merchant Account throwing exception:{}", e);
		}
	}
	
	//明细导出
	@RequestMapping("exportDetails")
	public void exportDetails(HttpServletResponse response, QueryAccountPeriodDto queryAccountPeriodDto) {
		try {
			// 此处为标题，excel首行的title，按照此格式即可，格式无需改动，但是可以增加或者减少项目。
			String export = "OYO订单编号#orderNo,顾客名字#guestName,预定人名称#bookingGuestName,预定人名称2#bookingSecondaryGuestName,订单来源#orderChannel,"
					      + "OYO酒店编号#oyoId,OYO酒店名称#hotelName,营业收入#orderTotalAmount,入住时间#checkInDate,离店时间#checkOutDate,间/夜#currentMonthRoomsNumber,"
					      + "费率#currentMonthRate,顾客选择方式#paymentType,平台名称#otaName,平台订单号#otaId,城市#city,区域#region,营收核对结果#revenueCheckResults,"
					      + "营收差异原因#reasonsForRevenueDifference,提成比例#proportions,顾客选择方式核对结果#paymentTypeCheckingResult,平台费承担方#platformFeePayableParty,"
					      + "备注#remarks";
			String[] excelHeader = export.split(",");
			
			LocalDate localDate = LocalDate.now();
    		//如果开始结束账期都为空，那么开始结束账期均为当前月所在的账期
    		if (StringUtils.isEmpty(queryAccountPeriodDto.getStartYearAndMonthQuery()) && StringUtils.isEmpty(queryAccountPeriodDto.getEndYearAndMonthQuery())) {
    			queryAccountPeriodDto.setStartYearAndMonthQuery(localDate.getYear() + "-" + (localDate.getMonthValue() < 10 ? "0" + localDate.getMonthValue() : localDate.getMonthValue()));
    			queryAccountPeriodDto.setEndYearAndMonthQuery(localDate.getYear() + "-" + (localDate.getMonthValue() < 10 ? "0" + localDate.getMonthValue() : localDate.getMonthValue()));
    		} else {
    			if (StringUtils.isNotEmpty(queryAccountPeriodDto.getStartYearAndMonthQuery()) && StringUtils.isEmpty(queryAccountPeriodDto.getEndYearAndMonthQuery())) {
        			queryAccountPeriodDto.setEndYearAndMonthQuery(queryAccountPeriodDto.getStartYearAndMonthQuery());
        		}
        		if (StringUtils.isEmpty(queryAccountPeriodDto.getStartYearAndMonthQuery()) && StringUtils.isNotEmpty(queryAccountPeriodDto.getEndYearAndMonthQuery())) {
        			queryAccountPeriodDto.setStartYearAndMonthQuery(queryAccountPeriodDto.getEndYearAndMonthQuery());
        		}
    		}
    		List<AccountPeriodDto> list = queryCrsAccountPeriodService.queryCrsAccountPeriod(queryAccountPeriodDto);
    		Map<Integer,List<AccountPeriodDto>> hotelGroupMap = list.stream().collect(Collectors.groupingBy(AccountPeriodDto::getUniqueCode));
    		for (Map.Entry<Integer, List<AccountPeriodDto>> entry : hotelGroupMap.entrySet()) {
				String className = entry.getKey() + "-" + queryAccountPeriodDto.getStartYearAndMonthQuery().replace("-", "") + "-商户对账单"; // className:生成的excel默认文件名和sheet页
				ExcelUtils.export(response, className, excelHeader, entry.getValue());// 调用封装好的导出方法
		    }
		} catch (Exception e) {
			log.error("Export Merchant Account throwing exception:{}", e);
		}
	}

}
