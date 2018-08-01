package com.oyo.accouting.bean;


import lombok.Getter;
import lombok.Setter;

import java.awt.*;
import java.sql.Timestamp;

/**
 * @author zfguo
 */
@Getter
@Setter
public class CitiesDto {

  private Long id;
  private String name;
  private String keywords;
  private Long priority;
  private Timestamp createdAt;
  private Timestamp updatedAt;
  private Long status;
  private String cityData;
  private Double tax;
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
