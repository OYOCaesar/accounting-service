package com.oyo.accouting.bean;

import java.math.BigDecimal;
import java.util.Date;

import lombok.Getter;
import lombok.Setter;

/***
 *  账期对账Accounting对象
 * @author ZhangSuYun
 * @date 2018-08-24
 */
@Getter
@Setter
public class DeductionsDto {
    
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
