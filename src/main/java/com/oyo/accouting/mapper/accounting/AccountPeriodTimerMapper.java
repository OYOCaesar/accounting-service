package com.oyo.accouting.mapper.accounting;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import com.oyo.accouting.pojo.AccountPeriodTimer;

@Mapper
public interface AccountPeriodTimerMapper {
    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table account_period_timer
     *
     * @mbg.generated Sun Sep 02 13:29:16 CST 2018
     */
    int deleteByPrimaryKey(Integer id);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table account_period_timer
     *
     * @mbg.generated Sun Sep 02 13:29:16 CST 2018
     */
    int insert(AccountPeriodTimer record);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table account_period_timer
     *
     * @mbg.generated Sun Sep 02 13:29:16 CST 2018
     */
    int insertSelective(AccountPeriodTimer record);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table account_period_timer
     *
     * @mbg.generated Sun Sep 02 13:29:16 CST 2018
     */
    AccountPeriodTimer selectByPrimaryKey(Integer id);
    
    //条件查询
    List<AccountPeriodTimer> selectByAccountPeriodTimer(AccountPeriodTimer accountPeriodTimer);
    
    //根据功能名称列表查询
    List<AccountPeriodTimer> selectByFunctionNameList(@Param("list") List<String> list);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table account_period_timer
     *
     * @mbg.generated Sun Sep 02 13:29:16 CST 2018
     */
    int updateByPrimaryKeySelective(AccountPeriodTimer record);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table account_period_timer
     *
     * @mbg.generated Sun Sep 02 13:29:16 CST 2018
     */
    int updateByPrimaryKey(AccountPeriodTimer record);
}