package com.oyo.accouting.enums;

public enum CouponMetadataStatus {

        READY(0),
        ONLINE(1),
        OFFLINE(2);

        Integer status;

        public Integer getStatus() {
            return status;
        }

        public void setStatus(Integer status) {
            this.status = status;
        }

        CouponMetadataStatus(Integer status) {
            this.status = status;
        }
}
