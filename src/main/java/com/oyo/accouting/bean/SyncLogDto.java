package com.oyo.accouting.bean;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.Id;
import javax.persistence.Table;

@Getter
@Setter
public class SyncLogDto {

  private Long id;
  private Long sourceId;
  private String type;
  private Long version;
  private java.sql.Timestamp updateTime;
  private java.sql.Timestamp createTime;



}
