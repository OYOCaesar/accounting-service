package com.oyo.accouting.pojo;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

public class Deductions implements Serializable {
    private Integer id;

    private Integer hotelId;

    private BigDecimal tempMemPromotionFee;

    private BigDecimal praisePlatformPromotionFee;

    private BigDecimal flyingPigsPlatformPromotionFee;

    private BigDecimal newActivityA;

    private BigDecimal newActivityB;

    private BigDecimal newActivityC;

    private BigDecimal currentMonthReceivedOyoCommission;

    private Date createTime;

    private static final long serialVersionUID = 1L;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getHotelId() {
        return hotelId;
    }

    public void setHotelId(Integer hotelId) {
        this.hotelId = hotelId;
    }

    public BigDecimal getTempMemPromotionFee() {
        return tempMemPromotionFee;
    }

    public void setTempMemPromotionFee(BigDecimal tempMemPromotionFee) {
        this.tempMemPromotionFee = tempMemPromotionFee;
    }

    public BigDecimal getPraisePlatformPromotionFee() {
        return praisePlatformPromotionFee;
    }

    public void setPraisePlatformPromotionFee(BigDecimal praisePlatformPromotionFee) {
        this.praisePlatformPromotionFee = praisePlatformPromotionFee;
    }

    public BigDecimal getFlyingPigsPlatformPromotionFee() {
        return flyingPigsPlatformPromotionFee;
    }

    public void setFlyingPigsPlatformPromotionFee(BigDecimal flyingPigsPlatformPromotionFee) {
        this.flyingPigsPlatformPromotionFee = flyingPigsPlatformPromotionFee;
    }

    public BigDecimal getNewActivityA() {
        return newActivityA;
    }

    public void setNewActivityA(BigDecimal newActivityA) {
        this.newActivityA = newActivityA;
    }

    public BigDecimal getNewActivityB() {
        return newActivityB;
    }

    public void setNewActivityB(BigDecimal newActivityB) {
        this.newActivityB = newActivityB;
    }

    public BigDecimal getNewActivityC() {
        return newActivityC;
    }

    public void setNewActivityC(BigDecimal newActivityC) {
        this.newActivityC = newActivityC;
    }

    public BigDecimal getCurrentMonthReceivedOyoCommission() {
        return currentMonthReceivedOyoCommission;
    }

    public void setCurrentMonthReceivedOyoCommission(BigDecimal currentMonthReceivedOyoCommission) {
        this.currentMonthReceivedOyoCommission = currentMonthReceivedOyoCommission;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }
}