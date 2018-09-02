package com.oyo.accouting.pojo;

import java.util.Date;

import javax.persistence.Table;

import lombok.Getter;
import lombok.Setter;

//账期导出定时器
@Getter
@Setter
@Table(name="account_period_timer")
public class AccountPeriodTimer {
    private Integer id;
    private String functionName;
    private Long createtime;
    private Integer status;
    private String creator;
    private Date createdate;
}