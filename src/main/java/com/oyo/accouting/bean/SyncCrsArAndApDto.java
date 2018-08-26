package com.oyo.accouting.bean;

import java.math.BigDecimal;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class SyncCrsArAndApDto {

  private Integer id;
  private String syncYearMonth;
  private Integer hotelId;
  private String hotelName;
  private BigDecimal arAmount;
  private BigDecimal apAmount;
  private BigDecimal rate;
  private java.sql.Timestamp createTime;
  private Boolean isSync;
  private Boolean isDel;

}
