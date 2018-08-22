package com.oyo.accouting.bean;

import lombok.Getter;
import lombok.Setter;

/***
 *  账期查询条件对象
 * @author ZhangSuYun
 * @date 2018-08-22
 */
@Getter
@Setter
public class QueryAccountPeriodDto {
  
	private String accountPeriod;// 账期，如：201807
	private String accountPeriodStart;// 账期开始日期，格式：yyyy-MM-dd
	private String accountPeriodEnd;// 账期结束日期，格式：yyyy-MM-dd
	private String nextAccountPeriodStart;// 下个账期开始日期，格式：yyyy-MM-dd
	
	//查询使用的
    private String startYearAndMonthQuery;//账期开始年月，如：2018-06，此字段只作为前端传参使用，后端mybatis中用不到
    private String endYearAndMonthQuery;//账期结束年月，如：2018-07，此字段只作为前端传参使用，后端mybatis中用不到
	private String checkInDate;// 入住日期，格式：yyyy-MM-dd,查询显示字段
	private String checkOutDate;// 退房日期，格式：yyyy-MM-dd,查询显示字段
	private String orderNo;// 订单号
    private String region;//region
    private String city;//City
	private String hotelName;// 酒店名称
	
	private Integer pageNum;//第几页，从1开始
	private Integer pageSize;//每页记录数
	
    
}
