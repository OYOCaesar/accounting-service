package com.oyo.accouting.mapper.crs;

import java.util.HashMap;
import java.util.List;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface CrsAccountMapper {

	//获取所有酒店的AR金额
	public List<HashMap<String, String>> calHotelAmount();
	
	//获取hotel对应的Owner Share
	public List<HashMap<String, String>> getHotelOwnerShare();
	
	//根据hotelId获取hotel名称
	public String getHotelNameById(@Param("hotelId") String hotelId);
	
}
