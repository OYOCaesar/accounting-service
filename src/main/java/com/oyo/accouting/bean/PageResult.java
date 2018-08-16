package com.oyo.accouting.bean;

import java.util.List;

//page result for easyui pager
public class PageResult {

	private long total;
    private List<?> rows;

    public long getTotal() {
        return total;
    }
    public void setTotal(long total) {
        this.total = total;
    }
    public List<?> getRows() {
        return rows;
    }
    public void setRows(List<?> rows) {
        this.rows = rows;
    } 
    
}
