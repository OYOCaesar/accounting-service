package com.oyo.accouting.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.oyo.accouting.bean.SyncCrsArAndApDto;
import com.oyo.accouting.mapper.accounting.SyncCrsArAndApMapper;

/**
 * query munshi ar and ap interface.
 * @author ZhangSuYun
 * @date 2018-08-17 14:28
 */
@Service
public class QueryMunshiArAndApService {
	private static Logger log = LoggerFactory.getLogger(QueryMunshiArAndApService.class);

    @Autowired
    private SyncCrsArAndApMapper syncCrsArAndApMapper;
    
    	
	public List<SyncCrsArAndApDto> queryCrsArAndAp(Map<String,Object> map) throws Exception {
		List<SyncCrsArAndApDto> resultList = new ArrayList<>();
    	log.info("----queryMunshiArAndAp start-------------");
    	resultList = syncCrsArAndApMapper.selectByMapPage(map);
    	log.info("----queryMunshiArAndAp end-------------");
        return resultList;
    }
    
}
