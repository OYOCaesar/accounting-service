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

  private Integer id;
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