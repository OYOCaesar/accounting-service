package com.oyo.accouting.pojo;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.Column;
import javax.persistence.Table;

@Getter
@Setter
@Table(name="sync_hotel")
public class SyncHotel {

  private Integer id;
  private String cardcode;
  private String cardname;
  private String valid;
  @Column(name = "cntctPrsn")
  private String cntctPrsn;
  @Column(name = "licTradNum")
  private String licTradNum;
  @Column(name = "U_CRSID")
  private String uCrsid;
  private String contacts;
  private String address;


}
