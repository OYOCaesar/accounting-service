package com.oyo.accouting.bean;

import java.math.BigDecimal;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class QueryCrsAccountingDto {

	private Integer hotelId;
	private String hotelName;
	private BigDecimal arAmount;
	private BigDecimal apAmount;
	private Integer rate;
	private String checkInDate;
	private String checkOutDate;
	private Integer status;
	
	private String checkInDateStart;//checkIn 查询开始时间
	private String checkInDateEnd;//checkIn 查询结束时间
	
	private String checkOutDateStart;//checkOut 查询开始时间
	private String checkOutDateEnd;//checkOut 查询结束时间
	
	private Integer pageNum = 1;
	private Integer pageSize = 10;
	
}
