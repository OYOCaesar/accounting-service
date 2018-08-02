package com.oyo.accouting.pojo;


import lombok.Getter;
import lombok.Setter;

import javax.naming.Name;
import javax.persistence.Table;
import java.sql.Date;
import java.sql.Timestamp;

@Getter
@Setter
@Table(name = "user_profiles")
public class UserProfiles {

  private Long id;
  private String firstName;
  private String lastName;
  private String phone;
  private String email;
  private String address;
  private String city;
  private String state;
  private java.sql.Date dateOfBirth;
  private java.sql.Timestamp createdAt;
  private java.sql.Timestamp updatedAt;
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