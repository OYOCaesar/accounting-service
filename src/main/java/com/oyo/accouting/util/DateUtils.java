package com.oyo.accouting.util;

import java.text.SimpleDateFormat;
import java.util.*;

public class DateUtils {
	public static final String FORMAT_DATE = "yyyyMMdd";
	public static final String FORMAT_TIMESTAMP = "yyyy-MM-dd HH:mm:ss";


	public static String getCurrentDate() {
		return getDate(new Date());
	}

	public static String getDate(Date d) {
		return DateUtils.getCurrentDate(new Date(),FORMAT_TIMESTAMP);
	}

	public static String getCurrentDate(Date date,String aFormat) {
		String tDate = new SimpleDateFormat(aFormat).format(date);
		return tDate;
	}

}
