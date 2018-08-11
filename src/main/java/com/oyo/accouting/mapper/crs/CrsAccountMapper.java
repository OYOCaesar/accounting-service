package com.oyo.accouting.mapper.crs;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import com.oyo.accouting.bean.QueryCrsAccountingDto;

@Mapper
public interface CrsAccountMapper {
	
	//条件查询CRS中的Ar and AP信息
	public List<QueryCrsAccountingDto> queryCrsArApInfoByCondition(QueryCrsAccountingDto queryCrsAccountingDto);

	//获取所有酒店的AR金额
	public List<HashMap<String, String>> calHotelAmount();
	
	//获取hotel对应的Owner Share
	public List<HashMap<String, String>> getHotelOwnerShare();
	
	//根据hotelId获取hotel名称
	public String getHotelNameById(@Param("hotelId") Integer hotelId);
	
	//根据hotelId和checkIn时间获取Owner Share
	public List<HashMap<String, String>> getHotelOwnerShareByHotelIdAndChekInDate(Map<String,Object> map);
	
}
