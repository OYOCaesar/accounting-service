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
  @Column(name = "cntct_prsn")
  private String cntctPrsn;
  @Column(name = "lic_trad_num")
  private String licTradNum;
  @Column(name = "u_crsid")
  private String uCrsid;
  @Column(name = "card_type")
  private String cardType;
  @Column(name = "deb_pay_acct")
  private String debPayAcct;

  private String batch;

  private String contacts;
  private String address;

  @Column(name = "start_date")
  private String startDate;

  @Column(name = "update_remark")
  private String updateRemark;





}
