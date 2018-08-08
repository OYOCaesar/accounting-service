package com.oyo.accouting.pojo;


import lombok.Getter;
import lombok.Setter;

import javax.persistence.Table;

@Getter
@Setter
@Table(name="account_details")
public class AccountDetails {

  private Integer id;
  private Integer hotelId;
  private String name;
  private String bankName;
  private String bankAddress;
  private String bankAccountNo;
  private Integer accountType;
  private String bankIfscCode;
  private String bankMicrCode;
  private java.sql.Timestamp createdAt;
  private java.sql.Timestamp updatedAt;
  private Integer itemId;
  private String itemType;
  private String serviceTaxNo;
  private String panNo;
  private String vatNo;
  private String cstNo;
  private Integer creditLimit;
  private Integer creditUsagePeriod;
  private java.sql.Date lastBilledDate;
  private Integer settlementPeriod;
  private String discount;
  private Integer discountType;
  private String btcAllowed;
  private Integer status;
  private String skipPrepaid;
  private Integer walletId;
  private Integer achStatus;
  private Integer sharePc;
  private String couponAllowed;
  private Integer billingCycle;
  private Integer paymentCycle;
  private String metadata;
  private String fixedPricing;
  private String generateInvoice;
  private Integer commissionPayoutTrigger;
  private String aadharNo;


}
