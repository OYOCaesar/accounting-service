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

  private Long id;
  private String name;
  private String keywords;
  private Long priority;
  private java.sql.Timestamp createdAt;
  private java.sql.Timestamp updatedAt;
  private Long status;
  private String cityData;
  private double tax;
  private String title;
  private String description;
  private String code;
  private Long cityManagerId;
  private String popularLocation;
  private String imageName;
  private String metaDescription;
  private Long countryId;
  private Long hubId;
  private Long cityType;
  private String timeZone;
  private Long stateId;
  private String boundary;
  private Point centre;
  private String osmData;
  private String metadata;
  private String partnerDescription;
  private Double dealsNotificationValue;
  private String sapSync;


}
