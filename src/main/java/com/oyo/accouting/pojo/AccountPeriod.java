package com.oyo.accouting.pojo;

import java.math.BigDecimal;
import java.util.Date;

import javax.persistence.Table;

import lombok.Getter;
import lombok.Setter;

//账期对账表
@Getter
@Setter
@Table(name="account_period")
public class AccountPeriod {
	
    private Integer id;//主键id
    private String oyoId;//oyo id
	private Integer uniqueCode;//unique Code
	private String hotelName;// 酒店名称
	private String accountPeriod;// 账期，如：201807
	private String orderNo;// 订单号
	private String guestName;// 客人姓名
	private String bookingGuestName;//预定人名称
	private String bookingSecondaryGuestName;//预定人名称2
	private Integer orderChannelCode;//订单渠道code
	private String orderChannel;// 订单渠道
	private String channelName;// 渠道名
	private String checkInDate;// 入住日期，格式：yyyy-MM-dd,查询显示字段
	private String checkOutDate;// 退房日期，格式：yyyy-MM-dd,查询显示字段
	private Integer statusCode;//订单状态code;
    private String statusDesc;//订单状态描述;
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
    private String paymentMethod;//支付方式
    private String paymentDetails;//支付明细
    private String paymentType;//支付类型（预付/后付费）
    private BigDecimal otaCommission;//OTA佣金
    private BigDecimal otaCommissionTax;//OYO佣金税额
    private String otaId;//OTA ID
	private String otaName;//OTA Name
    private String city;//City
    private String cityCh;//城市
    private String region;//region
    private Integer hotelId;//Hotels ID
    private BigDecimal currentMonthRate;//本月匹配费率
    private BigDecimal oyoShare;//OYO share
    private String startDateOfAccountPeriod;// 本账期开始日期
	private String endDateOfAccountPeriod;// 本账期结束日期
    private Integer checkInDays;// 本期入住天数
    private BigDecimal roomPrice;// 房间价格
    private BigDecimal currentMonthSettlementTotalAmountCompute;// 本月应结算总额（计算）,=房价*天数    
    private String revenueCheckResults;//营收核对结果
    private String reasonsForRevenueDifference;//营收差异原因
    private String proportions;//提成比例
    private String paymentTypeCheckingResult;//顾客选择方式核对结果
    private String platformFeePayableParty;//平台费承担方
    private String remarks;//备注
    private Date createTime;//创建时间
    
}