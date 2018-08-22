package com.oyo.accouting.mapper.coupon;

import com.oyo.accouting.bean.Coupons;

import java.util.List;
import java.util.Map;

public interface CouponsMapper {
    Coupons selectByPrimaryKey(Long id);

    int updateByPrimaryKeySelective(Coupons record);

    List<Coupons> selectByConditions(Map<String,Object> map);

    void updateByConditions(Map<String,Object> map);
}