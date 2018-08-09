package com.oyo.accouting.service;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
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
import com.oyo.accouting.util.WebserviceUtil;
import com.oyo.accouting.webservice.SAPWebServiceSoap;

import net.sf.json.JSONObject;

/**
 * sync ar and ap to sap interface.
 * @author ZhangSuYun
 * @date 2018-08-04 16:00
 */
@Service
public class SyncArAndApToSapService {
	private static Logger log = LoggerFactory.getLogger(SyncArAndApToSapService.class);

    @Autowired
    private CrsAccountMapper crsAccountMapper;
    
    @Autowired 
    private WebserviceUtil webserviceUtil;

    @Autowired
    private AccountingSyncLogMapper accountingSyncLogMapper;
    
    public String test() throws Exception {
    	JSONObject jsonData = new JSONObject();
    	jsonData.put("V_Code", "H-35279");//供应商代码
    	jsonData.put("C_Code", "CH-35279");//客户代码，传递时前面加C
    	jsonData.put("CardName", "test");//业务伙伴名称
        jsonData.put("DocDate", "2018-07-31");//过账日期,固定为每月最后一天
    	jsonData.put("DocDueDate", "2018-08-15");//到期日,固定为下月15号
    	jsonData.put("TaxDate", "2018-07-31");//单据日期,固定为每月最后一天
    	jsonData.put("CurSource", "3");//币种,固定RMB
    	jsonData.put("APTotal", 0.1);//AP含税总价
    	jsonData.put("ARTotal", 0.1);//AR含税总价
    	
    	log.info("----to sap data as follows:-------------");
    	log.info("to sap data:" + jsonData.toString());
    	
    	//调用SAP接口同步AR和AP信息
    	SAPWebServiceSoap serviceSap = webserviceUtil.getSapSapService();

        String syncSapResult = serviceSap.invoices(JSONObject.fromObject(jsonData).toString());
        
        log.info("Invoke sap result:" + syncSapResult);
    	return null;
    }

    public String syncArAndApToSap() throws Exception {
    	log.info("----syncArAndApToSap start-------------");
    	String result = "";
    	//AR列表数据
    	List<HashMap<String,String>> arMapList = null;//获取应收金额
    	//AP列表数据
    	List<HashMap<String,String>> apMapList = new ArrayList<HashMap<String,String>>();
    	List<HashMap<String,String>> ownerShareMapList = null;//获取ower share数据
    	Integer hotelIdMark = 0;//抛异常时，打印到log 控制台中。
    	try {
    		//AR列表数据
        	arMapList = this.crsAccountMapper.calHotelAmount();//获取应收金额
        	ownerShareMapList = this.crsAccountMapper.getHotelOwnerShare();//获取ower share数据
        	if (null != arMapList && !arMapList.isEmpty()) {
        		//获取sap接口
        		SAPWebServiceSoap service = webserviceUtil.getSapSapService();
        		if (null == service) {
		    		result += "Connection SAP Failed." + "\n";
		    		return result;
		    	}
        		JSONObject jsonData = new JSONObject();
        		for (Iterator<HashMap<String, String>> iterator = arMapList.iterator(); iterator.hasNext();) {
    				HashMap<String, String> hashMap = (HashMap<String, String>) iterator.next();
    				Integer hotelId = Integer.valueOf(String.valueOf(hashMap.get("hotel_id")));
    				hotelIdMark = hotelId;
    		    	BigDecimal arAmount = StringUtils.isNotEmpty(hashMap.get("OWNER_AMOUNT")) ? new BigDecimal(hashMap.get("OWNER_AMOUNT")) : new BigDecimal("0");
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
    				    	                return (int) (o2.key.compareTo(o1.key));
    				    	            }
    				    	        });
    				    			
    				    			HashMap<String,String> map = new HashMap<String,String>();
    				    			map.put("HOTEL_ID", hotelId.toString());
    				    			
    				    			BigDecimal owerShare = getOwerShare(list,arAmount);
    				    			apAmount = owerShare.multiply(arAmount);
    				    			map.put("cpAmount", apAmount.toString());
    				    			apMapList.add(map);
    				    			
    				    		}
    			    			
    			    		} else {
    			    			JSONObject jsonObj = JSONObject.fromObject(ownerShareJson);
    			    			Integer owerShare = Integer.valueOf(jsonObj.get("0").toString());
    			    			apAmount = new BigDecimal(owerShare).multiply(arAmount);
    			    			HashMap<String,String> map = new HashMap<String,String>();
    			    			map.put("HOTEL_ID", hotelId.toString());
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
    		        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-mm-dd");
    		        
    		    	jsonData.put("CardCode", "VH-" + hotelId);//业务伙伴代码
    		    	jsonData.put("CardName", this.crsAccountMapper.getHotelNameById(hotelId));//业务伙伴名称
    		    	jsonData.put("DocDate", sdf.format(lastDayOfPreviousMonthDate));//过账日期,固定为每月最后一天
    		    	jsonData.put("DocDueDate", sdf.format(fifteenDayOfThisMonthDate));//到期日,固定为下月15号
    		    	jsonData.put("TaxDate", sdf.format(lastDayOfPreviousMonthDate));//单据日期,固定为每月最后一天
    		    	jsonData.put("CurSource", "3");//币种,固定RMB
    		    	jsonData.put("APTotal", apAmount);//AP含税总价
    		    	jsonData.put("ARTotal", arAmount);//AR含税总价
    		    	
    		    	log.info("----to sap data as follows:-------------");
    		    	log.info("to sap data:" + jsonData.toString());
    		    	
    		    	//调用SAP接口同步AR和AP信息
                    String syncSapResult = service.invoices(JSONObject.fromObject(jsonData).toString());
                    
                    log.info("Invoke sap result:" + syncSapResult);
    		    	//插入同步日志
    		    	insertSyncLog(jsonData, hotelId, "Sync Ar And Ap To SAP");
    		    	
    			}
        	}
    	} catch (Exception e) {
    		result = "sync Ar anb Ap to SAP throw exception, hotelId is " + hotelIdMark + "\n";
    		throw e;
    	} 
    	log.info("----syncArAndApToSap end-------------");
        return result;
    }

    //插入同步日志
	private void insertSyncLog(JSONObject jsonData, Integer hotelId, String type) {
		log.info("----insertSyncLog start-------------");
		SyncLog sLog = new SyncLog();
		sLog.setSourceId(Integer.valueOf(hotelId));
		sLog.setCreateTime(new Timestamp(new Date().getTime()));
		sLog.setType(type);
		
		//查询同步日志，判断是否需要同步
		SyncLog syncLogSearch = new SyncLog();
		syncLogSearch.setSourceId(Integer.valueOf(hotelId));
		syncLogSearch.setType(type);
		List<SyncLogDto> syncLogDtoList = this.accountingSyncLogMapper.querySyncList(syncLogSearch);
		Integer versionNo = 1;
		if (null != syncLogDtoList && !syncLogDtoList.isEmpty()) {
			versionNo = syncLogDtoList.stream().max(Comparator.comparing(SyncLogDto::getVersion)).get().getVersion() + 1;
		}
		sLog.setVersion(versionNo);
		sLog.setJsonData(jsonData.toString());
		this.accountingSyncLogMapper.insert(sLog);
		log.info("----insertSyncLog end-------------");
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
    	for (int i = 0; i < list.size(); i++) {
    		OwnerShare ownerShare = list.get(i);
			if (i != list.size() - 1) {
				if (amount.compareTo(ownerShare.key) > 0) {
					continue;
				} else if (amount == ownerShare.key) {
					result = ownerShare.value;
				} else {
					result = list.get(i-1).value;
				}
			} else {
				if (amount.compareTo(ownerShare.key) >= 0) {
					result = ownerShare.value;
				} else {
					result = list.get(i-1).value;
				}
			}
		}
    	return result;
    }
    
}
