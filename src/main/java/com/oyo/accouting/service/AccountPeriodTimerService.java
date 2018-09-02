package com.oyo.accouting.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.oyo.accouting.mapper.accounting.AccountPeriodTimerMapper;
import com.oyo.accouting.pojo.AccountPeriodTimer;

//账期recon和导出监控servcie
@Service
public class AccountPeriodTimerService {

    @Autowired
    private AccountPeriodTimerMapper accountPeriodTimerMapper;

    public int deleteByPrimaryKey(Integer id) {
    	return accountPeriodTimerMapper.deleteByPrimaryKey(id);
    }

    public int insert(AccountPeriodTimer record) {
    	return accountPeriodTimerMapper.insert(record);
    }

    public int insertSelective(AccountPeriodTimer record) {
    	return accountPeriodTimerMapper.insertSelective(record);
    }

    public AccountPeriodTimer selectByPrimaryKey(Integer id) {
    	return accountPeriodTimerMapper.selectByPrimaryKey(id);
    }
    
    //条件查询
    public List<AccountPeriodTimer> selectByAccountPeriodTimer(AccountPeriodTimer accountPeriodTimer) {
    	return accountPeriodTimerMapper.selectByAccountPeriodTimer(accountPeriodTimer);
    }
    
    //根据功能名称列表查询
    public List<AccountPeriodTimer> selectByFunctionNameList(List<String> list) {
    	return accountPeriodTimerMapper.selectByFunctionNameList(list);
    }
    
    public int updateByPrimaryKeySelective(AccountPeriodTimer record) {
    	return accountPeriodTimerMapper.updateByPrimaryKeySelective(record);
    }

    public int updateByPrimaryKey(AccountPeriodTimer record) {
    	return accountPeriodTimerMapper.updateByPrimaryKey(record);
    }
    
}
