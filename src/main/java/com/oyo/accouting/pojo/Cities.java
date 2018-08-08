package com.oyo.accouting.pojo;


import lombok.Getter;
import lombok.Setter;

import javax.persistence.Table;
import java.awt.*;
import java.sql.Timestamp;

@Getter
@Setter
@Table(name = "cities")
public class Cities {

  private Integer id;
  private String name;
  private String keywords;
  private Integer priority;
  private java.sql.Timestamp createdAt;
  private java.sql.Timestamp updatedAt;
  private Integer status;
  private String cityData;
  private Double tax;
  private String title;
  private String description;
  private String code;
  private Integer cityManagerId;
  private String popularLocation;
  private String imageName;
  private String metaDescription;
  private Integer countryId;
  private Integer hubId;
  private Integer cityType;
  private String timeZone;
  private Integer stateId;
  private String boundary;
  private Point centre;
  private String osmData;
  private String metadata;
  private String partnerDescription;
  private Double dealsNotificationValue;
  private String sapSync;


}
