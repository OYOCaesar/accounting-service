package com.oyo.accouting.pojo;

import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

import javax.persistence.Id;
import javax.persistence.Table;

@Getter
@Setter
@Table(name = "sync_crs_ar_ap")
public class SyncCrsArAndAp {

  @Id
  private Integer id;
  private String syncYearMonth;
  private Integer hotelId;
  private String hotelName;
  private BigDecimal arAmount;
  private BigDecimal apAmount;
  private Integer rate;
  private java.sql.Timestamp createTime;
  private Boolean isSync;
  private Boolean isDel;


}
