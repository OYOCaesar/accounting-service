package com.oyo.accouting.bean;


import javax.persistence.Table;
import java.sql.Date;
import java.sql.Timestamp;

public class AccountDetailsDto {

  private Long id;
  private Long hotelId;
  private String name;
  private String bankName;
  private String bankAddress;
  private String bankAccountNo;
  private Long accountType;
  private String bankIfscCode;
  private String bankMicrCode;
  private Timestamp createdAt;
  private Timestamp updatedAt;
  private Long itemId;
  private String itemType;
  private String serviceTaxNo;
  private String panNo;
  private String vatNo;
  private String cstNo;
  private Long creditLimit;
  private Long creditUsagePeriod;
  private Date lastBilledDate;
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

  public Long getId() {
    return id;
  }

  public void setId(Long id) {
    this.id = id;
  }

  public Long getHotelId() {
    return hotelId;
  }

  public void setHotelId(Long hotelId) {
    this.hotelId = hotelId;
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public String getBankName() {
    return bankName;
  }

  public void setBankName(String bankName) {
    this.bankName = bankName;
  }

  public String getBankAddress() {
    return bankAddress;
  }

  public void setBankAddress(String bankAddress) {
    this.bankAddress = bankAddress;
  }

  public String getBankAccountNo() {
    return bankAccountNo;
  }

  public void setBankAccountNo(String bankAccountNo) {
    this.bankAccountNo = bankAccountNo;
  }

  public Long getAccountType() {
    return accountType;
  }

  public void setAccountType(Long accountType) {
    this.accountType = accountType;
  }

  public String getBankIfscCode() {
    return bankIfscCode;
  }

  public void setBankIfscCode(String bankIfscCode) {
    this.bankIfscCode = bankIfscCode;
  }

  public String getBankMicrCode() {
    return bankMicrCode;
  }

  public void setBankMicrCode(String bankMicrCode) {
    this.bankMicrCode = bankMicrCode;
  }

  public Timestamp getCreatedAt() {
    return createdAt;
  }

  public void setCreatedAt(Timestamp createdAt) {
    this.createdAt = createdAt;
  }

  public Timestamp getUpdatedAt() {
    return updatedAt;
  }

  public void setUpdatedAt(Timestamp updatedAt) {
    this.updatedAt = updatedAt;
  }

  public Long getItemId() {
    return itemId;
  }

  public void setItemId(Long itemId) {
    this.itemId = itemId;
  }

  public String getItemType() {
    return itemType;
  }

  public void setItemType(String itemType) {
    this.itemType = itemType;
  }

  public String getServiceTaxNo() {
    return serviceTaxNo;
  }

  public void setServiceTaxNo(String serviceTaxNo) {
    this.serviceTaxNo = serviceTaxNo;
  }

  public String getPanNo() {
    return panNo;
  }

  public void setPanNo(String panNo) {
    this.panNo = panNo;
  }

  public String getVatNo() {
    return vatNo;
  }

  public void setVatNo(String vatNo) {
    this.vatNo = vatNo;
  }

  public String getCstNo() {
    return cstNo;
  }

  public void setCstNo(String cstNo) {
    this.cstNo = cstNo;
  }

  public Long getCreditLimit() {
    return creditLimit;
  }

  public void setCreditLimit(Long creditLimit) {
    this.creditLimit = creditLimit;
  }

  public Long getCreditUsagePeriod() {
    return creditUsagePeriod;
  }

  public void setCreditUsagePeriod(Long creditUsagePeriod) {
    this.creditUsagePeriod = creditUsagePeriod;
  }

  public Date getLastBilledDate() {
    return lastBilledDate;
  }

  public void setLastBilledDate(Date lastBilledDate) {
    this.lastBilledDate = lastBilledDate;
  }

  public Long getSettlementPeriod() {
    return settlementPeriod;
  }

  public void setSettlementPeriod(Long settlementPeriod) {
    this.settlementPeriod = settlementPeriod;
  }

  public String getDiscount() {
    return discount;
  }

  public void setDiscount(String discount) {
    this.discount = discount;
  }

  public Long getDiscountType() {
    return discountType;
  }

  public void setDiscountType(Long discountType) {
    this.discountType = discountType;
  }

  public String getBtcAllowed() {
    return btcAllowed;
  }

  public void setBtcAllowed(String btcAllowed) {
    this.btcAllowed = btcAllowed;
  }

  public Long getStatus() {
    return status;
  }

  public void setStatus(Long status) {
    this.status = status;
  }

  public String getSkipPrepaid() {
    return skipPrepaid;
  }

  public void setSkipPrepaid(String skipPrepaid) {
    this.skipPrepaid = skipPrepaid;
  }

  public Long getWalletId() {
    return walletId;
  }

  public void setWalletId(Long walletId) {
    this.walletId = walletId;
  }

  public Long getAchStatus() {
    return achStatus;
  }

  public void setAchStatus(Long achStatus) {
    this.achStatus = achStatus;
  }

  public Long getSharePc() {
    return sharePc;
  }

  public void setSharePc(Long sharePc) {
    this.sharePc = sharePc;
  }

  public String getCouponAllowed() {
    return couponAllowed;
  }

  public void setCouponAllowed(String couponAllowed) {
    this.couponAllowed = couponAllowed;
  }

  public Long getBillingCycle() {
    return billingCycle;
  }

  public void setBillingCycle(Long billingCycle) {
    this.billingCycle = billingCycle;
  }

  public Long getPaymentCycle() {
    return paymentCycle;
  }

  public void setPaymentCycle(Long paymentCycle) {
    this.paymentCycle = paymentCycle;
  }

  public String getMetadata() {
    return metadata;
  }

  public void setMetadata(String metadata) {
    this.metadata = metadata;
  }

  public String getFixedPricing() {
    return fixedPricing;
  }

  public void setFixedPricing(String fixedPricing) {
    this.fixedPricing = fixedPricing;
  }

  public String getGenerateInvoice() {
    return generateInvoice;
  }

  public void setGenerateInvoice(String generateInvoice) {
    this.generateInvoice = generateInvoice;
  }

  public Long getCommissionPayoutTrigger() {
    return commissionPayoutTrigger;
  }

  public void setCommissionPayoutTrigger(Long commissionPayoutTrigger) {
    this.commissionPayoutTrigger = commissionPayoutTrigger;
  }

  public String getAadharNo() {
    return aadharNo;
  }

  public void setAadharNo(String aadharNo) {
    this.aadharNo = aadharNo;
  }
}
