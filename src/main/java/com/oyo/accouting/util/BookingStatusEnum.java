/**
 * 
 */
package com.oyo.accouting.util;

import org.springframework.util.StringUtils;

import lombok.Getter;
import lombok.Setter;

/**
 * @author EDZ
 *
 */
public enum BookingStatusEnum {

	CONFIRM_BOOKING("Confirm Booking", "0"), CHECKED_IN("Checked In", "1"), CHECKED_OUT("Checked Out", "2"),
	CANCELLED_BOOKING("Cancelled Booking", "3"), NO_SHOW("No Show", "4"), OWNER_BOOKING("Owner Booking", "5"),
	BLOCKED("Blocked", "6"), HOLD("Hold", "7"), UNPROCESSED("Unprocessed", "8"),
	NEED_MANUAL_INTERVENTION("Need Manual Intervention", "9"), NEED_MANUAL_OVERBOOKING("Need Manual Overbooking", "10"),
	NEED_MANUAL_BULK("Need Manual Bulk", "11"), SAVED("Saved", "12"), VOID_BOOKING("Void Booking", "13");

	@Getter
	@Setter
	private String name;
	@Getter
	@Setter
	private String code;

	private BookingStatusEnum(String name, String code) {
		this.name = name;
		this.code = code;
	}
	
	public static BookingStatusEnum of(String name) {
		if (StringUtils.isEmpty(name)) {
			return null;
		}
		for (BookingStatusEnum sourceDataEnum : values()) {
			if (sourceDataEnum.name.equals(name)) {
				return sourceDataEnum;
			}
		}
		return null;
	}
}
