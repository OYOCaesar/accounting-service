package com.oyo.accouting.mapper.crs;

import com.oyo.accouting.bean.HotelDto;
import com.oyo.accouting.pojo.Hotel;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface CrsHotelMapper{


    public List<HotelDto> queryHotelList(@Param("searchHotel")HotelDto searchHotel);

}
