package com.oyo.accouting.service;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.ZonedDateTime;
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

import com.oyo.accouting.mapper.accounting.SyncCrsArAndApMapper;
import com.oyo.accouting.mapper.crs.CrsAccountMapper;
import com.oyo.accouting.pojo.SyncCrsArAndAp;

import net.sf.json.JSONObject;

/**
 * sync crs ar and ap interface.
 * @author ZhangSuYun
 * @date 2018-08-10 11:28
 */
@Service
public class SyncCrsArAndApService {
	private static Logger log = LoggerFactory.getLogger(SyncCrsArAndApService.class);

    @Autowired
    private CrsAccountMapper crsAccountMapper;
    
    @Autowired
    private SyncCrsArAndApMapper syncCrsArAndApMapper;
    
    public String syncCrsArAndAp() throws Exception {
    	log.info("----syncCrsArAndAp start-------------");
    	String result = "";
    	Integer totalCount = 0;//同步总记录数
    	
    	//AR列表数据
    	List<HashMap<String,String>> arMapList = null;//获取应收金额
    	List<HashMap<String,String>> ownerShareMapList = null;//获取ower share数据
    	Integer hotelIdMark = 0;//抛异常时，打印到log 控制台中。
    	List<SyncCrsArAndAp> crsArAndApList = new ArrayList<SyncCrsArAndAp>();//来自crs的ar And ap数据
    	try {
    		ZoneId zoneId = ZoneId.systemDefault();
    		LocalDate now = LocalDate.now();
	        LocalDate lastMonthDateLocal = now.minusMonths(1);
	        ZonedDateTime zdt = lastMonthDateLocal.atStartOfDay(zoneId);
	        Date lastMonthDate = Date.from(zdt.toInstant());
	        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM");
	        String yearMonth = sdf.format(lastMonthDate);
	        //先删除掉指定年月的Ar And Ap数据
    		syncCrsArAndApMapper.updateYearMonthCrsArAndApIsDelBatch(yearMonth);
    		
    		//AR列表数据
        	arMapList = this.crsAccountMapper.calHotelAmount();//获取应收金额
        	totalCount = arMapList.size();
        	ownerShareMapList = this.crsAccountMapper.getHotelOwnerShare();//获取ower share数据
        	if (null != arMapList && !arMapList.isEmpty()) {
        		SyncCrsArAndAp syncCrsArAndAp = null;
        		for (Iterator<HashMap<String, String>> iterator = arMapList.iterator(); iterator.hasNext();) {
        			syncCrsArAndAp = new SyncCrsArAndAp();
    				HashMap<String, String> hashMap = (HashMap<String, String>) iterator.next();
    				Integer hotelId = Integer.valueOf(String.valueOf(hashMap.get("hotel_id")));
    				hotelIdMark = hotelId;
    		    	BigDecimal arAmount = new BigDecimal(String.valueOf(hashMap.get("sum")));
    		    	BigDecimal apAmount = new BigDecimal("0");
    		    	
    		    	if (null != ownerShareMapList && !ownerShareMapList.isEmpty()) {
    		    		HashMap<String,String> ownerShareMap = ownerShareMapList.stream().filter(q->Integer.valueOf(String.valueOf(q.get("hotel_id"))).equals(hotelId)).collect(Collectors.toList()).get(0);
    		    		String ownerShareJson = String.valueOf(ownerShareMap.get("rs_slabs"));
    		    		if (StringUtils.isNotEmpty(ownerShareJson)) {
    			    		String[] ownerShareArray = ownerShareJson.split(",");
    			    		List<OwnerShare> list = new ArrayList<OwnerShare>();
    			    		if (null != ownerShareArray && ownerShareArray.length > 0) {
    			    			for (String eachOwnerShare : ownerShareArray) {
    			    				eachOwnerShare = eachOwnerShare.replace("{", "");
    			    				eachOwnerShare = eachOwnerShare.replace("}", "");
    			    				eachOwnerShare = eachOwnerShare.replace("\"", "");
    			    				String [] array = eachOwnerShare.split(":");
    			    				if (null != array && array.length > 0) {
    			    					OwnerShare ownerShare = null; 
    			    					ownerShare = new OwnerShare(new BigDecimal(array[0]),new BigDecimal(array[1]));
    			    					list.add(ownerShare);
    			    				}
    							}
    			    			
    			    			if (null != list && !list.isEmpty()) {
    				    			Collections.sort(list,new Comparator<OwnerShare>() {
    				    	            @Override
    				    	            public int compare(OwnerShare o1, OwnerShare o2) {
    				    	                return (int) (o1.key.compareTo(o2.key));
    				    	            }
    				    	        });
    				    			
    				    			BigDecimal owerShare = getOwerShare(list,arAmount);
    				    			syncCrsArAndAp.setRate(owerShare.intValue());//汇率
    				    			apAmount = owerShare.multiply(arAmount).divide(new BigDecimal("100"),6,BigDecimal.ROUND_HALF_UP);
    				    		}
    			    			
    			    		} else {
    			    			JSONObject jsonObj = JSONObject.fromObject(ownerShareJson);
    			    			Integer owerShare = Integer.valueOf(jsonObj.get("0").toString());
    			    			syncCrsArAndAp.setRate(owerShare.intValue());//汇率
    			    			apAmount = new BigDecimal(owerShare).multiply(arAmount).divide(new BigDecimal("100"),6,BigDecimal.ROUND_HALF_UP);
    			    		}
    		    		}
    		    		
    		    	}
    		        
    		        syncCrsArAndAp.setSyncYearMonth(yearMonth);
    		        syncCrsArAndAp.setHotelId(hotelId);
    		        syncCrsArAndAp.setHotelName(hashMap.get("hotel_name"));
    		        syncCrsArAndAp.setArAmount(arAmount);
    		        syncCrsArAndAp.setApAmount(apAmount);
    		        syncCrsArAndAp.setIsSync(Boolean.FALSE);
    		        syncCrsArAndAp.setIsDel(Boolean.FALSE);
    		        syncCrsArAndAp.setCreateTime(new Timestamp(System.currentTimeMillis()));
    		        crsArAndApList.add(syncCrsArAndAp);
    		    	
    			}
        	}
        	
        	if (null != crsArAndApList && !crsArAndApList.isEmpty()) {
        		//每1000条批量插入一次
        		int len = (crsArAndApList.size() % 1000 == 0 ? crsArAndApList.size() / 1000 : ((crsArAndApList.size() / 1000) + 1));
        		for (int i = 0; i < len; i++) {
        			int startIndex = 0;
        			int endIndex = 0;
        			if (len <= 1) {
        				endIndex = crsArAndApList.size();
        			} else {
        				startIndex = i * 1000;
        				if (i == len - 1) {
            				endIndex = crsArAndApList.size();
            			} else {
            				endIndex = (i + 1) * 1000;
            			}
        			}
        			//批量插入来自crs 的ar and ap数据
            		syncCrsArAndApMapper.insertCrsArAndApList(crsArAndApList.subList(startIndex, endIndex));
        		}
        		
        	}
        	
    	} catch (Exception e) {
    		result = "sync CRS ar and ap throw exception, hotelId is:" + hotelIdMark;
    		throw e;
    	} 
    	log.info("----syncCrsArAndAp end-------------");
    	result += "Sync result:totalCount=" + totalCount;
        return result;
    }
    
    class OwnerShare {
    	private BigDecimal key;
        private BigDecimal value;
        public OwnerShare() {
        	
        }
        public OwnerShare(BigDecimal key,BigDecimal value) {
        	this.key = key;
        	this.value = value;
        }
    }
    
    //获取ower share
    private BigDecimal getOwerShare(List<OwnerShare> list, BigDecimal amount) {
    	BigDecimal result = new BigDecimal("0");
    	if (amount.compareTo(BigDecimal.ZERO) <= 0) {
    		return result;
    	}
    	for (int i = 0; i < list.size(); i++) {
    		OwnerShare ownerShare = list.get(i);
			if (i != list.size() - 1) {
				if (amount.compareTo(ownerShare.key) > 0) {
					continue;
				} else if (amount.compareTo(ownerShare.key) == 0) {
					result = ownerShare.value;
					break;
				} else {
					result = list.get(i-1).value;
					break;
				}
			} else {
				if (amount.compareTo(ownerShare.key) >= 0) {
					result = ownerShare.value;
					break;
				} else {
					result = list.get(i-1).value;
					break;
				}
			}
		}
    	return result;
    }
    
}
