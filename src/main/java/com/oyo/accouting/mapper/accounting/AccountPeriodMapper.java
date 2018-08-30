package com.oyo.accouting.mapper.accounting;

import java.util.List;

import org.apache.ibatis.annotations.Param;

import com.oyo.accouting.bean.AccountPeriodDto;
import com.oyo.accouting.bean.QueryAccountPeriodDto;
import com.oyo.accouting.bean.SyncCrsArAndApDto;
import com.oyo.accouting.pojo.AccountPeriod;

/***
  * 账期对账Mapper
 * @author ZhangSuYun
 * @date 2018-08-25
 */
public interface AccountPeriodMapper {
    int deleteByPrimaryKey(Integer id);

    int insert(AccountPeriod record);

    int insertSelective(AccountPeriod record);

    AccountPeriod selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(AccountPeriod record);

    int updateByPrimaryKey(AccountPeriod record);
    
    //条件查询账期数据
    List<AccountPeriodDto> queryAccountPeriodByCondition(QueryAccountPeriodDto queryAccountPeriodDto);
    
    //条件查询账期统计数据
    List<AccountPeriodDto> queryAccountPeriodStatisticsByCondition(QueryAccountPeriodDto queryAccountPeriodDto);
    
    //批量插入账期数据
    public int insertBtach(@Param("accountPeriodList") List<AccountPeriod> accountPeriodList);
    
    //按账期删除对账信息
    public int deleteByAccountPeriod(@Param("accountPeriod") String accountPeriod);
    
    //按账期truncate对账信息
    public int truncateByAccountPeriod(@Param("accountPeriod") String accountPeriod);
    
    //按账期查询对账的记录数
    public int selectByAccountPeriod(@Param("accountPeriod") String accountPeriod);
    
    //查询酒店指定月份的收入信息
    public List<SyncCrsArAndApDto> selectArByAccountPeriod(@Param("accountPeriod") String accountPeriod);
    
}