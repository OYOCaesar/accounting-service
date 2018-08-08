package com.oyo.accouting.pojo;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.Id;
import javax.persistence.Table;

@Getter
@Setter
@Table(name = "sync_log")
public class SyncLog {

  @Id
  private Integer id;
  private Integer sourceId;
  private String type;
  private Integer version;
  private java.sql.Timestamp sourceUpdateTime;
  private java.sql.Timestamp createTime;
  private Integer status;
  private String jsonData;


}
