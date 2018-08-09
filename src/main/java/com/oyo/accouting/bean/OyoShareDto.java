package com.oyo.accouting.bean;

import lombok.Getter;
import lombok.Setter;
import org.springframework.util.StringUtils;

@Getter
@Setter
public class OyoShareDto {

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

  
}