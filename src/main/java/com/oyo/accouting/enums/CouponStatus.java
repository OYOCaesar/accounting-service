package com.oyo.accouting.enums;

public enum CouponStatus {
        UNUSED(0),
        USED(1),
        EXPIRED(2),
        DISABLE(-1);
        Integer status;

        public Integer getStatus() {
            return status;
        }

        public void setStatus(Integer status) {
            this.status = status;
        }

        CouponStatus(Integer status) {
            this.status = status;
        }
}
