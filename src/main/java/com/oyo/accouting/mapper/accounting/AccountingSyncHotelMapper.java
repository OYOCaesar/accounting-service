package com.oyo.accouting.mapper.accounting;

import com.oyo.accouting.pojo.SyncHotel;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface AccountingSyncHotelMapper extends com.github.abel533.mapper.Mapper<SyncHotel> {

    public List<com.oyo.accouting.pojo.SyncHotel> querySyncHotelList(@Param("syncHotel") com.oyo.accouting.pojo.SyncHotel syncHotel);


}
