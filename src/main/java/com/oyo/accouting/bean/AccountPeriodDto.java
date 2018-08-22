package com.oyo.accouting.bean;

import java.math.BigDecimal;

import lombok.Getter;
import lombok.Setter;

/***
 *  账期对象
 * @author ZhangSuYun
 * @date 2018-08-21
 */
@Getter
@Setter
public class AccountPeriodDto {
    
	private String accountPeriod;// 账期，如：201807
	private String hotelName;// 酒店名称
	private String orderNo;// 订单号
	private String guestName;// 客人姓名
	private String orderChannel;// 订单渠道
	private String channelName;// 渠道名
	private String checkInDate;// 入住日期，格式：yyyy-MM-dd,查询显示字段
	private String checkOutDate;// 退房日期，格式：yyyy-MM-dd,查询显示字段
	private String startDateOfAccountPeriod;// 本账期开始日期
	private String endDateOfAccountPeriod;// 本账期结束日期
	private Integer checkInDays;// 本期入住天数
	private BigDecimal roomPrice;// 房间价格
    private BigDecimal currentMonthSettlementTotalAmountCompute;// 本月应结算总额（计算）,=房价*天数
    private Integer statusCode;//订单状态code;
    private String statusDes;//订单状态描述;
    private Integer roomsNumber;//已用客房数
    private Integer currentMonthRoomsNumber;//本月已用间夜数
    private BigDecimal orderTotalAmount;//订单总额
    private BigDecimal currentMonthSettlementTotalAmount;//本月应结算总额
    private String paymentMethod;//支付方式
    private String paymentDetails;//支付明细
    private String paymentType;//支付类型（预付/后付费）
    private String otaId;//OTA ID
    private String city;//City
    private String region;//region
    private Integer hotelId;//Hotels ID
    private BigDecimal currentMonthRate;//本月匹配费率
    private BigDecimal oyoShare;//OYO share
    
    //查询使用的
    private String startYearAndMonthQuery;//账期开始年月，如：2018-06
    private String endYearAndMonthQuery;//账期结束年月，如：2018-07
    
}
