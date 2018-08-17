package com.oyo.accouting.mapper.accounting;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import com.oyo.accouting.bean.QueryCrsAccountingDto;
import com.oyo.accouting.bean.SyncCrsArAndApDto;
import com.oyo.accouting.pojo.SyncCrsArAndAp;

@Mapper
public interface SyncCrsArAndApMapper extends com.github.abel533.mapper.Mapper<SyncCrsArAndAp> {

	//条件查询ar and ap数据
    public List<SyncCrsArAndApDto> selectByMap(Map<String,Object> map);
    
    //分页条件查询ar and ap数据
    public List<SyncCrsArAndApDto> selectByMapPage(Map<String,Object> map);
    
    //批量插入ar and ap数据
    public int insertCrsArAndApList(@Param("crsArAndApList") List<SyncCrsArAndAp> syncCrsArAndApList);
    
    //当条插入ar and ap数据
    public int insertCrsArAndAp(SyncCrsArAndAp syncCrsArAndAp);
    
    //批量更新为“已同步”状态
    public int updateCrsArAndApIsSyncList(@Param("crsArAndApList") List<SyncCrsArAndAp> syncCrsArAndApList);
    
    //批量更新为“已删除”状态
    public int updateCrsArAndApIsDelcList(@Param("crsArAndApList") List<SyncCrsArAndAp> syncCrsArAndApList);
    
    //逻辑删除指定年月同步的ar ap数据
    public int updateYearMonthCrsArAndApIsDelBatch(@Param("syncYearMonth") String syncYearMonth);

}
