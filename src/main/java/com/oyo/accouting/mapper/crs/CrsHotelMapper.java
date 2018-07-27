package com.oyo.accouting.mapper.crs;

import com.oyo.accouting.pojo.Hotel;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface CrsHotelMapper{


    @Select("select * from hotels where hotels.status!= 5  and (hotels.country_id is null or hotels.country_id = 1)")
    public List<Hotel> queryHotelList();

}
