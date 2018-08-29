package com.oyo.accouting.service;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.temporal.TemporalAdjusters;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.apache.cxf.jaxws.JaxWsProxyFactoryBean;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.alibaba.druid.util.StringUtils;
import com.oyo.accouting.bean.OyoShareDto;
import com.oyo.accouting.bean.SyncCrsArAndApDto;
import com.oyo.accouting.bean.SyncLogDto;
import com.oyo.accouting.constants.AccountingCode;
import com.oyo.accouting.mapper.accounting.AccountingOyoShareMapper;
import com.oyo.accouting.mapper.accounting.AccountingSyncLogMapper;
import com.oyo.accouting.mapper.accounting.SyncCrsArAndApMapper;
import com.oyo.accouting.pojo.SyncCrsArAndAp;
import com.oyo.accouting.pojo.SyncLog;
import com.oyo.accouting.util.YmlUtil;
import com.oyo.accouting.webservice.SAPWebServiceSoap;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

/**
 * sync ar and ap to sap interface.
 * @author ZhangSuYun
 * @date 2018-08-04 16:00
 */
@Service
public class SyncArAndApAndJournalEntryToSapService {
	private static Logger log = LoggerFactory.getLogger(SyncArAndApAndJournalEntryToSapService.class);
	
    @Autowired
    private SyncCrsArAndApMapper syncCrsArAndApMapper;
    
    @Autowired
    private YmlUtil ymlUtil;

    @Autowired
    private AccountingSyncLogMapper accountingSyncLogMapper;
    
    @Autowired
    private AccountingOyoShareMapper accountingOyoShareMapper;
    
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
    
    /***
     * Sync Ar and Ap and Journal Entry to SAP from Accounting
     * @return
     * @throws Exception
     */
    public String syncArAndApAndJournalEntryToSap(String yearMonth, Integer syncHotelId) throws Exception {
    	log.info("----sync Ar anb Ap and Journal Entry to SAP start-------------");
    	String result = "";
    	Integer totalCount = 0;//同步总记录数
    	Integer successCount = 0;//同步成功记录数
    	Integer failedCount = 0;//同步失败记录数
    	
    	Integer totalCountJournalEntries = 0;//日记账分录同步总记录数
    	Integer successCountJournalEntries = 0;//日记账分录同步成功记录数
    	Integer failedCountJournalEntries = 0;//日记账分录同步失败记录数
    	String batch = String.valueOf(new Date().getTime());
    	
    	List<SyncCrsArAndApDto> crsArAndApList = null;
    	List<SyncCrsArAndAp> crsArAndApSuccessList = new ArrayList<SyncCrsArAndAp>();//成功同步到SAP的AR AND AP列表
    	Integer hotelIdMark = 0;//抛异常时，打印到log 控制台中。
    	try {
	        ZoneId zoneId = ZoneId.systemDefault();
    		LocalDate now = LocalDate.now();
	        LocalDate lastMonthDateLocal = now.minusMonths(1);
	        ZonedDateTime zdt = lastMonthDateLocal.atStartOfDay(zoneId);
	        Date lastMonthDate = Date.from(zdt.toInstant());
	        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM");
	        if (StringUtils.isEmpty(yearMonth)) {//如果未传同步年月，那么就取当前日期的上月为同步年月
	        	yearMonth = sdf.format(lastMonthDate);// 同步的年月
	        }
	        
	    	LocalDate fifteenDayOfThisMonth = now.withDayOfMonth(15);
	        ZonedDateTime zdt1 = fifteenDayOfThisMonth.atStartOfDay(zoneId);
	        Date fifteenDayOfThisMonthDate = Date.from(zdt1.toInstant());//下月15日
	        
	        LocalDate lastDayOfPreviousMonth = now.minusMonths(1).with(TemporalAdjusters.lastDayOfMonth()); 
	        ZonedDateTime zdt2 = lastDayOfPreviousMonth.atStartOfDay(zoneId);
	        Date lastDayOfPreviousMonthDate = Date.from(zdt2.toInstant());//同步年月的最后一天
	        SimpleDateFormat sdf2 = new SimpleDateFormat("yyyy-MM-dd");
	        
    		Map<String,Object> map = new HashMap<String,Object>();
    		map.put("syncYearMonth", yearMonth);
    		//获取AR and AP列表数据
    		if (null != syncHotelId) {
    			map.put("hotelId", syncHotelId);
    		}
    		crsArAndApList = this.syncCrsArAndApMapper.selectByMap(map);
        	
        	if (null != crsArAndApList && !crsArAndApList.isEmpty()) {
        		
        		OyoShareDto info = new OyoShareDto();
    			info.setIsTest("t");
    			//过滤掉黑名单或测试酒店
    			List<OyoShareDto> hotelExceptList = this.accountingOyoShareMapper.queryOyoShareList(info);
    			if (null != hotelExceptList && !hotelExceptList.isEmpty()) {
    				List<String> hotelIdHeiList = hotelExceptList.stream().map(OyoShareDto::getHotelId).collect(Collectors.toList());
    				crsArAndApList = crsArAndApList.stream().filter(q->!hotelIdHeiList.contains(q.getHotelId().toString())).collect(Collectors.toList());
    			}
    			
    			totalCount = crsArAndApList.size();
            	totalCountJournalEntries = crsArAndApList.size();
            	
        		//获取sap接口
        		SAPWebServiceSoap sapService = this.getSapSapService();
        		if (null == sapService) {
		    		result += "Connection SAP Failed." + "\n";
		    		return result;
		    	}
        		JSONObject jsonData = null;
        		for (SyncCrsArAndApDto syncCrsArAndAp : crsArAndApList) {
    		        
        			Integer hotelId = syncCrsArAndAp.getHotelId();
        			hotelIdMark = hotelId;
        			
    		        jsonData = new JSONObject();
    		        jsonData.put("V_Code", "H-" + hotelId);//供应商代码
    		    	jsonData.put("C_Code", "CH-" + hotelId);//客户代码，传递时前面加C
    		    	jsonData.put("CardName", syncCrsArAndAp.getHotelName());//业务伙伴名称
    		    	jsonData.put("DocDate", sdf2.format(lastDayOfPreviousMonthDate));//过账日期,固定为每月最后一天
    		    	jsonData.put("DocDueDate", sdf2.format(fifteenDayOfThisMonthDate));//到期日,固定为下月15号
    		    	jsonData.put("TaxDate", sdf2.format(lastDayOfPreviousMonthDate));//单据日期,固定为每月最后一天
    		    	jsonData.put("CurSource", "3");//币种,固定为RMB
    		    	jsonData.put("ARTotal", syncCrsArAndAp.getArAmount());//AR含税总价
    		    	jsonData.put("APTotal", syncCrsArAndAp.getApAmount());//AP含税总价
    		    	
    		    	log.info("----to sap data as follows:-------------");
    		    	log.info("to sap data:" + jsonData.toString());
    		    	
    		    	//调用SAP接口同步AR和AP信息
                    String syncSapResult = sapService.invoices(JSONObject.fromObject(jsonData).toString());
                    log.info("Invoke sap result:" + syncSapResult);
                    JSONObject json = JSONObject.fromObject(syncSapResult);
                    String code = json.getString("Code");
                    String message = json.getString("Message");
                    jsonData.put("Code", code);
                    jsonData.put("Message", message);
                    
                    if ("0".equals(code)) {
                    	successCount ++;
                    	jsonData = new JSONObject();
        		        jsonData.put("RefDate", sdf2.format(lastDayOfPreviousMonthDate));//过账日期,固定为每月最后一天
        		        jsonData.put("DueDate", sdf2.format(fifteenDayOfThisMonthDate));//到期日,固定为下月15号
        		        jsonData.put("TaxDate", sdf2.format(lastDayOfPreviousMonthDate));//单据日期,固定为每月最后一天
        		        jsonData.put("Memo", "CH-" + hotelId);//备注
        		        
        		        JSONArray jsonArray = new JSONArray();
        		        JSONObject obj = new JSONObject();
        		        //应收Sales Memo
        		        //obj.put("CardCode", "CH-" + hotelId);//客户代码，传递时前面加C
        		        obj.put("CardCode", "");//客户代码，传递时前面加C
        		        obj.put("Account", AccountingCode.CODE_60010201.getCode());//科目代码
        		        obj.put("Debit", syncCrsArAndAp.getArAmount());//借方金额
        		        obj.put("Credit", 0);//贷方金额
        		        obj.put("LineMemo", "CH-" + hotelId);//行备注
        		        jsonArray.add(obj);
        		    	
        		        //应付 Cost Memo
        		        //obj.put("CardCode", "H-" + hotelId);//客户代码，传递时前面加C
        		        obj.put("CardCode", "");//客户代码，传递时前面加C
        		        obj.put("Account", AccountingCode.CODE_60010401.getCode());//科目代码
        		        obj.put("Debit", 0);//借方金额
        		        obj.put("Credit", syncCrsArAndAp.getApAmount());//贷方金额
        		        obj.put("LineMemo", "CH-" + hotelId);//行备注
        		        jsonArray.add(obj);
        		        
        		        //应付 OYO Share
        		        obj.put("CardCode", "");//客户代码，传递时前面加C
        		        obj.put("Account", AccountingCode.CODE_60010601.getCode());//科目代码
        		        BigDecimal oyoAmount = new BigDecimal("0");
        		        oyoAmount = syncCrsArAndAp.getArAmount().subtract(syncCrsArAndAp.getApAmount());
        		        obj.put("Debit", 0);//借方金额
        		        obj.put("Credit", oyoAmount);//贷方金额
        		        obj.put("LineMemo", "CH-" + hotelId);//行备注
        		        jsonArray.add(obj);
        		        
        		        jsonData.put("Lines", jsonArray);
        		    	
        		        log.info("Invoke sap interface for <Journal Entry> json String:" + jsonData);
        		        
        		    	//调用SAP接口同步日记账分表应付信息
                        String syncSapApResult = sapService.journalEntries(JSONObject.fromObject(jsonData).toString());
                        log.info("Invoke sap interface for <Journal Entry> result:" + syncSapApResult);
                        JSONObject journalEntriesJsonResult = JSONObject.fromObject(syncSapApResult);
                        String journalEntriesCode = journalEntriesJsonResult.getString("Code");
                        String journalEntriesMessage = journalEntriesJsonResult.getString("Message");
                        jsonData.put("Code", journalEntriesCode);
                        jsonData.put("Message", journalEntriesMessage);
        		    	
                        if ("0".equals(journalEntriesCode)) {
                        	successCountJournalEntries ++;
                        } else {
                        	failedCountJournalEntries ++;
                        }
                        
                        //插入同步日志
        		    	insertSyncLog(Integer.valueOf(journalEntriesCode), journalEntriesMessage, batch, jsonData, hotelId, "Sync JE To SAP");
        		    	
        		    	//若AR AP 及 JournalEntry均成功，那么将表中sync_crs_ar_ap该条记录更新为已同步
        		    	if ("0".equals(journalEntriesCode)) {
        		    		//成功将其放入成功列表中，后面批量更新Accounting中的同步状态为已同步，下次该hotelId不再同步
        		    		SyncCrsArAndAp syncCrsArAndApSuccess = new SyncCrsArAndAp();
        		    		BeanUtils.copyProperties(syncCrsArAndAp, syncCrsArAndApSuccess);
        		    		syncCrsArAndApSuccess.setIsSync(Boolean.TRUE);//更新为已同步
        		    		if (hotelId.intValue() != 20240) {
        		    			crsArAndApSuccessList.add(syncCrsArAndApSuccess);
        		    		}
        		    	}
                    } else {
                    	failedCount ++;
                    }
                    
    		    	//插入同步日志
    		    	insertSyncLog(Integer.valueOf(code), message, batch, jsonData, hotelId, "Sync Ar And Ap To SAP");
    		    	
				}
        	}
        	
        	//批量更新Accounting中表sync_crs_ar_ap的同步状态为已同步
        	if (null != crsArAndApSuccessList && !crsArAndApSuccessList.isEmpty()) {
        		//每1000条批量插入一次
        		int len = (crsArAndApSuccessList.size() % 1000 == 0 ? crsArAndApSuccessList.size() / 1000 : ((crsArAndApSuccessList.size() / 1000) + 1));
        		for (int i = 0; i < len; i++) {
        			int startIndex = 0;
        			int endIndex = 0;
        			if (len <= 1) {
        				endIndex = crsArAndApSuccessList.size();
        			} else {
        				startIndex = i * 1000;
        				if (i == len - 1) {
            				endIndex = crsArAndApSuccessList.size();
            			} else {
            				endIndex = (i + 1) * 1000;
            			}
        			}
        			//批量更新Accounting中表sync_crs_ar_ap的同步状态
            		syncCrsArAndApMapper.updateCrsArAndApIsSyncList(crsArAndApSuccessList.subList(startIndex, endIndex));
        		}
        		
        	}
        	
    	} catch (Exception e) {
    		result = "sync Ar anb Ap and Journal Entry to SAP throw exception, hotelId is:" + hotelIdMark + "\r\n";
    		throw e;
    	} 
    	log.info("----sync Ar anb Ap and Journal Entry to SAP end-------------");
    	result += "Sync total Ar And Ap to SAP result : totalCount = " + totalCount + ",successCount = " + successCount + ",failedCount = " + failedCount + "<br/>" + 
    	          "Sync Journal Entry to SAP result : totalCountJournalEntries = " + totalCountJournalEntries + ",successCountJournalEntries = " + successCountJournalEntries + ",failedCountJournalEntries = " + failedCountJournalEntries;
        return result;
    }

    //插入同步日志
	private void insertSyncLog(Integer status, String message, String batch, JSONObject jsonData, Integer hotelId, String type) {
		log.info("----insertSyncLog start-------------");
		SyncLog sLog = new SyncLog();
		sLog.setSourceId(Integer.valueOf(hotelId));
		sLog.setCreateTime(new Timestamp(System.currentTimeMillis()));
		sLog.setType(type);
		sLog.setStatus(status);//状态码
		sLog.setMessage(message);//错误描述
		sLog.setBatch(batch);//批次
		
		//查询同步日志，判断是否需要同步
		SyncLogDto syncLogSearch = new SyncLogDto();
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
    
    public static void main(String[] args) {
    	SyncArAndApAndJournalEntryToSapService aa = new SyncArAndApAndJournalEntryToSapService();
		List<OwnerShare> list = new ArrayList<OwnerShare>();
		list.add(aa.new OwnerShare(new BigDecimal("0"),new BigDecimal("70")));
		list.add(aa.new OwnerShare(new BigDecimal("2000"),new BigDecimal("80")));
		list.add(aa.new OwnerShare(new BigDecimal("3000"),new BigDecimal("85")));
		BigDecimal zz = aa.getOwerShare(list, new BigDecimal("-1"));
		System.out.println(zz);
	}
    
}
