package com.oyo.accouting.pojo;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.Table;
import javax.persistence.Transient;
import java.sql.Date;
import java.sql.Time;
import java.sql.Timestamp;

@Getter
@Setter
@Table(name = "hotels")
public class Hotel {

  private Long id;
  private String name;
  private String alternateName;
  private String oyoId;
  private String summary;
  private String description;
  private String locationDescription;
  private String keywords;
  private Long priority;
  private String clientele;
  private String mapLink;
  private Long rating;
  private String website;
  private String metaDescription;
  private String email;
  private String phone;
  private String landmark;
  private String directions;
  private java.sql.Timestamp createdAt;
  private java.sql.Timestamp updatedAt;
  private Long status;
  private Long category;
  private Long clusterId;
  private String plotNumber;
  private String street;
  private String city;
  private String state;
  private String country;
  private String pincode;
  private double latitude;
  private double longitude;
  private String newOyo;
  private String featured;
  private Long transformedById;
  private Long mintariff;
  private String sendManagerDetails;
  private String primaryContact;
  private String tripAdvisorUrl;
  private Long operationManagerId;
  private Long hostessManagerId;
  private Long trainingManagerId;
  private Long boardType;
  private Long hoarding;
  private Long overSellableRooms;
  private String landline;
  private String managerName;
  private Long leadType;
  private java.sql.Timestamp activationDate;
  private String oyoEmail;
  private Long tabletStatus;
  private Long createdBy;
  private String onlyPrepaid;
  private Long bdManagerId;
  private Long discount;
  private Long miniClusterId;
  private String shortDescription;
  private String otaListed;
  private Long countryId;
  private Long cityId;
  private Long currencyId;
  private String holidayIqUrl;
  private java.sql.Time checkinTime;
  private java.sql.Time checkoutTime;
  private String statusTrack;
  private Long totalRooms;
  private Long hotelType;
  private String billingEntity;
  private String hotelLogo;
  private String nearbyJson;
  private java.sql.Date trainingDate;
  private java.sql.Time callingActiveTime;
  private String desiredPricingSplit;
  private Long migratedFromHotelId;
  private String flagshipTransitionCategory;
  private String prevOyoId;
  private String wifiAutoconnect;
  private Long wifiDeviceType;
  private String isOnlyMmSellable;
  private String isOnlyHomeMmSellable;
  private Long studioStaysPropertyType;
  private String authorizedImei;
  private String partnerDescription;
  private String oneLiner;
  private String smartPtaDone;
  private String noMmSellable;
  private String isLiveAtGoogle;
  private String gstnNo;
  private String shortAddress;
  private String useOyoName;
  private String displayCategory;
  private String sapSync;
  private String alcottSapSync;
  private String onBoardingDetails;
  private Long uniqueCode;
  private String atlasId;
  private String hawkEyeHotelId;
  private String hawkEyeUrl;

}