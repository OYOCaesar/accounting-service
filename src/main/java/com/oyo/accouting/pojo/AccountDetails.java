package com.oyo.accouting.pojo;


import lombok.Getter;
import lombok.Setter;

import javax.persistence.Table;

@Getter
@Setter
@Table(name="account_details")
public class AccountDetails {

  private Long id;
  private Long hotelId;
  private String name;
  private String bankName;
  private String bankAddress;
  private String bankAccountNo;
  private Long accountType;
  private String bankIfscCode;
  private String bankMicrCode;
  private java.sql.Timestamp createdAt;
  private java.sql.Timestamp updatedAt;
  private Long itemId;
  private String itemType;
  private String serviceTaxNo;
  private String panNo;
  private String vatNo;
  private String cstNo;
  private Long creditLimit;
  private Long creditUsagePeriod;
  private java.sql.Date lastBilledDate;
  private Long settlementPeriod;
  private String discount;
  private Long discountType;
  private String btcAllowed;
  private Long status;
  private String skipPrepaid;
  private Long walletId;
  private Long achStatus;
  private Long sharePc;
  private String couponAllowed;
  private Long billingCycle;
  private Long paymentCycle;
  private String metadata;
  private String fixedPricing;
  private String generateInvoice;
  private Long commissionPayoutTrigger;
  private String aadharNo;


}
