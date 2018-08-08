package com.oyo.accouting.bean;

import lombok.Getter;
import lombok.Setter;

import java.sql.Date;
import java.sql.Time;
import java.sql.Timestamp;
/**
 * @author zfguo
 */
@Getter
@Setter
public class HotelDto {

  private Integer id;
  private String name;
  private String alternateName;
  private String oyoId;
  private String summary;
  private String description;
  private String locationDescription;
  private String keywords;
  private Integer priority;
  private String clientele;
  private String mapLink;
  private Integer rating;
  private String website;
  private String metaDescription;
  private String email;
  private String phone;
  private String landmark;
  private String directions;
  private java.sql.Timestamp createdAt;
  private java.sql.Timestamp updatedAt;
  private Integer status;
  private Integer category;
  private Integer clusterId;
  private String plotNumber;
  private String street;
  private String city;
  private String state;
  private String country;
  private String pincode;
  private Double latitude;
  private Double longitude;
  private String newOyo;
  private String featured;
  private Integer transformedById;
  private Integer mintariff;
  private String sendManagerDetails;
  private String primaryContact;
  private String tripAdvisorUrl;
  private Integer operationManagerId;
  private Integer hostessManagerId;
  private Integer trainingManagerId;
  private Integer boardType;
  private Integer hoarding;
  private Integer overSellableRooms;
  private String landline;
  private String managerName;
  private Integer leadType;
  private java.sql.Timestamp activationDate;
  private String oyoEmail;
  private Integer tabletStatus;
  private Integer createdBy;
  private String onlyPrepaid;
  private Integer bdManagerId;
  private Integer discount;
  private Integer miniClusterId;
  private String shortDescription;
  private String otaListed;
  private Integer countryId;
  private Integer cityId;
  private Integer currencyId;
  private String holidayIqUrl;
  private java.sql.Time checkinTime;
  private java.sql.Time checkoutTime;
  private String statusTrack;
  private Integer totalRooms;
  private Integer hotelType;
  private String billingEntity;
  private String hotelLogo;
  private String nearbyJson;
  private java.sql.Date trainingDate;
  private java.sql.Time callingActiveTime;
  private String desiredPricingSplit;
  private Integer migratedFromHotelId;
  private String flagshipTransitionCategory;
  private String prevOyoId;
  private String wifiAutoconnect;
  private Integer wifiDeviceType;
  private String isOnlyMmSellable;
  private String isOnlyHomeMmSellable;
  private Integer studioStaysPropertyType;
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
  private Integer uniqueCode;
  private String atlasId;
  private String hawkEyeHotelId;
  private String hawkEyeUrl;

  private AccountDetailsDto accountDetails;

  private UserProfilesDto userProfiles;

  private CitiesDto cities;

  private String oyoShare;

}