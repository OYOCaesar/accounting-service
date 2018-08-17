package com.oyo.accouting.service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.oyo.accouting.bean.QueryCrsAccountingDto;
import com.oyo.accouting.mapper.crs.CrsAccountMapper;
import net.sf.json.JSONObject;

/**
 * query crs ar and ap interface.
 * @author ZhangSuYun
 * @date 2018-08-11 11:28
 */
@Service
public class QueryCrsArAndApService {
	private static Logger log = LoggerFactory.getLogger(QueryCrsArAndApService.class);

    @Autowired
    private CrsAccountMapper crsAccountMapper;
    
    public List<QueryCrsAccountingDto> queryCrsArAndAp(QueryCrsAccountingDto queryCrsAccountingDto) throws Exception {
    	List<QueryCrsAccountingDto> resultList = new ArrayList<>();
    	log.info("----queryCrsArAndAp start-------------");
    	resultList = crsAccountMapper.queryCrsArApInfoByCondition(queryCrsAccountingDto);
    	if (null != resultList && !resultList.isEmpty()) {
    		
    		resultList.forEach(q->{
    			Map<String,Object> map = new HashMap<String,Object>();
    			map.put("hotelId", q.getHotelId());
    			map.put("checkInDateStart", queryCrsAccountingDto.getCheckInDateStart());
    			map.put("checkInDateEnd", queryCrsAccountingDto.getCheckInDateEnd());
    			List<HashMap<String, String>> owerShareMapList = crsAccountMapper.getHotelOwnerShareByHotelIdAndChekInDate(map);
    			
    			BigDecimal arAmount = q.getArAmount();
    			BigDecimal apAmount = new BigDecimal("0");
    			
    			if (null != owerShareMapList && !owerShareMapList.isEmpty()) {
    				owerShareMapList = owerShareMapList.stream().distinct().collect(Collectors.toList());
		    		String ownerShareJson = String.valueOf(owerShareMapList.get(0).get("rs_slabs"));
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
				    			q.setRate(owerShare.intValue());
				    			apAmount = owerShare.multiply(arAmount).divide(new BigDecimal("100"),6,BigDecimal.ROUND_HALF_UP);
				    		}
			    			
			    		} else {
			    			JSONObject jsonObj = JSONObject.fromObject(ownerShareJson);
			    			Integer owerShare = Integer.valueOf(jsonObj.get("0").toString());
			    			q.setRate(owerShare.intValue());//汇率
			    			apAmount = new BigDecimal(owerShare).multiply(arAmount).divide(new BigDecimal("100"),6,BigDecimal.ROUND_HALF_UP);
			    		}
			    		q.setApAmount(apAmount);
		    		}
		    		
		    	} else {
		    		q.setApAmount(apAmount);
		    		q.setRate(0);
		    	}
    			
    		});
    	}
    	log.info("----queryCrsArAndAp end-------------");
        return resultList;
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
