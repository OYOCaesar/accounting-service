package com.oyo.accouting.bean;


import lombok.Getter;
import lombok.Setter;

import java.sql.Date;
import java.sql.Timestamp;

/**
 * @author zfguo
 */
@Getter
@Setter
public class UserProfilesDto {

  private Integer id;
  private String firstName;
  private String lastName;
  private String phone;
  private String email;
  private String address;
  private String city;
  private String state;
  private Date dateOfBirth;
  private Timestamp createdAt;
  private Timestamp updatedAt;
  private Integer role;
  private Integer sex;
  private Integer status;
  private String registrationId;
  private Integer team;
  private Integer agentId;
  private String countryCode;
  private Integer currencyId;
  private Integer countryId;
  private Integer deviseRole;
  private String imageUrl;
  private String isRelationshipModeOn;

}