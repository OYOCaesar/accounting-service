package com.oyo.accouting.service;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.temporal.TemporalAdjusters;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.stream.Collectors;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.oyo.accouting.bean.SyncLogDto;
import com.oyo.accouting.mapper.accounting.AccountingSyncLogMapper;
import com.oyo.accouting.mapper.crs.CrsAccountMapper;
import com.oyo.accouting.pojo.SyncLog;

import net.sf.json.JSONObject;

/**
 * @author ZhangSuYun
 * @date 2018-08-04 16:00
 */
@Service
public class SyncArAndApToSapService {
	private static Logger log = LoggerFactory.getLogger(SyncArAndApToSapService.class);

    @Autowired
    private CrsAccountMapper crsAccountMapper;

    @Autowired
    private AccountingSyncLogMapper accountingSyncLogMapper;

    public String syncArAndApToSap() {
    	log.info("----syncArAndApToSap start-------------");
    	String result = "";
    	//AR列表数据
    	List<HashMap<String,String>> arMapList = this.crsAccountMapper.calHotelAmount();//获取应收金额
    	//AP列表数据
    	List<HashMap<String,String>> apMapList = new ArrayList<HashMap<String,String>>();
    	List<HashMap<String,String>> ownerShareMapList = this.crsAccountMapper.getHotelOwnerShare();//获取ower share数据
    	if (null != arMapList && !arMapList.isEmpty()) {
    		JSONObject jsonData = new JSONObject();
    		for (Iterator<HashMap<String, String>> iterator = arMapList.iterator(); iterator.hasNext();) {
				HashMap<String, String> hashMap = (HashMap<String, String>) iterator.next();
		    	String hotelId = hashMap.get("HOTEL_ID");
		    	Integer arAmount = Integer.valueOf(hashMap.get("OWNER_AMOUNT"));
		    	BigDecimal apAmount = new BigDecimal("");
		    	
		    	if (null != ownerShareMapList && !ownerShareMapList.isEmpty()) {
		    		HashMap<String,String> ownerShareMap = ownerShareMapList.stream().filter(q->q.get("hotel_id").equals(hotelId)).collect(Collectors.toList()).get(0);
		    		String ownerShareJson = ownerShareMap.get("rs_slabs");
		    		if (StringUtils.isNotEmpty(ownerShareJson)) {
			    		String[] ownerShareArray = ownerShareJson.split(",");
			    		List<OwnerShare> list = new ArrayList<OwnerShare>();
			    		if (null != ownerShareArray && ownerShareArray.length > 0) {
			    			for (String eachOwnerShare : ownerShareArray) {
			    				String [] array = eachOwnerShare.split(":");
			    				if (null != array && array.length > 0) {
			    					OwnerShare ownerShare = null;
			    					ownerShare = new OwnerShare(Integer.valueOf(array[0]),Integer.valueOf(array[1]));
			    					list.add(ownerShare);
			    				}
							}
			    			
			    			if (null != list && !list.isEmpty()) {
				    			Collections.sort(list,new Comparator<OwnerShare>() {
				    	            @Override
				    	            public int compare(OwnerShare o1, OwnerShare o2) {
				    	                return o2.key - o1.key;
				    	            }
				    	        });
				    			
				    			HashMap<String,String> map = new HashMap<String,String>();
				    			map.put("HOTEL_ID", hotelId);
				    			
				    			Integer owerShare = getOwerShare(list,arAmount);
				    			BigDecimal owerShareBIg = new BigDecimal(owerShare);
				    			apAmount = owerShareBIg.multiply(new BigDecimal(arAmount));
				    			map.put("cpAmount", apAmount.toString());
				    			apMapList.add(map);
				    			
				    		}
			    			
			    		} else {
			    			JSONObject jsonObj = JSONObject.fromObject(ownerShareJson);
			    			Integer owerShare = Integer.valueOf(jsonObj.get("0").toString());
			    			apAmount = new BigDecimal(owerShare).multiply(new BigDecimal(arAmount));
			    			HashMap<String,String> map = new HashMap<String,String>();
			    			map.put("HOTEL_ID", hotelId);
			    			map.put("cpAmount", String.valueOf(apAmount));
			    			apMapList.add(map);
			    		}
		    		}
		    		
		    	}
		    	
		    	ZoneId zoneId = ZoneId.systemDefault();
		    	LocalDate now = LocalDate.now();
		    	LocalDate fifteenDayOfThisMonth = now.withDayOfMonth(15);
		        ZonedDateTime zdt = fifteenDayOfThisMonth.atStartOfDay(zoneId);
		        Date fifteenDayOfThisMonthDate = Date.from(zdt.toInstant());
		        
		        LocalDate lastDayOfPreviousMonth = now.minusMonths(1).with(TemporalAdjusters.lastDayOfMonth()); 
		        ZonedDateTime zdt2 = lastDayOfPreviousMonth.atStartOfDay(zoneId);
		        Date lastDayOfPreviousMonthDate = Date.from(zdt2.toInstant());
		        
		    	jsonData.put("CardCode", hotelId);//业务伙伴代码
		    	jsonData.put("CardName", this.crsAccountMapper.getHotelNameById(hotelId));//业务伙伴名称
		    	jsonData.put("DocDate", lastDayOfPreviousMonthDate);//过账日期,固定为每月最后一天
		    	jsonData.put("DocDueDate", fifteenDayOfThisMonthDate);//到期日,固定为下月15号
		    	jsonData.put("TaxDate", lastDayOfPreviousMonthDate);//单据日期,固定为每月最后一天
		    	jsonData.put("CurSource", "3");//币种,固定RMB
		    	jsonData.put("APTotal", apAmount);//AP含税总价
		    	jsonData.put("ARTotal", new BigDecimal(arAmount));//AR含税总价
		    	
		    	log.info("----to sap data as follows:-------------");
		    	log.info("to sap data:" + jsonData.toString());
		    	//调用SAP接口同步AR和AP信息
		    	//TODO
		    	
		    	SyncLog sLog = new SyncLog();
		    	sLog.setSourceId(Long.valueOf(hotelId));
		    	sLog.setCreateTime(new Timestamp(new Date().getTime()));
		    	sLog.setType("ArAndAp");
		    	
		    	//查询同步日志，判断是否需要同步
	            SyncLog syncLogSearch = new SyncLog();
	            syncLogSearch.setSourceId(Long.valueOf(hotelId));
	            syncLogSearch.setType("ArAndAp");
	            List<SyncLogDto> syncLogDtoList = this.accountingSyncLogMapper.querySyncList(syncLogSearch);
	            Integer versionNo = 1;
	            if (null != syncLogDtoList && !syncLogDtoList.isEmpty()) {
	            	versionNo = syncLogDtoList.stream().max(Comparator.comparing(SyncLogDto::getVersion)).get().getVersion() + 1;
	            }
		    	sLog.setVersion(versionNo);
		        sLog.setJsonData(jsonData.toString());
		        this.accountingSyncLogMapper.insert(sLog);
		    	
			}
    	}
    	log.info("----syncArAndApToSap end-------------");
        return result;
    }
    
    class OwnerShare {
    	private Integer key;
        private Integer value;
        public OwnerShare() {
        	
        }
        public OwnerShare(Integer key,Integer value) {
        	this.key = key;
        	this.value = value;
        }
    }
    
    //获取ower share
    private Integer getOwerShare(List<OwnerShare> list, Integer amount) {
    	Integer result = 0;
    	for (int i = 0; i < list.size(); i++) {
    		OwnerShare ownerShare = list.get(i);
			if (i != list.size() - 1) {
				if (amount > ownerShare.key) {
					continue;
				} else if (amount == ownerShare.key) {
					result = ownerShare.value;
				} else {
					result = list.get(i-1).value;
				}
			} else {
				if (amount >= ownerShare.key) {
					result = ownerShare.value;
				} else {
					result = list.get(i-1).value;
				}
			}
		}
    	return result;
    }
    
    public static void main(String[] args) {
    	LocalDate now = LocalDate.now();
    	LocalDate localDate8 = now.minusMonths(1).with(TemporalAdjusters.lastDayOfMonth()); 
    	System.out.println(localDate8);
	}
    
}
