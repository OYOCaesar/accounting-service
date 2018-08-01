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
  private Long id;
  private Long sourceId;
  private String type;
  private Long version;
  private java.sql.Timestamp updateTime;
  private java.sql.Timestamp createTime;



}
