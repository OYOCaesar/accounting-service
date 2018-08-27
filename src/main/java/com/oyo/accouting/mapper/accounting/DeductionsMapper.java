package com.oyo.accouting.mapper.accounting;

import com.oyo.accouting.pojo.Deductions;

public interface DeductionsMapper {
    int deleteByPrimaryKey(Integer id);

    int insert(Deductions record);

    int insertSelective(Deductions record);

    Deductions selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(Deductions record);

    int updateByPrimaryKey(Deductions record);
    
    //根据hotelId查询扣除费用
    Deductions selectByHotelId(Integer hotelId);
    
}