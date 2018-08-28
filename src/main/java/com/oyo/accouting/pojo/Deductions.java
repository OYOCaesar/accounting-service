package com.oyo.accouting.pojo;

import java.math.BigDecimal;
import java.util.Date;

import javax.persistence.Table;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Table(name="deductions")
public class Deductions {
    private Integer id;

    private Integer hotelId;
    
    private String accountPeriod;

    private BigDecimal tempMemPromotionFee;

    private BigDecimal praisePlatformPromotionFee;

    private BigDecimal flyingPigsPlatformPromotionFee;

    private BigDecimal newActivityA;

    private BigDecimal newActivityB;

    private BigDecimal newActivityC;

    private BigDecimal currentMonthReceivedOyoCommission;

    private Date createTime;
    
}