/**
 * 
 */
package com.oyo.accouting.util;

import org.springframework.util.StringUtils;

import lombok.Getter;
import lombok.Setter;

/**
 * @author liujunyi
 *
 */
public enum PaymentTypeEnum {

	PREPAID("Prepaid", "0"), PAY_AT_HOTEL("Pay At Hotel", "1"), PREPAID_PAY_AT_HOTEL("Prepaid + Pay At Hotel", "2"),
	BARTER_DEAL("Barter Deal", "3");

	@Getter
	@Setter
	private String name;
	@Getter
	@Setter
	private String code;

	private PaymentTypeEnum(String name, String code) {
		this.name = name;
		this.code = code;
	}
	
	public static PaymentTypeEnum of(String name) {
		if (StringUtils.isEmpty(name)) {
			return null;
		}
		for (PaymentTypeEnum sourceDataEnum : values()) {
			if (sourceDataEnum.name.equals(name)) {
				return sourceDataEnum;
			}
		}
		return null;
	}
}
