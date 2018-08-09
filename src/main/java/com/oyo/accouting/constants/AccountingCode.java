package com.oyo.accouting.constants;

/***
 * SAP会计科目代码枚举
 * @author ZhangSuYun
 * @date 2018-08-09
 */
public enum AccountingCode {
	CODE_11220201("应收账款-非关联企业", 11220201),
	CODE_22020202("应付账款-代收代付", 22020202),
	CODE_60010201("非关联企业营业收入", 60010201),
	CODE_60010401("非关联企业营业成本", 60010401),
	CODE_60010601("非关联企业营业费率", 60010601);
	
    // 成员变量
    private String value;
    private int code;

    // 构造方法
    private AccountingCode(String value, int code) {
        this.value = value;
        this.code = code;
    }

    // 普通方法
    public static String getValue(int code) {
        for (AccountingCode c : AccountingCode.values()) {
        if (c.getCode() == code) {
            return c.value;
        }
        }
        return null;
    }

	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}

	public int getCode() {
		return code;
	}

	public void setCode(int code) {
		this.code = code;
	}
    
}
