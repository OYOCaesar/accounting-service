package com.oyo.accouting.mapper.crs;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import com.oyo.accouting.bean.AccountPeriodDetailsDto;
import com.oyo.accouting.bean.AccountPeriodDto;
import com.oyo.accouting.bean.AccountPeriodTotalDto;
import com.oyo.accouting.bean.CrsEnumsDto;
import com.oyo.accouting.bean.QueryAccountPeriodDto;

@Mapper
public interface CrsAccountPeriodMapper {
	
	//条件查询CRS中账期信息
	public List<AccountPeriodDto> queryAccountPeriodByCondition(@Param("list") List<QueryAccountPeriodDto> list);
	
	//条件查询CRS中统计账期信息
	public List<AccountPeriodTotalDto> queryTotalAccountPeriodByCondition(@Param("list") List<QueryAccountPeriodDto> list);
	
	//条件查询CRS中明细账期信息
	public List<AccountPeriodDetailsDto> queryDetailsAccountPeriodByCondition(@Param("list") List<QueryAccountPeriodDto> list);
	
	//条件查询crs枚举类型
	public List<CrsEnumsDto> queryCrsEnumByTableName(@Param("tableName") String tableName);
	
}
