package com.oyo.accouting.service;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.oyo.accouting.mapper.accounting.AccountingSyncLogMapper;
import com.oyo.accouting.mapper.crs.CrsAccountMapper;

/**
 * sync Journal Entry to sap interface.
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

    public String syncJournalEntryToSap() throws Exception {
    	
    	log.info("----SyncJournalEntryToSap end-------------");
        return null;
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
    
}
