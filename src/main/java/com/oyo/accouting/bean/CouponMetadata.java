package com.oyo.accouting.bean;

import java.math.BigDecimal;
import java.util.Date;

public class CouponMetadata {
    private Long id;

    private String couponMetadataCode;

    private String name;

    private Integer category;

    private Long maxNum;

    private String ruleDescription;

    private Integer status;

    private BigDecimal amount;

    private BigDecimal maxAmount;

    private Date startTime;

    private Date endTime;

    private Integer perHotelUsageMax;

    private String userLevel;

    private String channel;

    private Integer perUserUsageMax;

    private BigDecimal minPrice;

    private BigDecimal maxPrice;

    private String extKey;

    private Date createTime;

    private String createBy;

    private Date lastUpdateTime;

    private String lastUpdateBy;

    private Integer currencyId;

    private String includeHotelList;

    private String excludeHotelList;

    private String excludeAreaList;

    private String includeAreaList;

    private String includeUserList;

    private String excludeUserList;

    private String showName;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getCouponMetadataCode() {
        return couponMetadataCode;
    }

    public void setCouponMetadataCode(String couponMetadataCode) {
        this.couponMetadataCode = couponMetadataCode == null ? null : couponMetadataCode.trim();
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name == null ? null : name.trim();
    }

    public Integer getCategory() {
        return category;
    }

    public void setCategory(Integer category) {
        this.category = category;
    }

    public Long getMaxNum() {
        return maxNum;
    }

    public void setMaxNum(Long maxNum) {
        this.maxNum = maxNum;
    }

    public String getRuleDescription() {
        return ruleDescription;
    }

    public void setRuleDescription(String ruleDescription) {
        this.ruleDescription = ruleDescription == null ? null : ruleDescription.trim();
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    public BigDecimal getMaxAmount() {
        return maxAmount;
    }

    public void setMaxAmount(BigDecimal maxAmount) {
        this.maxAmount = maxAmount;
    }

    public Date getStartTime() {
        return startTime;
    }

    public void setStartTime(Date startTime) {
        this.startTime = startTime;
    }

    public Date getEndTime() {
        return endTime;
    }

    public void setEndTime(Date endTime) {
        this.endTime = endTime;
    }

    public Integer getPerHotelUsageMax() {
        return perHotelUsageMax;
    }

    public void setPerHotelUsageMax(Integer perHotelUsageMax) {
        this.perHotelUsageMax = perHotelUsageMax;
    }

    public String getUserLevel() {
        return userLevel;
    }

    public void setUserLevel(String userLevel) {
        this.userLevel = userLevel == null ? null : userLevel.trim();
    }

    public String getChannel() {
        return channel;
    }

    public void setChannel(String channel) {
        this.channel = channel == null ? null : channel.trim();
    }

    public Integer getPerUserUsageMax() {
        return perUserUsageMax;
    }

    public void setPerUserUsageMax(Integer perUserUsageMax) {
        this.perUserUsageMax = perUserUsageMax;
    }

    public BigDecimal getMinPrice() {
        return minPrice;
    }

    public void setMinPrice(BigDecimal minPrice) {
        this.minPrice = minPrice;
    }

    public BigDecimal getMaxPrice() {
        return maxPrice;
    }

    public void setMaxPrice(BigDecimal maxPrice) {
        this.maxPrice = maxPrice;
    }

    public String getExtKey() {
        return extKey;
    }

    public void setExtKey(String extKey) {
        this.extKey = extKey == null ? null : extKey.trim();
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    public String getCreateBy() {
        return createBy;
    }

    public void setCreateBy(String createBy) {
        this.createBy = createBy == null ? null : createBy.trim();
    }

    public Date getLastUpdateTime() {
        return lastUpdateTime;
    }

    public void setLastUpdateTime(Date lastUpdateTime) {
        this.lastUpdateTime = lastUpdateTime;
    }

    public String getLastUpdateBy() {
        return lastUpdateBy;
    }

    public void setLastUpdateBy(String lastUpdateBy) {
        this.lastUpdateBy = lastUpdateBy == null ? null : lastUpdateBy.trim();
    }

    public Integer getCurrencyId() {
        return currencyId;
    }

    public void setCurrencyId(Integer currencyId) {
        this.currencyId = currencyId;
    }

    public String getIncludeHotelList() {
        return includeHotelList;
    }

    public void setIncludeHotelList(String includeHotelList) {
        this.includeHotelList = includeHotelList == null ? null : includeHotelList.trim();
    }

    public String getExcludeHotelList() {
        return excludeHotelList;
    }

    public void setExcludeHotelList(String excludeHotelList) {
        this.excludeHotelList = excludeHotelList == null ? null : excludeHotelList.trim();
    }

    public String getExcludeAreaList() {
        return excludeAreaList;
    }

    public void setExcludeAreaList(String excludeAreaList) {
        this.excludeAreaList = excludeAreaList == null ? null : excludeAreaList.trim();
    }

    public String getIncludeAreaList() {
        return includeAreaList;
    }

    public void setIncludeAreaList(String includeAreaList) {
        this.includeAreaList = includeAreaList == null ? null : includeAreaList.trim();
    }

    public String getIncludeUserList() {
        return includeUserList;
    }

    public void setIncludeUserList(String includeUserList) {
        this.includeUserList = includeUserList == null ? null : includeUserList.trim();
    }

    public String getExcludeUserList() {
        return excludeUserList;
    }

    public void setExcludeUserList(String excludeUserList) {
        this.excludeUserList = excludeUserList == null ? null : excludeUserList.trim();
    }

    public String getShowName() {
        return showName;
    }

    public void setShowName(String showName) {
        this.showName = showName == null ? null : showName.trim();
    }
}