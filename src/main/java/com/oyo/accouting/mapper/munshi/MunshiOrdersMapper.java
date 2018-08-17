package com.oyo.accouting.mapper.munshi;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import com.oyo.accouting.bean.QueryMunshiAccountingDto;

@Mapper
public interface MunshiOrdersMapper {
	
	//条件查询CRS中的Ar and AP信息
	public List<QueryMunshiAccountingDto> queryMunshiArApInfoByCondition(Map<String,Object> map);

	//获取所有酒店的AR金额
	public List<HashMap<String, String>> calHotelAmount(HashMap<String,Object> map);
	
	//获取hotel对应的Owner Share
	public List<HashMap<String, String>> getHotelOwnerShare(HashMap<String,String> map);
	
	//根据hotelId获取hotel名称
	public String getHotelNameById(@Param("hotelId") Integer hotelId);
	
	//根据hotelId和checkIn时间获取Owner Share
	public List<HashMap<String, String>> getHotelOwnerShareByHotelIdAndChekInDate(Map<String,Object> map);
	
}
