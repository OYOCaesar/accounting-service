package com.oyo.accouting.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.oyo.accouting.mapper.accounting.DeductionsMapper;
import com.oyo.accouting.pojo.Deductions;

/**
 * 扣除费用接口.
 * @author ZhangSuYun
 * @date 2018-08-27 20:11
 */
@Service
public class DeductionsService {

    @Autowired
    private DeductionsMapper deductionsMapper;
    
    public int deleteByPrimaryKey(Integer id) {
    	return deductionsMapper.deleteByPrimaryKey(id);
    }

    public int insert(Deductions record) {
    	return deductionsMapper.insert(record);
    }

    public int insertSelective(Deductions record) {
    	return deductionsMapper.insertSelective(record);
    }

    public Deductions selectByPrimaryKey(Integer id) {
    	return deductionsMapper.selectByPrimaryKey(id);
    }

    public int updateByPrimaryKeySelective(Deductions record) {
    	return deductionsMapper.updateByPrimaryKeySelective(record);
    }

    public int updateByPrimaryKey(Deductions record) {
    	return deductionsMapper.updateByPrimaryKey(record);
    }
    
    //根据hotelId查询扣除费用
    public Deductions selectByHotelId(Integer hotelId) {
    	return deductionsMapper.selectByHotelId(hotelId);
    }
    
}
