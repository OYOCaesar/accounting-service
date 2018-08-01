package com.oyo.accouting.mapper.crs;

import java.util.HashMap;

import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface CrsDemoMapper {

	
	public HashMap<String, String> calHotelAmount();
}
