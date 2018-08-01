package com.oyo.accouting.bean;


import lombok.Getter;
import lombok.Setter;

import java.sql.Date;
import java.sql.Timestamp;

@Getter
@Setter
public class UserProfilesDto {

  private Long id;
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
  private Long role;
  private Long sex;
  private Long status;
  private String registrationId;
  private Long team;
  private Long agentId;
  private String countryCode;
  private Long currencyId;
  private Long countryId;
  private Long deviseRole;
  private String imageUrl;
  private String isRelationshipModeOn;

}