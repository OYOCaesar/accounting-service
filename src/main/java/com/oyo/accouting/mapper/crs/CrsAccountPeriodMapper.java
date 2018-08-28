package com.oyo.accouting.mapper.crs;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import com.oyo.accouting.bean.CrsEnumsDto;
import com.oyo.accouting.bean.QueryAccountPeriodDto;
import com.oyo.accouting.pojo.AccountPeriod;

@Mapper
public interface CrsAccountPeriodMapper {
	
	//条件查询CRS中账期信息
	public List<AccountPeriod> queryAccountPeriodByCondition(QueryAccountPeriodDto queryAccountPeriodDto);
	
	//条件查询crs枚举类型
	public List<CrsEnumsDto> queryCrsEnumByTableName(@Param("tableName") String tableName);
	
}
