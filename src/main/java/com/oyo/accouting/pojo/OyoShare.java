package com.oyo.accouting.pojo;

import lombok.Getter;
import lombok.Setter;
import org.springframework.util.StringUtils;

import javax.persistence.Table;

@Getter
@Setter
@Table(name = "oyo_share")
public class OyoShare {

  private Integer id;
  private String hotelId;
  private String oyoShare;
  private String status;
  private String uniqueCode;
  private String oyoId;
  private String hotelName;
  private String city;
  private String zoneName;
  private String fixedRate;

  private String isTest;

}