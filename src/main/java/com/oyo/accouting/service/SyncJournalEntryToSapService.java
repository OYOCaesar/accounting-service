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
import org.apache.cxf.jaxws.JaxWsProxyFactoryBean;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.oyo.accouting.bean.SyncLogDto;
import com.oyo.accouting.constants.AccountingCode;
import com.oyo.accouting.mapper.accounting.AccountingSyncLogMapper;
import com.oyo.accouting.mapper.crs.CrsAccountMapper;
import com.oyo.accouting.pojo.SyncLog;
import com.oyo.accouting.util.YmlUtil;
import com.oyo.accouting.webservice.SAPWebServiceSoap;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

/**
 * 同步日记账分录给SAP接口
 * @author ZhangSuYun
 * @date 2018-08-04 16:00
 */
@Service
public class SyncJournalEntryToSapService {
	private static Logger log = LoggerFactory.getLogger(SyncJournalEntryToSapService.class);

    @Autowired
    private CrsAccountMapper crsAccountMapper;

    @Autowired
    private AccountingSyncLogMapper accountingSyncLogMapper;
    
    @Autowired
    private YmlUtil ymlUtil;
    
    //获取sap接口对象
  	public SAPWebServiceSoap getSapSapService() {
  		SAPWebServiceSoap service = null;
  		try {
  			JaxWsProxyFactoryBean jwpfb = new JaxWsProxyFactoryBean();
  	        jwpfb.setServiceClass(SAPWebServiceSoap.class);
  	        jwpfb.setAddress(ymlUtil.getSapWebServiceUrl());
  	        service = (SAPWebServiceSoap) jwpfb.create();
  		} catch (Exception e) {
  			log.error("Get sap interface throw Exception: {}", e);
  		}
        return service;
  	}
  	
  	public String test() throws Exception {
  		ZoneId zoneId = ZoneId.systemDefault();
    	LocalDate now = LocalDate.now();
    	LocalDate fifteenDayOfThisMonth = now.withDayOfMonth(15);
        ZonedDateTime zdt = fifteenDayOfThisMonth.atStartOfDay(zoneId);
        Date fifteenDayOfThisMonthDate = Date.from(zdt.toInstant());
        
        LocalDate lastDayOfPreviousMonth = now.minusMonths(1).with(TemporalAdjusters.lastDayOfMonth()); 
        ZonedDateTime zdt2 = lastDayOfPreviousMonth.atStartOfDay(zoneId);
        Date lastDayOfPreviousMonthDate = Date.from(zdt2.toInstant());
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        
    	JSONObject jsonData = new JSONObject();
    	jsonData.put("RefDate", sdf.format(lastDayOfPreviousMonthDate));//过账日期,固定为每月最后一天
        jsonData.put("DueDate", sdf.format(fifteenDayOfThisMonthDate));//到期日,固定为下月15号
        jsonData.put("TaxDate", sdf.format(lastDayOfPreviousMonthDate));//单据日期,固定为每月最后一天
        jsonData.put("Memo", "CH-35279");//备注
        
        JSONArray jsonArray = new JSONArray();
        JSONObject obj = new JSONObject();
        //obj.put("CardCode", "CH-35279");//业务伙伴代码
//    	jsonData.put("V_Code", "H-35279");//供应商代码
//    	jsonData.put("C_Code", "CH-35279");//客户代码，传递时前面加C
    	//-----------------------------------------------------
        //应收
        obj.put("Account", AccountingCode.CODE_60010201.getCode());//科目代码
        obj.put("Debit", 100);//借方金额
        obj.put("Credit", 0);//贷方金额
        jsonArray.add(obj);
    	
        //应付
        obj.put("Account", AccountingCode.CODE_60010401.getCode());//科目代码
        obj.put("Debit", 0);//借方金额
        obj.put("Credit", 90);//贷方金额
        jsonArray.add(obj);
        
        obj.put("Account", AccountingCode.CODE_60010601.getCode());//科目代码
        obj.put("Debit", 0);//借方金额
        obj.put("Credit", 10);//贷方金额
        jsonArray.add(obj);
        
        jsonData.put("Lines", jsonArray);
    	log.info("----Journal Entry Ap to SAP data as follows:-------------");
    	log.info("to sap data:" + jsonData.toString());
    	
    	//调用SAP接口同步AR和AP信息
    	SAPWebServiceSoap sapService = this.getSapSapService();
    	//调用SAP接口同步日记账分表应付信息
        String syncSapApResult = sapService.journalEntries(JSONObject.fromObject(jsonData).toString());
        log.info("Invoke Ap to SAP result:" + syncSapApResult);
        
    	return null;
    }

    public String syncJournalEntryToSap() throws Exception {
    	log.info("----SyncJournalEntryToSap start-------------");
    	String result = "";
    	Integer totalCountAr = 0;//ar同步总记录数
    	Integer successCountAr = 0;//ar同步成功记录数
    	Integer failedCountAr = 0;//ar同步失败记录数
    	
    	Integer totalCountAp = 0;//ap同步总记录数
    	Integer successCountAp = 0;//ap同步成功记录数
    	Integer failedCountAp = 0;//ap同步失败记录数
    	
    	//AR列表数据
    	List<HashMap<String,String>> arMapList = null;//获取应收金额
    	List<HashMap<String,String>> ownerShareMapList = null;//获取ower share数据
    	Integer hotelIdMark = 0;//抛异常时，打印到log 控制台中。
    	try {
    		//AR列表数据
        	arMapList = this.crsAccountMapper.calHotelAmount(null);//获取应收金额
        	totalCountAr = arMapList.size();
        	totalCountAp = arMapList.size();
        	ownerShareMapList = this.crsAccountMapper.getHotelOwnerShare(null);//获取ower share数据
        	if (null != arMapList && !arMapList.isEmpty()) {
        		//获取sap接口
        		SAPWebServiceSoap sapService = this.getSapSapService();
        		if (null == sapService) {
		    		result += "Connection SAP Failed." + "\n";
		    		return result;
		    	}
        		
        		JSONObject jsonData = null;
        		for (Iterator<HashMap<String, String>> iterator = arMapList.iterator(); iterator.hasNext();) {
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
    				    			
    				    			HashMap<String,String> map = new HashMap<String,String>();
    				    			map.put("HOTEL_ID", hotelId.toString());
    				    			
    				    			BigDecimal owerShare = getOwerShare(list,arAmount);
    				    			apAmount = owerShare.multiply(arAmount);
    				    		}
    			    			
    			    		} else {
    			    			JSONObject jsonObj = JSONObject.fromObject(ownerShareJson);
    			    			Integer owerShare = Integer.valueOf(jsonObj.get("0").toString());
    			    			apAmount = new BigDecimal(owerShare).multiply(arAmount);
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
    		        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
    		        
    		        jsonData = new JSONObject();
    		        jsonData.put("RefDate", sdf.format(lastDayOfPreviousMonthDate));//过账日期,固定为每月最后一天
    		        jsonData.put("DueDate", sdf.format(fifteenDayOfThisMonthDate));//到期日,固定为下月15号
    		        jsonData.put("TaxDate", sdf.format(lastDayOfPreviousMonthDate));//单据日期,固定为每月最后一天
    		        jsonData.put("Memo", "CH-" + hotelId);//备注
    		        
    		        JSONArray jsonArray = new JSONArray();
    		        JSONObject obj = new JSONObject();
    		        //应收Sales Memo
    		        obj.put("Account", AccountingCode.CODE_60010201.getCode());//科目代码
    		        obj.put("Debit", arAmount);//借方金额
    		        obj.put("Credit", 0);//贷方金额
    		        jsonArray.add(obj);
    		    	
    		        //应付 Cost Memo
    		        obj.put("Account", AccountingCode.CODE_60010401.getCode());//科目代码
    		        obj.put("Debit", 0);//借方金额
    		        obj.put("Credit", apAmount);//贷方金额
    		        jsonArray.add(obj);
    		        
    		        //应付 OYO Share
    		        obj.put("Account", AccountingCode.CODE_60010601.getCode());//科目代码
    		        obj.put("Debit", 0);//借方金额
    		        BigDecimal oyoAmount = new BigDecimal("0");
    		        oyoAmount = arAmount.subtract(apAmount);
    		        obj.put("Credit", oyoAmount);//贷方金额
    		        jsonArray.add(obj);
    		        
    		        jsonData.put("Lines", jsonArray);
    		    	
    		    	//调用SAP接口同步日记账分表应付信息
                    String syncSapApResult = sapService.journalEntries(JSONObject.fromObject(jsonData).toString());
                    log.info("Invoke sap interface for <Journal Entry Ap> result::" + syncSapApResult);
                    JSONObject jsonResult = JSONObject.fromObject(syncSapApResult);
                    String code = jsonResult.getString("Code");
                    String message = jsonResult.getString("Message");
                    jsonData.put("Code", code);
                    jsonData.put("Message", message);
                    
                    if ("0".equals(code)) {
                    	successCountAp ++;
                    } else {
                    	failedCountAp ++;
                    }
                    
                    //插入日记账应收同步日志
    		    	insertSyncLog(Integer.valueOf(code), jsonData, hotelId, "Sync Journal Entry Ap To SAP");
    		    	
    			}
        	}
    	} catch (Exception e) {
    		result = "sync Journal Entry to SAP throw exception, hotelId is:" + hotelIdMark + "\n";
    		throw e;
    	}
    	log.info("----SyncJournalEntryToSap end---------------");
    	result += "Sync result:totalCountAr=" + totalCountAr + ",successCountAr=" + successCountAr + ",failedCountAr" + failedCountAr + "\n" +
    			  "totalCountAp=" + totalCountAp + ",successCountAp=" + successCountAp + ",failedCountAp" + failedCountAp + "\n";
        return result;
    }
    
    //插入同步日志
    private void insertSyncLog(Integer status, JSONObject jsonData, Integer hotelId, String type) {
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
