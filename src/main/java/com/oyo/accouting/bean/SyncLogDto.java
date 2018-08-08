package com.oyo.accouting.bean;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.Id;
import javax.persistence.Table;

@Getter
@Setter
public class SyncLogDto {

  private Integer id;
  private Integer sourceId;
  private String type;
  private Integer version;
  private java.sql.Timestamp sourceUpdateTime;
  private java.sql.Timestamp createTime;
  private Integer status;
  private String jsonData;


}
