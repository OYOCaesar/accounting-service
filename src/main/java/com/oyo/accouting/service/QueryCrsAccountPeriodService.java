package com.oyo.accouting.service;

import java.math.BigDecimal;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.temporal.ChronoUnit;
import java.time.temporal.TemporalAdjusters;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.oyo.accouting.bean.AccountPeriodDto;
import com.oyo.accouting.bean.CrsEnumsDto;
import com.oyo.accouting.bean.OyoShareDto;
import com.oyo.accouting.bean.QueryAccountPeriodDto;
import com.oyo.accouting.bean.SyncCrsArAndApDto;
import com.oyo.accouting.mapper.accounting.AccountPeriodMapper;
import com.oyo.accouting.mapper.accounting.AccountingOyoShareMapper;
import com.oyo.accouting.mapper.accounting.SyncCrsArAndApMapper;
import com.oyo.accouting.mapper.crs.CrsAccountPeriodMapper;
import com.oyo.accouting.pojo.AccountPeriod;

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
    
    @Autowired
    private AccountPeriodMapper accountPeriodMapper;
    
    @Autowired
    private SyncCrsArAndApMapper syncCrsArAndApMapper;
    
    @Autowired
    private AccountingOyoShareMapper accountingOyoShareMapper;
    
    //生成recon数据
    @Transactional(value="accountingTransactionManager", rollbackFor = Exception.class)
    public String generateReconData(QueryAccountPeriodDto queryAccountPeriodDto) throws Exception {
    	log.info("----Generate recon data start-------------");
    	StringBuffer buf = new StringBuffer();
    	try {
    		List<AccountPeriod> resultList = new ArrayList<AccountPeriod>();
        	if (null == queryAccountPeriodDto) {
        		throw new Exception("Please input the necessary parameters.");
        	}
        	
        	ZoneId zoneId = ZoneId.systemDefault();
        	SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        	String startYearAndMonthQuery = queryAccountPeriodDto.getStartYearAndMonthQuery();//开始账期
        	String[] startYearMonthArray = startYearAndMonthQuery.split("-");
        	LocalDate start = LocalDate.of(Integer.valueOf(startYearMonthArray[0]), Integer.valueOf(startYearMonthArray[1]), 1);
        	
        	queryAccountPeriodDto.setAccountPeriod(startYearMonthArray[0] + (startYearMonthArray[1].length() == 1 ? "0" + startYearMonthArray[1] : startYearMonthArray[1]));
    		queryAccountPeriodDto.setAccountPeriodStart(startYearMonthArray[0] + "-" + (startYearMonthArray[1].length() == 1 ? "0" + startYearMonthArray[1] : startYearMonthArray[1]) + "-01");
    		queryAccountPeriodDto.setAccountPeriodSecondStart(startYearMonthArray[0] + "-" + (startYearMonthArray[1].length() == 1 ? "0" + startYearMonthArray[1] : startYearMonthArray[1]) + "-02");
    		
    		LocalDate lastDayOfCurrentMonth = start.with(TemporalAdjusters.lastDayOfMonth()); 
	        ZonedDateTime zdt = lastDayOfCurrentMonth.atStartOfDay(zoneId);
	        Date lastDayOfCurrentMonthDate = Date.from(zdt.toInstant());//同步年月的最后一天
	        
	        LocalDate firstDayOfNextMonth = start.plusMonths(1L).with(TemporalAdjusters.firstDayOfMonth()); 
	        ZonedDateTime zdt1 = firstDayOfNextMonth.atStartOfDay(zoneId);
	        Date firstDayOfNextMonthDate = Date.from(zdt1.toInstant());//同步年月的最后一天
	        
    		queryAccountPeriodDto.setAccountPeriodEnd(sdf.format(lastDayOfCurrentMonthDate));
    		queryAccountPeriodDto.setNextAccountPeriodStart(sdf.format(firstDayOfNextMonthDate));
        	
    		queryAccountPeriodDto.setPageSize(null);
    		resultList = crsAccountPeriodMapper.queryAccountPeriodByCondition(queryAccountPeriodDto);
    		
    		if (null != resultList && !resultList.isEmpty()) {
    			//获取CRS中所有的bookings表的枚举类型
    			List<CrsEnumsDto> crsEnumsDtoList = crsAccountPeriodMapper.queryCrsEnumByTableName("bookings");
    			Map<String,Object> rateMap = new HashMap<String,Object>();
    			String hotelIds = "";
    			List<Integer> hotelIdList = resultList.stream().map(AccountPeriod::getHotelId).distinct().collect(Collectors.toList());
    			for (int i = 0; i < hotelIdList.size(); i++) {
    				Integer hotelId = hotelIdList.get(i);
					if (i != hotelIdList.size() - 1) {
						hotelIds += hotelId + ",";
					} else {
						hotelIds += hotelId;
					}
				}
    			rateMap.put("hotelIds", hotelIds);
    			//获取账期的汇率列表
    			List<SyncCrsArAndApDto> rateList = syncCrsArAndApMapper.selectRateListByMap(rateMap);
    			
    			OyoShareDto info = new OyoShareDto();
    			info.setIsTest("t");
    			//过滤掉黑名单或测试酒店
    			List<OyoShareDto> hotelExceptList = this.accountingOyoShareMapper.queryOyoShareList(info);
    			if (null != hotelExceptList && !hotelExceptList.isEmpty()) {
    				List<String> hotelIdHeiList = hotelExceptList.stream().map(OyoShareDto::getHotelId).collect(Collectors.toList());
    				resultList = resultList.stream().filter(q->!hotelIdHeiList.contains(q.getHotelId().toString())).collect(Collectors.toList());
    			}
    			
    			//查询汇率表，以便获取CRS中城市没有维护的区域
    			OyoShareDto oyoShare = new OyoShareDto();
    			oyoShare.setValidDate(startYearMonthArray[0] + "-" + (startYearMonthArray[1].length() == 1 ? "0" + startYearMonthArray[1] : startYearMonthArray[1]));
    			List<OyoShareDto> oyoShareDtoList = accountingOyoShareMapper.queryOyoShareList(oyoShare);
    			
    			SimpleDateFormat sdfCheck = new SimpleDateFormat("yyyy-MM-dd");
    			resultList.forEach(q->{
    			    //设置区域
    				if (StringUtils.isEmpty(q.getRegion()) && null != oyoShareDtoList && !oyoShareDtoList.isEmpty() &&
    						oyoShareDtoList.stream().anyMatch(m->m.getHotelId().equals(m.getHotelId()))) {
    					String region = oyoShareDtoList.stream().filter(m->m.getHotelId().equals(m.getHotelId())).map(OyoShareDto::getZoneName).collect(Collectors.toList()).get(0);
    					q.setRegion(region);
    				}
    				
    			    Date checkInDate = null;
    				Date checkOutDate = null;
    				int days = 0;//总入住天数
    				Date startDateOfAccountPeriodDate = null;
    				Date endDateOfAccountPeriodDate = null;
    				Integer checkInDays = 0;// 本期入住天数
					try {
						checkInDate = sdfCheck.parse(q.getCheckInDate());
						checkOutDate = sdfCheck.parse(q.getCheckOutDate());
        				days = (int) ((checkOutDate.getTime() - checkInDate.getTime()) / (1000 * 3600 * 24));//总共入住天数
        				
						startDateOfAccountPeriodDate = sdfCheck.parse(queryAccountPeriodDto.getAccountPeriodStart());//账期查询开始日期（日期类型）
						endDateOfAccountPeriodDate = sdfCheck.parse(queryAccountPeriodDto.getAccountPeriodEnd());//账期查询结束日期（日期类型）
						
						//本账期开始日期
						if (checkInDate.compareTo(startDateOfAccountPeriodDate) < 0) {
	        			    q.setStartDateOfAccountPeriod(queryAccountPeriodDto.getAccountPeriodStart());
						} else {
	        			    q.setStartDateOfAccountPeriod(q.getCheckInDate());
						}
						
						//本账期结束日期
						if (checkOutDate.compareTo(endDateOfAccountPeriodDate) <= 0) {
	        			    q.setEndDateOfAccountPeriod(q.getCheckOutDate());
						} else {
	        			    q.setEndDateOfAccountPeriod(queryAccountPeriodDto.getNextAccountPeriodStart());
						}
        			    
						Date startDateOfAccountPeriodDateComputer = sdfCheck.parse(q.getStartDateOfAccountPeriod());//账期开始日期（日期类型）
						Date endDateOfAccountPeriodDateComputer = sdfCheck.parse(q.getEndDateOfAccountPeriod());//账期结束日期（日期类型）
						
						// 本期入住天数
						checkInDays = (int) ((endDateOfAccountPeriodDateComputer.getTime() - startDateOfAccountPeriodDateComputer.getTime()) / (1000 * 3600 * 24));//本期入住天数
						
        			    // 本期入住天数
        			    q.setCheckInDays(checkInDays);
						
					} catch (ParseException e) {
						log.error("Date convert exception:{}",e);
					}
    			    
    				//替换表情符号为空
    				q.setGuestName(q.getGuestName().replaceAll("[\\ud800\\udc00-\\udbff\\udfff\\ud800-\\udfff]", ""));
    				
    				//订单渠道
    				if (null != q.getOrderChannelCode()) {
    					if (crsEnumsDtoList.stream().anyMatch(m->"source".equals(m.getColumnName()) && m.getEnumKey().equals(q.getOrderChannelCode()))) {
    						q.setOrderChannel(crsEnumsDtoList.stream().filter(m->"source".equals(m.getColumnName()) && m.getEnumKey().equals(q.getOrderChannelCode())).collect(Collectors.toList()).get(0).getEnumVal());
    					}
    				} else {
    					q.setOrderChannel("");
    				}
    				
    				//计算房间价格 roomPrice
    				if (q.getRoomsNumber() != null && q.getRoomsNumber().intValue() != 0 && days != 0) {
    					q.setRoomPrice(q.getOrderTotalAmount().divide(new BigDecimal(q.getRoomsNumber()).multiply(new BigDecimal(days)), 2, BigDecimal.ROUND_HALF_UP));
    				}
    				
    				//本月应结算总额（计算）,=房价*天数*客房数 currentMonthSettlementTotalAmountCompute
    				if (null != q.getRoomPrice() && null != q.getCheckInDays()) {
    					q.setCurrentMonthSettlementTotalAmountCompute(q.getRoomPrice().multiply(new BigDecimal(q.getRoomsNumber())).multiply(new BigDecimal(q.getCheckInDays())).setScale(2, BigDecimal.ROUND_HALF_UP));
    				} else {
    					q.setCurrentMonthSettlementTotalAmountCompute(null);
    				}
    				
    				//订单状态描述;
    				if (q.getStatusCode() != null) {
    					if (crsEnumsDtoList.stream().anyMatch(m->"status".equals(m.getColumnName()) && m.getEnumKey().intValue() == q.getStatusCode().intValue())) {
    						q.setStatusDesc(crsEnumsDtoList.stream().filter(m->"status".equals(m.getColumnName()) && m.getEnumKey().intValue() == q.getStatusCode().intValue()).collect(Collectors.toList()).get(0).getEnumVal());
    					}
    				} else {
    					q.setStatusDesc("");
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
    				if (null != rateList && !rateList.isEmpty() && rateList.stream().anyMatch(m->m.getHotelId().equals(q.getHotelId()) && m.getRate() != null)) {
    					BigDecimal rate = rateList.stream().filter(m->m.getHotelId().equals(q.getHotelId()) && m.getRate() != null).map(SyncCrsArAndApDto::getRate).collect(Collectors.toList()).get(0);
    					q.setCurrentMonthRate(new BigDecimal("100").subtract(rate));
    				}
    				
    				//OYO share
    				if (q.getCurrentMonthRate() != null && q.getCurrentMonthRate().compareTo(BigDecimal.ZERO) > 0
    					&& q.getCurrentMonthSettlementTotalAmountCompute() != null) {
    					q.setOyoShare(q.getCurrentMonthSettlementTotalAmountCompute().multiply(q.getCurrentMonthRate()).multiply(new BigDecimal("0.01")).setScale(2,BigDecimal.ROUND_HALF_UP));
    				}
    				
    				//创建时间
    				q.setCreateTime(new Date());
    				
    			});
    			
    			if (this.accountPeriodMapper.selectByAccountPeriod(queryAccountPeriodDto.getAccountPeriod()) > 0) {
    				//先删除所选账期的数据,然后再插入所选账期数据
        			int deleteCount = this.accountPeriodMapper.deleteByAccountPeriod(queryAccountPeriodDto.getAccountPeriod());
    				if (deleteCount < 1) {
    					buf.append("Delete acccount period:'" + queryAccountPeriodDto.getAccountPeriod() + "' failed!<br/>");
    					throw new Exception("Delete acccount period:'" + queryAccountPeriodDto.getAccountPeriod() + "' failed!");
    				}
    			}
				
			    //每1000条批量插入一次
        		int len = (resultList.size() % 1000 == 0 ? resultList.size() / 1000 : ((resultList.size() / 1000) + 1));
        		for (int i = 0; i < len; i++) {
        			int startIndex = 0;
        			int endIndex = 0;
        			if (len <= 1) {
        				endIndex = resultList.size();
        			} else {
        				startIndex = i * 1000;
        				if (i == len - 1) {
            				endIndex = resultList.size();
            			} else {
            				endIndex = (i + 1) * 1000;
            			}
        			}
        			//批量插入所选账期数据
        			int insertCount = this.accountPeriodMapper.insertBtach(resultList.subList(startIndex, endIndex));
        			if (insertCount < 1) {
				    	buf.append("Insert acccount period:'" + queryAccountPeriodDto.getAccountPeriod() + "' failed!<br/>");
				    	throw new Exception("Insert acccount period:'" + queryAccountPeriodDto.getAccountPeriod() + "' failed");
				    }
        			
        		}
    			
    		}
    	} catch (Exception e) {
    		log.info("Generate recon data throw exception:{}", e);
    		throw e;
    	}
    	log.info("----Generate recon data end-------------");
    	return buf.toString();
    }
    
    //条件查询账期对账信息
    public List<AccountPeriodDto> queryAccountPeriodByCondition(QueryAccountPeriodDto queryAccountPeriodDto) throws Exception {
    	List<AccountPeriodDto> resultList = new ArrayList<AccountPeriodDto>();
    	if (null == queryAccountPeriodDto) {
    		throw new Exception("Please input the necessary parameters.");
    	}
    	resultList = this.accountPeriodMapper.queryAccountPeriodByCondition(queryAccountPeriodDto);
        return resultList;
    }
    
    //条件查询账期统计对账信息
    public List<AccountPeriodDto> queryAccountPeriodStatisticsByCondition(QueryAccountPeriodDto queryAccountPeriodDto) throws Exception {
    	List<AccountPeriodDto> resultList = new ArrayList<AccountPeriodDto>();
    	if (null == queryAccountPeriodDto) {
    		throw new Exception("Please input the necessary parameters.");
    	}
    	resultList = this.accountPeriodMapper.queryAccountPeriodStatisticsByCondition(queryAccountPeriodDto);
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
