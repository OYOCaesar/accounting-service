package com.oyo.accouting.mapper.coupon;

import com.oyo.accouting.bean.CouponMetadata;
import java.util.List;
import java.util.Map;

public interface CouponMetadataMapper {
    CouponMetadata selectByPrimaryKey(Long id);

    List<CouponMetadata> selectByConditions(Map<String,Object> map);

    int updateByPrimaryKeySelective(CouponMetadata record);

    void updateByConditions(Map<String,Object> map);
}