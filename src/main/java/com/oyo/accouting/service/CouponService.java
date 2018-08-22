package com.oyo.accouting.service;

import com.google.common.base.Function;
import com.google.common.collect.Lists;
import com.oyo.accouting.bean.*;
import com.oyo.accouting.enums.CouponMetadataStatus;
import com.oyo.accouting.enums.CouponStatus;
import com.oyo.accouting.mapper.coupon.CouponMetadataMapper;
import com.oyo.accouting.mapper.coupon.CouponsMapper;
import com.oyo.accouting.util.DateUtils;
import com.oyo.accouting.util.ListUtils;
import com.oyo.accouting.util.MapUtils;
import org.apache.commons.collections.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Nullable;
import java.util.Arrays;
import java.util.List;

@Service
public class CouponService {
    private static Logger _log = LoggerFactory.getLogger(CouponService.class);
    @Autowired
    private CouponMetadataMapper couponMetadataMapper;
    @Autowired
    private CouponsMapper couponsMapper;

    public String expire() {
        // 一般情况不会一次出现太多过期的优惠券模板，暂不分页处理
        List<CouponMetadata> couponMetadataList = couponMetadataMapper.selectByConditions(MapUtils.createConditionMap("endTime", DateUtils.getCurrentDate(),
                "statusList", Arrays.asList(CouponMetadataStatus.READY.getStatus(),CouponMetadataStatus.ONLINE.getStatus())));
        _log.info("expire metadata = {}",couponMetadataList);
        if(CollectionUtils.isNotEmpty(couponMetadataList)){
            List<List<CouponMetadata>> metaList = ListUtils.partition(couponMetadataList,200);
            for(List<CouponMetadata> subMetaList : metaList){
                doExpire(subMetaList);
            }
        }
        return null;
    }

    @Transactional(rollbackFor = Throwable.class,propagation = Propagation.REQUIRES_NEW)
    public void doExpire(List<CouponMetadata> subMetaList){
       couponsMapper.updateByConditions(MapUtils.createConditionMap("metadataIdList", Lists.transform(subMetaList, new Function<CouponMetadata,Long>() {
            @Override
            public Long apply(@Nullable CouponMetadata o) {
                return o.getId();
            }
        }),"statusList", Arrays.asList(CouponStatus.UNUSED.getStatus()),"targetStatus",CouponStatus.EXPIRED.getStatus(),"lastUpdateTime",DateUtils.getCurrentDate()));

        couponMetadataMapper.updateByConditions(MapUtils.createConditionMap("idList", Lists.transform(subMetaList, new Function<CouponMetadata,Long>() {
            @Override
            public Long apply(@Nullable CouponMetadata o) {
                return o.getId();
            }
        }),"statusList",Arrays.asList(CouponMetadataStatus.READY.getStatus(),CouponMetadataStatus.ONLINE.getStatus()),"targetStatus",CouponMetadataStatus.OFFLINE.getStatus(),"lastUpdateTime",DateUtils.getCurrentDate()));
    }



}