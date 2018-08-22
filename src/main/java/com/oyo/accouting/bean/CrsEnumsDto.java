package com.oyo.accouting.bean;

import lombok.Getter;
import lombok.Setter;

/***
 *  CRS枚举对象
 * @author ZhangSuYun
 * @date 2018-08-22
 */
@Getter
@Setter
public class CrsEnumsDto {
    
	private Integer id;// id
	private String tableName;// 表名
	private String columnName; //列名
	private Integer enumKey;// key
	private String enumVal;// value
    
}
