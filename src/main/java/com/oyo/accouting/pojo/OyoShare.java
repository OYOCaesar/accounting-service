package com.oyo.accouting.pojo;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.Table;

@Getter
@Setter
@Table(name = "oyo_share")
public class OyoShare {

  private Integer id;
  private Integer hotelId;
  private String oyoShare;

  
}
