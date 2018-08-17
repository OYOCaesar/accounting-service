package com.oyo.accouting.mapper.crs;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import com.oyo.accouting.bean.HotelDto;

@Mapper
public interface CrsHotelMapper{


    public List<HotelDto> queryHotelList(@Param("searchHotel")HotelDto searchHotel);
    
    //条件查询酒店id和名称
    public List<HotelDto> queryHotelNameList(@Param("searchHotel")HotelDto searchHotel);
    

}
