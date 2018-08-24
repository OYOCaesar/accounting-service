package com.oyo.accouting.service;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.temporal.ChronoUnit;
import java.time.temporal.TemporalAdjusters;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.oyo.accouting.bean.AccountPeriodDetailsDto;
import com.oyo.accouting.bean.AccountPeriodDto;
import com.oyo.accouting.bean.AccountPeriodTotalDto;
import com.oyo.accouting.bean.CrsEnumsDto;
import com.oyo.accouting.bean.QueryAccountPeriodDto;
import com.oyo.accouting.mapper.crs.CrsAccountPeriodMapper;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

/**
 * query crs account period interface.
 * @author ZhangSuYun
 * @date 2018-08-22 15:28
 */
@Service
public class QueryCrsAccountPeriodService {
	private static Logger log = LoggerFactory.getLogger(QueryCrsAccountPeriodService.class);

    @Autowired
    private CrsAccountPeriodMapper crsAccountPeriodMapper;
    
    //商户对账查询
    public List<AccountPeriodDto> queryCrsAccountPeriod(QueryAccountPeriodDto queryAccountPeriodDto) throws Exception {
    	List<AccountPeriodDto> resultList = new ArrayList<AccountPeriodDto>();
    	log.info("----queryCrsAccountPeriod start-------------");
    	if (null == queryAccountPeriodDto) {
    		throw new Exception("Please input the necessary parameters.");
    	}
    	
    	ZoneId zoneId = ZoneId.systemDefault();
    	SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
    	List<QueryAccountPeriodDto> list = new ArrayList<QueryAccountPeriodDto>();
    	String startYearAndMonthQuery = queryAccountPeriodDto.getStartYearAndMonthQuery();//开始账期
    	String endYearAndMonthQuery = queryAccountPeriodDto.getEndYearAndMonthQuery();//结束账期
    	String[] startYearMonthArray = startYearAndMonthQuery.split("-");
    	String[] endYearAndMonthArray = endYearAndMonthQuery.split("-");
    	LocalDate start = LocalDate.of(Integer.valueOf(startYearMonthArray[0]), Integer.valueOf(startYearMonthArray[1]), 1);
    	LocalDate end = LocalDate.of(Integer.valueOf(endYearAndMonthArray[0]), Integer.valueOf(endYearAndMonthArray[1]), 1);
    	
    	if (startYearAndMonthQuery.equals(endYearAndMonthQuery)) {
    		queryAccountPeriodDto.setAccountPeriod(startYearMonthArray[0] + (startYearMonthArray[1].length() == 1 ? "0" + startYearMonthArray[1] : startYearMonthArray[1]));
    		queryAccountPeriodDto.setAccountPeriodStart(startYearMonthArray[0] + "-" + (startYearMonthArray[1].length() == 1 ? "0" + startYearMonthArray[1] : startYearMonthArray[1]) + "-01");
    		
    		LocalDate lastDayOfCurrentMonth = start.with(TemporalAdjusters.lastDayOfMonth()); 
	        ZonedDateTime zdt = lastDayOfCurrentMonth.atStartOfDay(zoneId);
	        Date lastDayOfCurrentMonthDate = Date.from(zdt.toInstant());//同步年月的最后一天
	        
	        LocalDate firstDayOfNextMonth = start.plusMonths(1L).with(TemporalAdjusters.firstDayOfMonth()); 
	        ZonedDateTime zdt1 = firstDayOfNextMonth.atStartOfDay(zoneId);
	        Date firstDayOfNextMonthDate = Date.from(zdt1.toInstant());//同步年月的最后一天
	        
    		queryAccountPeriodDto.setAccountPeriodEnd(sdf.format(lastDayOfCurrentMonthDate));
    		queryAccountPeriodDto.setNextAccountPeriodStart(sdf.format(firstDayOfNextMonthDate));
    		list.add(queryAccountPeriodDto);
    	} else {
    		QueryAccountPeriodDto insertDto = null;
        	List<String> yearMonthArray = getBetweenYearMonth(start,end);
            for (String str : yearMonthArray) {
            	insertDto = new QueryAccountPeriodDto();
            	BeanUtils.copyProperties(queryAccountPeriodDto, insertDto);
            	String[] array = str.split("-");
            	LocalDate startDate = LocalDate.of(Integer.valueOf(array[0]), Integer.valueOf(array[1]), 1);
            	insertDto.setAccountPeriod(array[0] + (array[1].length() == 1 ? "0" + array[1] : array[1]));
            	insertDto.setAccountPeriodStart(array[0] + "-" + (array[1].length() == 1 ? "0" + array[1] : array[1]) + "-01");
        		
        		LocalDate lastDayOfCurrentMonth = startDate.with(TemporalAdjusters.lastDayOfMonth()); 
    	        ZonedDateTime zdt = lastDayOfCurrentMonth.atStartOfDay(zoneId);
    	        Date lastDayOfCurrentMonthDate = Date.from(zdt.toInstant());//同步年月的最后一天
    	        
    	        LocalDate firstDayOfNextMonth = startDate.plusMonths(1L).with(TemporalAdjusters.firstDayOfMonth()); 
    	        ZonedDateTime zdt1 = firstDayOfNextMonth.atStartOfDay(zoneId);
    	        Date firstDayOfNextMonthDate = Date.from(zdt1.toInstant());//同步年月的最后一天
    	        
    	        insertDto.setAccountPeriodEnd(sdf.format(lastDayOfCurrentMonthDate));
    	        insertDto.setNextAccountPeriodStart(sdf.format(firstDayOfNextMonthDate));
        		list.add(insertDto);
			}
    	}
    	
    	if (null != list && !list.isEmpty()) {
    		resultList = crsAccountPeriodMapper.queryAccountPeriodByCondition(list);
    		
    		if (null != resultList && !resultList.isEmpty()) {
    			//获取CRS中所有的bookings表的枚举类型
    			List<CrsEnumsDto> crsEnumsDtoList = crsAccountPeriodMapper.queryCrsEnumByTableName("bookings");
    			
    			resultList.forEach(q->{
    				//订单渠道
    				if (StringUtils.isNotEmpty(q.getOrderChannel())) {
    					if (crsEnumsDtoList.stream().anyMatch(m->"source".equals(m.getColumnName()) && m.getEnumKey().equals(Integer.valueOf(q.getOrderChannel())))) {
    						q.setOrderChannel(crsEnumsDtoList.stream().filter(m->"source".equals(m.getColumnName()) && m.getEnumKey().equals(Integer.valueOf(q.getOrderChannel()))).collect(Collectors.toList()).get(0).getEnumVal());
    					}
    				} else {
    					q.setOrderChannel("");
    				}
    				
    				//计算房间价格 roomPrice
    				if (q.getRoomsNumber() != null && q.getRoomsNumber().intValue() != 0 && q.getCheckInDays() != null && q.getCheckInDays().intValue() != 0) {
    					q.setRoomPrice(q.getOrderTotalAmount().divide(new BigDecimal(q.getRoomsNumber()).multiply(new BigDecimal(q.getCheckInDays())), 2, BigDecimal.ROUND_HALF_UP));
    				}
    				
    				//本月应结算总额（计算）,=房价*天数 currentMonthSettlementTotalAmountCompute
    				if (null != q.getRoomPrice() && null != q.getCheckInDays()) {
    					q.setCurrentMonthSettlementTotalAmountCompute(q.getRoomPrice().multiply(new BigDecimal(q.getCheckInDays())).setScale(2, BigDecimal.ROUND_HALF_UP));
    				} else {
    					q.setCurrentMonthSettlementTotalAmountCompute(null);
    				}
    				//订单状态描述;
    				if (q.getStatusCode() != null) {
    					if (crsEnumsDtoList.stream().anyMatch(m->"status".equals(m.getColumnName()) && m.getEnumKey().intValue() == q.getStatusCode().intValue())) {
    						q.setStatusDes(crsEnumsDtoList.stream().filter(m->"status".equals(m.getColumnName()) && m.getEnumKey().intValue() == q.getStatusCode().intValue()).collect(Collectors.toList()).get(0).getEnumVal());
    					}
    				} else {
    					q.setStatusDes("");
    				}
    				//本月已用间夜数 currentMonthRoomsNumber
    				q.setCurrentMonthRoomsNumber(q.getRoomsNumber() * q.getCheckInDays());
    				//本月应结算总额 currentMonthSettlementTotalAmount
    				q.setCurrentMonthSettlementTotalAmount(null); //待定
    				//支付方式 paymentMethod
    				if (StringUtils.isNotEmpty(q.getPaymentMethod())) {
    					if (q.getPaymentMethod().startsWith("[")) {
    						JSONArray arr = JSONArray.fromObject(q.getPaymentMethod());
    						q.setPaymentMethod(arr.getJSONObject(0).getString("mode"));
    					} else {
    						JSONObject obj = JSONObject.fromObject(q.getPaymentMethod());
        					if (null != obj) {
        						q.setPaymentMethod(obj.getString("mode"));
        					}
    					}
    				}    				
    				//支付类型（预付/后付费）paymentType
    				if (StringUtils.isNotEmpty(q.getPaymentType())) {
    					if (crsEnumsDtoList.stream().anyMatch(m->"payment_type".equals(m.getColumnName()) && m.getEnumKey().equals(Integer.parseInt(q.getPaymentType())))) {
    						q.setPaymentType(crsEnumsDtoList.stream().filter(m->"payment_type".equals(m.getColumnName()) && m.getEnumKey().equals(Integer.parseInt(q.getPaymentType()))).collect(Collectors.toList()).get(0).getEnumVal());
    					}
    				}
    				//本月匹配费率 currentMonthRate
    				
    				//OYO share
    				
    			});
    		}
    		
    	}
    	
    	log.info("----queryCrsAccountPeriod end-------------");
        return resultList;
    }
    
    //汇总统计查询
    public List<AccountPeriodTotalDto> queryCrsTotalAccountPeriod(QueryAccountPeriodDto queryAccountPeriodDto) throws Exception {
    	List<AccountPeriodTotalDto> resultList = new ArrayList<AccountPeriodTotalDto>();
    	log.info("----queryCrsTotalAccountPeriod start-------------");
    	if (null == queryAccountPeriodDto) {
    		throw new Exception("Please input the necessary parameters.");
    	}
    	
    	ZoneId zoneId = ZoneId.systemDefault();
    	SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
    	List<QueryAccountPeriodDto> list = new ArrayList<QueryAccountPeriodDto>();
    	String startYearAndMonthQuery = queryAccountPeriodDto.getStartYearAndMonthQuery();//开始账期
    	String endYearAndMonthQuery = queryAccountPeriodDto.getEndYearAndMonthQuery();//结束账期
    	String[] startYearMonthArray = startYearAndMonthQuery.split("-");
    	String[] endYearAndMonthArray = endYearAndMonthQuery.split("-");
    	LocalDate start = LocalDate.of(Integer.valueOf(startYearMonthArray[0]), Integer.valueOf(startYearMonthArray[1]), 1);
    	LocalDate end = LocalDate.of(Integer.valueOf(endYearAndMonthArray[0]), Integer.valueOf(endYearAndMonthArray[1]), 1);
    	
    	if (startYearAndMonthQuery.equals(endYearAndMonthQuery)) {
    		queryAccountPeriodDto.setAccountPeriod(startYearMonthArray[0] + (startYearMonthArray[1].length() == 1 ? "0" + startYearMonthArray[1] : startYearMonthArray[1]));
    		queryAccountPeriodDto.setAccountPeriodStart(startYearMonthArray[0] + "-" + (startYearMonthArray[1].length() == 1 ? "0" + startYearMonthArray[1] : startYearMonthArray[1]) + "-01");
    		
    		LocalDate lastDayOfCurrentMonth = start.with(TemporalAdjusters.lastDayOfMonth()); 
	        ZonedDateTime zdt = lastDayOfCurrentMonth.atStartOfDay(zoneId);
	        Date lastDayOfCurrentMonthDate = Date.from(zdt.toInstant());//同步年月的最后一天
	        
	        LocalDate firstDayOfNextMonth = start.plusMonths(1L).with(TemporalAdjusters.firstDayOfMonth()); 
	        ZonedDateTime zdt1 = firstDayOfNextMonth.atStartOfDay(zoneId);
	        Date firstDayOfNextMonthDate = Date.from(zdt1.toInstant());//同步年月的最后一天
	        
    		queryAccountPeriodDto.setAccountPeriodEnd(sdf.format(lastDayOfCurrentMonthDate));
    		queryAccountPeriodDto.setNextAccountPeriodStart(sdf.format(firstDayOfNextMonthDate));
    		list.add(queryAccountPeriodDto);
    	} else {
    		QueryAccountPeriodDto insertDto = null;
        	List<String> yearMonthArray = getBetweenYearMonth(start,end);
            for (String str : yearMonthArray) {
            	insertDto = new QueryAccountPeriodDto();
            	BeanUtils.copyProperties(queryAccountPeriodDto, insertDto);
            	String[] array = str.split("-");
            	LocalDate startDate = LocalDate.of(Integer.valueOf(array[0]), Integer.valueOf(array[1]), 1);
            	insertDto.setAccountPeriod(array[0] + (array[1].length() == 1 ? "0" + array[1] : array[1]));
            	insertDto.setAccountPeriodStart(array[0] + "-" + (array[1].length() == 1 ? "0" + array[1] : array[1]) + "-01");
        		
        		LocalDate lastDayOfCurrentMonth = startDate.with(TemporalAdjusters.lastDayOfMonth()); 
    	        ZonedDateTime zdt = lastDayOfCurrentMonth.atStartOfDay(zoneId);
    	        Date lastDayOfCurrentMonthDate = Date.from(zdt.toInstant());//同步年月的最后一天
    	        
    	        LocalDate firstDayOfNextMonth = startDate.plusMonths(1L).with(TemporalAdjusters.firstDayOfMonth()); 
    	        ZonedDateTime zdt1 = firstDayOfNextMonth.atStartOfDay(zoneId);
    	        Date firstDayOfNextMonthDate = Date.from(zdt1.toInstant());//同步年月的最后一天
    	        
    	        insertDto.setAccountPeriodEnd(sdf.format(lastDayOfCurrentMonthDate));
    	        insertDto.setNextAccountPeriodStart(sdf.format(firstDayOfNextMonthDate));
        		list.add(insertDto);
			}
    	}
    	
    	if (null != list && !list.isEmpty()) {
    		resultList = crsAccountPeriodMapper.queryTotalAccountPeriodByCondition(list);
    		
    		if (null != resultList && !resultList.isEmpty()) {
    			
    			resultList.forEach(q->{
    				
    				//计算房间价格 roomPrice
    				if (q.getRoomsNumber() != null && q.getRoomsNumber().intValue() != 0 && q.getCheckInDays() != null && q.getCheckInDays().intValue() != 0) {
    					q.setRoomPrice(q.getOrderTotalAmount().divide(new BigDecimal(q.getRoomsNumber()).multiply(new BigDecimal(q.getCheckInDays())), 2, BigDecimal.ROUND_HALF_UP));
    				}
    				
    				//本月应结算总额（计算）,=房价*天数 currentMonthSettlementTotalAmountCompute
    				if (null != q.getRoomPrice() && null != q.getCheckInDays()) {
    					q.setCurrentMonthSettlementTotalAmountCompute(q.getRoomPrice().multiply(new BigDecimal(q.getCheckInDays())).setScale(2, BigDecimal.ROUND_HALF_UP));
    				} else {
    					q.setCurrentMonthSettlementTotalAmountCompute(null);
    				}
    				//本月已用间夜数 currentMonthRoomsNumber
    				q.setCurrentMonthRoomsNumber(q.getRoomsNumber() * q.getCheckInDays());
    				//本月应结算总额 currentMonthSettlementTotalAmount
    				q.setCurrentMonthSettlementTotalAmount(null); //待定
    				//本月匹配费率 currentMonthRate
    				
    				//OYO share
    				
    			});
    		}
    		
    	}
    	
    	log.info("----queryCrsTotalAccountPeriod end-------------");
        return resultList;
    }
    
    //明细查询
    public List<AccountPeriodDetailsDto> queryCrsDetailsAccountPeriod(QueryAccountPeriodDto queryAccountPeriodDto) throws Exception {
    	List<AccountPeriodDetailsDto> resultList = new ArrayList<AccountPeriodDetailsDto>();
    	log.info("----queryCrsDetailsAccountPeriod start-------------");
    	if (null == queryAccountPeriodDto) {
    		throw new Exception("Please input the necessary parameters.");
    	}
    	
    	ZoneId zoneId = ZoneId.systemDefault();
    	SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
    	List<QueryAccountPeriodDto> list = new ArrayList<QueryAccountPeriodDto>();
    	String startYearAndMonthQuery = queryAccountPeriodDto.getStartYearAndMonthQuery();//开始账期
    	String endYearAndMonthQuery = queryAccountPeriodDto.getEndYearAndMonthQuery();//结束账期
    	String[] startYearMonthArray = startYearAndMonthQuery.split("-");
    	String[] endYearAndMonthArray = endYearAndMonthQuery.split("-");
    	LocalDate start = LocalDate.of(Integer.valueOf(startYearMonthArray[0]), Integer.valueOf(startYearMonthArray[1]), 1);
    	LocalDate end = LocalDate.of(Integer.valueOf(endYearAndMonthArray[0]), Integer.valueOf(endYearAndMonthArray[1]), 1);
    	
    	if (startYearAndMonthQuery.equals(endYearAndMonthQuery)) {
    		queryAccountPeriodDto.setAccountPeriod(startYearMonthArray[0] + (startYearMonthArray[1].length() == 1 ? "0" + startYearMonthArray[1] : startYearMonthArray[1]));
    		queryAccountPeriodDto.setAccountPeriodStart(startYearMonthArray[0] + "-" + (startYearMonthArray[1].length() == 1 ? "0" + startYearMonthArray[1] : startYearMonthArray[1]) + "-01");
    		
    		LocalDate lastDayOfCurrentMonth = start.with(TemporalAdjusters.lastDayOfMonth()); 
	        ZonedDateTime zdt = lastDayOfCurrentMonth.atStartOfDay(zoneId);
	        Date lastDayOfCurrentMonthDate = Date.from(zdt.toInstant());//同步年月的最后一天
	        
	        LocalDate firstDayOfNextMonth = start.plusMonths(1L).with(TemporalAdjusters.firstDayOfMonth()); 
	        ZonedDateTime zdt1 = firstDayOfNextMonth.atStartOfDay(zoneId);
	        Date firstDayOfNextMonthDate = Date.from(zdt1.toInstant());//同步年月的最后一天
	        
    		queryAccountPeriodDto.setAccountPeriodEnd(sdf.format(lastDayOfCurrentMonthDate));
    		queryAccountPeriodDto.setNextAccountPeriodStart(sdf.format(firstDayOfNextMonthDate));
    		list.add(queryAccountPeriodDto);
    	} else {
    		QueryAccountPeriodDto insertDto = null;
        	List<String> yearMonthArray = getBetweenYearMonth(start,end);
            for (String str : yearMonthArray) {
            	insertDto = new QueryAccountPeriodDto();
            	BeanUtils.copyProperties(queryAccountPeriodDto, insertDto);
            	String[] array = str.split("-");
            	LocalDate startDate = LocalDate.of(Integer.valueOf(array[0]), Integer.valueOf(array[1]), 1);
            	insertDto.setAccountPeriod(array[0] + (array[1].length() == 1 ? "0" + array[1] : array[1]));
            	insertDto.setAccountPeriodStart(array[0] + "-" + (array[1].length() == 1 ? "0" + array[1] : array[1]) + "-01");
        		
        		LocalDate lastDayOfCurrentMonth = startDate.with(TemporalAdjusters.lastDayOfMonth()); 
    	        ZonedDateTime zdt = lastDayOfCurrentMonth.atStartOfDay(zoneId);
    	        Date lastDayOfCurrentMonthDate = Date.from(zdt.toInstant());//同步年月的最后一天
    	        
    	        LocalDate firstDayOfNextMonth = startDate.plusMonths(1L).with(TemporalAdjusters.firstDayOfMonth()); 
    	        ZonedDateTime zdt1 = firstDayOfNextMonth.atStartOfDay(zoneId);
    	        Date firstDayOfNextMonthDate = Date.from(zdt1.toInstant());//同步年月的最后一天
    	        
    	        insertDto.setAccountPeriodEnd(sdf.format(lastDayOfCurrentMonthDate));
    	        insertDto.setNextAccountPeriodStart(sdf.format(firstDayOfNextMonthDate));
        		list.add(insertDto);
			}
    	}
    	
    	if (null != list && !list.isEmpty()) {
    		resultList = crsAccountPeriodMapper.queryDetailsAccountPeriodByCondition(list);
    		
    		if (null != resultList && !resultList.isEmpty()) {
    			//获取CRS中所有的bookings表的枚举类型
    			List<CrsEnumsDto> crsEnumsDtoList = crsAccountPeriodMapper.queryCrsEnumByTableName("bookings");
    			
    			resultList.forEach(q->{
    				//订单渠道
    				if (StringUtils.isNotEmpty(q.getOrderChannel())) {
    					if (crsEnumsDtoList.stream().anyMatch(m->"source".equals(m.getColumnName()) && m.getEnumKey().equals(Integer.valueOf(q.getOrderChannel())))) {
    						q.setOrderChannel(crsEnumsDtoList.stream().filter(m->"source".equals(m.getColumnName()) && m.getEnumKey().equals(Integer.valueOf(q.getOrderChannel()))).collect(Collectors.toList()).get(0).getEnumVal());
    					}
    				} else {
    					q.setOrderChannel("");
    				}
    				
    				//计算房间价格 roomPrice
    				if (q.getRoomsNumber() != null && q.getRoomsNumber().intValue() != 0 && q.getCheckInDays() != null && q.getCheckInDays().intValue() != 0) {
    					q.setRoomPrice(q.getOrderTotalAmount().divide(new BigDecimal(q.getRoomsNumber()).multiply(new BigDecimal(q.getCheckInDays())), 2, BigDecimal.ROUND_HALF_UP));
    				}
    				
    				//本月应结算总额（计算）,=房价*天数 currentMonthSettlementTotalAmountCompute
    				if (null != q.getRoomPrice() && null != q.getCheckInDays()) {
    					q.setCurrentMonthSettlementTotalAmountCompute(q.getRoomPrice().multiply(new BigDecimal(q.getCheckInDays())).setScale(2, BigDecimal.ROUND_HALF_UP));
    				} else {
    					q.setCurrentMonthSettlementTotalAmountCompute(null);
    				}
    				//订单状态描述;
    				if (q.getStatusCode() != null) {
    					if (crsEnumsDtoList.stream().anyMatch(m->"status".equals(m.getColumnName()) && m.getEnumKey().intValue() == q.getStatusCode().intValue())) {
    						q.setStatusDes(crsEnumsDtoList.stream().filter(m->"status".equals(m.getColumnName()) && m.getEnumKey().intValue() == q.getStatusCode().intValue()).collect(Collectors.toList()).get(0).getEnumVal());
    					}
    				} else {
    					q.setStatusDes("");
    				}
    				//本月已用间夜数 currentMonthRoomsNumber
    				q.setCurrentMonthRoomsNumber(q.getRoomsNumber() * q.getCheckInDays());
    				//本月应结算总额 currentMonthSettlementTotalAmount
    				q.setCurrentMonthSettlementTotalAmount(null); //待定
    				//支付方式 paymentMethod
    				if (StringUtils.isNotEmpty(q.getPaymentMethod())) {
    					if (q.getPaymentMethod().startsWith("[")) {
    						JSONArray arr = JSONArray.fromObject(q.getPaymentMethod());
    						q.setPaymentMethod(arr.getJSONObject(0).getString("mode"));
    					} else {
    						JSONObject obj = JSONObject.fromObject(q.getPaymentMethod());
        					if (null != obj) {
        						q.setPaymentMethod(obj.getString("mode"));
        					}
    					}
    				}    				
    				//支付类型（预付/后付费）paymentType
    				if (StringUtils.isNotEmpty(q.getPaymentType())) {
    					if (crsEnumsDtoList.stream().anyMatch(m->"payment_type".equals(m.getColumnName()) && m.getEnumKey().equals(Integer.parseInt(q.getPaymentType())))) {
    						q.setPaymentType(crsEnumsDtoList.stream().filter(m->"payment_type".equals(m.getColumnName()) && m.getEnumKey().equals(Integer.parseInt(q.getPaymentType()))).collect(Collectors.toList()).get(0).getEnumVal());
    					}
    				}
    				//本月匹配费率 currentMonthRate
    				
    				//OYO share
    				
    			});
    		}
    		
    	}
    	
    	log.info("----queryDetailsCrsAccountPeriod end-------------");
        return resultList;
    }
    
    /** 
     * 获取两个日期之间的所有年月 
     * @param start
     * @param end
     * @return 
     */  
    private static List<String> getBetweenYearMonth(LocalDate start, LocalDate end) {  
        List<String> list = new ArrayList<>();  
        long distance = ChronoUnit.DAYS.between(start, end);  
        if (distance < 1) {  
            return list;  
        }  
        Stream.iterate(start, d -> {  
            return d.plusDays(1);  
        }).limit(distance + 1).forEach(f -> {
        	if (!list.contains(f.toString().substring(0, 7))) {
        		list.add(f.toString().substring(0, 7));  
        	}
        });  
        return list;  
    }
    
}
