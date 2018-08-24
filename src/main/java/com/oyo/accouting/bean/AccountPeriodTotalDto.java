package com.oyo.accouting.bean;

import java.math.BigDecimal;

import lombok.Getter;
import lombok.Setter;

/***
 *  账期明细对象
 * @author ZhangSuYun
 * @date 2018-08-21
 */
@Getter
@Setter
public class AccountPeriodTotalDto {
    
    private String oyoId;//oyo id
	private Integer uniqueCode;//unique Code
	private String hotelName;// 酒店名称
	private String accountPeriod;// 账期，如：201807
	private Integer roomsNumber;//已用客房数
    private Integer currentMonthRoomsNumber;//本月已用间夜数
    private BigDecimal orderTotalAmount;//订单总额
    private BigDecimal currentMonthSettlementTotalAmount;//本月应结算总额
    private BigDecimal currentMonthOyoShareAmount;//本月OYO提佣额
	private BigDecimal ownerGrossShareAmount;//业主毛份额(A)
    private BigDecimal disputeOrderAmount;//争议订单金额(B)
    private BigDecimal otaExemptionAmount;//OTA豁免额(C)
    private BigDecimal currentMonthOwnersNetShareAmount;//当月业主净份额(A+B+C)
    private BigDecimal currentMonthPayAmont;//当月应付
    private BigDecimal hotelChargeAmount;//酒店收取金额(已结算)
    private BigDecimal hotelChargeMoreAmount;//酒店多收取金额
    private BigDecimal oyoChargeAmount;//OYO收取金额(已结算)
    private BigDecimal oyoChargeMoreAmount;//OYO多收取金额
    private BigDecimal otaCommission;//OTA佣金
    private BigDecimal otaCommissionTax;//OYO佣金税额
    private String city;//City
    private String cityCh;//城市
    private String region;//region
    private Integer hotelId;//Hotels ID
    private BigDecimal currentMonthRate;//本月匹配费率
    private BigDecimal oyoShare;//OYO share
    private Integer checkInDays;// 本期入住天数
    private BigDecimal roomPrice;// 房间价格
    private BigDecimal currentMonthSettlementTotalAmountCompute;// 本月应结算总额（计算）,=房价*天数
    
}
