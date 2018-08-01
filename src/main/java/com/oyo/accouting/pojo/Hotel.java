package com.oyo.accouting.pojo;

import javax.persistence.Table;
import javax.persistence.Transient;
import java.sql.Date;
import java.sql.Time;
import java.sql.Timestamp;

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


  public Long getId() {
    return id;
  }

  public void setId(Long id) {
    this.id = id;
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public String getAlternateName() {
    return alternateName;
  }

  public void setAlternateName(String alternateName) {
    this.alternateName = alternateName;
  }

  public String getOyoId() {
    return oyoId;
  }

  public void setOyoId(String oyoId) {
    this.oyoId = oyoId;
  }

  public String getSummary() {
    return summary;
  }

  public void setSummary(String summary) {
    this.summary = summary;
  }

  public String getDescription() {
    return description;
  }

  public void setDescription(String description) {
    this.description = description;
  }

  public String getLocationDescription() {
    return locationDescription;
  }

  public void setLocationDescription(String locationDescription) {
    this.locationDescription = locationDescription;
  }

  public String getKeywords() {
    return keywords;
  }

  public void setKeywords(String keywords) {
    this.keywords = keywords;
  }

  public Long getPriority() {
    return priority;
  }

  public void setPriority(Long priority) {
    this.priority = priority;
  }

  public String getClientele() {
    return clientele;
  }

  public void setClientele(String clientele) {
    this.clientele = clientele;
  }

  public String getMapLink() {
    return mapLink;
  }

  public void setMapLink(String mapLink) {
    this.mapLink = mapLink;
  }

  public Long getRating() {
    return rating;
  }

  public void setRating(Long rating) {
    this.rating = rating;
  }

  public String getWebsite() {
    return website;
  }

  public void setWebsite(String website) {
    this.website = website;
  }

  public String getMetaDescription() {
    return metaDescription;
  }

  public void setMetaDescription(String metaDescription) {
    this.metaDescription = metaDescription;
  }

  public String getEmail() {
    return email;
  }

  public void setEmail(String email) {
    this.email = email;
  }

  public String getPhone() {
    return phone;
  }

  public void setPhone(String phone) {
    this.phone = phone;
  }

  public String getLandmark() {
    return landmark;
  }

  public void setLandmark(String landmark) {
    this.landmark = landmark;
  }

  public String getDirections() {
    return directions;
  }

  public void setDirections(String directions) {
    this.directions = directions;
  }

  public Timestamp getCreatedAt() {
    return createdAt;
  }

  public void setCreatedAt(Timestamp createdAt) {
    this.createdAt = createdAt;
  }

  public Timestamp getUpdatedAt() {
    return updatedAt;
  }

  public void setUpdatedAt(Timestamp updatedAt) {
    this.updatedAt = updatedAt;
  }

  public Long getStatus() {
    return status;
  }

  public void setStatus(Long status) {
    this.status = status;
  }

  public Long getCategory() {
    return category;
  }

  public void setCategory(Long category) {
    this.category = category;
  }

  public Long getClusterId() {
    return clusterId;
  }

  public void setClusterId(Long clusterId) {
    this.clusterId = clusterId;
  }

  public String getPlotNumber() {
    return plotNumber;
  }

  public void setPlotNumber(String plotNumber) {
    this.plotNumber = plotNumber;
  }

  public String getStreet() {
    return street;
  }

  public void setStreet(String street) {
    this.street = street;
  }

  public String getCity() {
    return city;
  }

  public void setCity(String city) {
    this.city = city;
  }

  public String getState() {
    return state;
  }

  public void setState(String state) {
    this.state = state;
  }

  public String getCountry() {
    return country;
  }

  public void setCountry(String country) {
    this.country = country;
  }

  public String getPincode() {
    return pincode;
  }

  public void setPincode(String pincode) {
    this.pincode = pincode;
  }

  public double getLatitude() {
    return latitude;
  }

  public void setLatitude(double latitude) {
    this.latitude = latitude;
  }

  public double getLongitude() {
    return longitude;
  }

  public void setLongitude(double longitude) {
    this.longitude = longitude;
  }

  public String getNewOyo() {
    return newOyo;
  }

  public void setNewOyo(String newOyo) {
    this.newOyo = newOyo;
  }

  public String getFeatured() {
    return featured;
  }

  public void setFeatured(String featured) {
    this.featured = featured;
  }

  public Long getTransformedById() {
    return transformedById;
  }

  public void setTransformedById(Long transformedById) {
    this.transformedById = transformedById;
  }

  public Long getMintariff() {
    return mintariff;
  }

  public void setMintariff(Long mintariff) {
    this.mintariff = mintariff;
  }

  public String getSendManagerDetails() {
    return sendManagerDetails;
  }

  public void setSendManagerDetails(String sendManagerDetails) {
    this.sendManagerDetails = sendManagerDetails;
  }

  public String getPrimaryContact() {
    return primaryContact;
  }

  public void setPrimaryContact(String primaryContact) {
    this.primaryContact = primaryContact;
  }

  public String getTripAdvisorUrl() {
    return tripAdvisorUrl;
  }

  public void setTripAdvisorUrl(String tripAdvisorUrl) {
    this.tripAdvisorUrl = tripAdvisorUrl;
  }

  public Long getOperationManagerId() {
    return operationManagerId;
  }

  public void setOperationManagerId(Long operationManagerId) {
    this.operationManagerId = operationManagerId;
  }

  public Long getHostessManagerId() {
    return hostessManagerId;
  }

  public void setHostessManagerId(Long hostessManagerId) {
    this.hostessManagerId = hostessManagerId;
  }

  public Long getTrainingManagerId() {
    return trainingManagerId;
  }

  public void setTrainingManagerId(Long trainingManagerId) {
    this.trainingManagerId = trainingManagerId;
  }

  public Long getBoardType() {
    return boardType;
  }

  public void setBoardType(Long boardType) {
    this.boardType = boardType;
  }

  public Long getHoarding() {
    return hoarding;
  }

  public void setHoarding(Long hoarding) {
    this.hoarding = hoarding;
  }

  public Long getOverSellableRooms() {
    return overSellableRooms;
  }

  public void setOverSellableRooms(Long overSellableRooms) {
    this.overSellableRooms = overSellableRooms;
  }

  public String getLandline() {
    return landline;
  }

  public void setLandline(String landline) {
    this.landline = landline;
  }

  public String getManagerName() {
    return managerName;
  }

  public void setManagerName(String managerName) {
    this.managerName = managerName;
  }

  public Long getLeadType() {
    return leadType;
  }

  public void setLeadType(Long leadType) {
    this.leadType = leadType;
  }

  public Timestamp getActivationDate() {
    return activationDate;
  }

  public void setActivationDate(Timestamp activationDate) {
    this.activationDate = activationDate;
  }

  public String getOyoEmail() {
    return oyoEmail;
  }

  public void setOyoEmail(String oyoEmail) {
    this.oyoEmail = oyoEmail;
  }

  public Long getTabletStatus() {
    return tabletStatus;
  }

  public void setTabletStatus(Long tabletStatus) {
    this.tabletStatus = tabletStatus;
  }

  public Long getCreatedBy() {
    return createdBy;
  }

  public void setCreatedBy(Long createdBy) {
    this.createdBy = createdBy;
  }

  public String getOnlyPrepaid() {
    return onlyPrepaid;
  }

  public void setOnlyPrepaid(String onlyPrepaid) {
    this.onlyPrepaid = onlyPrepaid;
  }

  public Long getBdManagerId() {
    return bdManagerId;
  }

  public void setBdManagerId(Long bdManagerId) {
    this.bdManagerId = bdManagerId;
  }

  public Long getDiscount() {
    return discount;
  }

  public void setDiscount(Long discount) {
    this.discount = discount;
  }

  public Long getMiniClusterId() {
    return miniClusterId;
  }

  public void setMiniClusterId(Long miniClusterId) {
    this.miniClusterId = miniClusterId;
  }

  public String getShortDescription() {
    return shortDescription;
  }

  public void setShortDescription(String shortDescription) {
    this.shortDescription = shortDescription;
  }

  public String getOtaListed() {
    return otaListed;
  }

  public void setOtaListed(String otaListed) {
    this.otaListed = otaListed;
  }

  public Long getCountryId() {
    return countryId;
  }

  public void setCountryId(Long countryId) {
    this.countryId = countryId;
  }

  public Long getCityId() {
    return cityId;
  }

  public void setCityId(Long cityId) {
    this.cityId = cityId;
  }

  public Long getCurrencyId() {
    return currencyId;
  }

  public void setCurrencyId(Long currencyId) {
    this.currencyId = currencyId;
  }

  public String getHolidayIqUrl() {
    return holidayIqUrl;
  }

  public void setHolidayIqUrl(String holidayIqUrl) {
    this.holidayIqUrl = holidayIqUrl;
  }

  public Time getCheckinTime() {
    return checkinTime;
  }

  public void setCheckinTime(Time checkinTime) {
    this.checkinTime = checkinTime;
  }

  public Time getCheckoutTime() {
    return checkoutTime;
  }

  public void setCheckoutTime(Time checkoutTime) {
    this.checkoutTime = checkoutTime;
  }

  public String getStatusTrack() {
    return statusTrack;
  }

  public void setStatusTrack(String statusTrack) {
    this.statusTrack = statusTrack;
  }

  public Long getTotalRooms() {
    return totalRooms;
  }

  public void setTotalRooms(Long totalRooms) {
    this.totalRooms = totalRooms;
  }

  public Long getHotelType() {
    return hotelType;
  }

  public void setHotelType(Long hotelType) {
    this.hotelType = hotelType;
  }

  public String getBillingEntity() {
    return billingEntity;
  }

  public void setBillingEntity(String billingEntity) {
    this.billingEntity = billingEntity;
  }

  public String getHotelLogo() {
    return hotelLogo;
  }

  public void setHotelLogo(String hotelLogo) {
    this.hotelLogo = hotelLogo;
  }

  public String getNearbyJson() {
    return nearbyJson;
  }

  public void setNearbyJson(String nearbyJson) {
    this.nearbyJson = nearbyJson;
  }

  public Date getTrainingDate() {
    return trainingDate;
  }

  public void setTrainingDate(Date trainingDate) {
    this.trainingDate = trainingDate;
  }

  public Time getCallingActiveTime() {
    return callingActiveTime;
  }

  public void setCallingActiveTime(Time callingActiveTime) {
    this.callingActiveTime = callingActiveTime;
  }

  public String getDesiredPricingSplit() {
    return desiredPricingSplit;
  }

  public void setDesiredPricingSplit(String desiredPricingSplit) {
    this.desiredPricingSplit = desiredPricingSplit;
  }

  public Long getMigratedFromHotelId() {
    return migratedFromHotelId;
  }

  public void setMigratedFromHotelId(Long migratedFromHotelId) {
    this.migratedFromHotelId = migratedFromHotelId;
  }

  public String getFlagshipTransitionCategory() {
    return flagshipTransitionCategory;
  }

  public void setFlagshipTransitionCategory(String flagshipTransitionCategory) {
    this.flagshipTransitionCategory = flagshipTransitionCategory;
  }

  public String getPrevOyoId() {
    return prevOyoId;
  }

  public void setPrevOyoId(String prevOyoId) {
    this.prevOyoId = prevOyoId;
  }

  public String getWifiAutoconnect() {
    return wifiAutoconnect;
  }

  public void setWifiAutoconnect(String wifiAutoconnect) {
    this.wifiAutoconnect = wifiAutoconnect;
  }

  public Long getWifiDeviceType() {
    return wifiDeviceType;
  }

  public void setWifiDeviceType(Long wifiDeviceType) {
    this.wifiDeviceType = wifiDeviceType;
  }

  public String getIsOnlyMmSellable() {
    return isOnlyMmSellable;
  }

  public void setIsOnlyMmSellable(String isOnlyMmSellable) {
    this.isOnlyMmSellable = isOnlyMmSellable;
  }

  public String getIsOnlyHomeMmSellable() {
    return isOnlyHomeMmSellable;
  }

  public void setIsOnlyHomeMmSellable(String isOnlyHomeMmSellable) {
    this.isOnlyHomeMmSellable = isOnlyHomeMmSellable;
  }

  public Long getStudioStaysPropertyType() {
    return studioStaysPropertyType;
  }

  public void setStudioStaysPropertyType(Long studioStaysPropertyType) {
    this.studioStaysPropertyType = studioStaysPropertyType;
  }

  public String getAuthorizedImei() {
    return authorizedImei;
  }

  public void setAuthorizedImei(String authorizedImei) {
    this.authorizedImei = authorizedImei;
  }

  public String getPartnerDescription() {
    return partnerDescription;
  }

  public void setPartnerDescription(String partnerDescription) {
    this.partnerDescription = partnerDescription;
  }

  public String getOneLiner() {
    return oneLiner;
  }

  public void setOneLiner(String oneLiner) {
    this.oneLiner = oneLiner;
  }

  public String getSmartPtaDone() {
    return smartPtaDone;
  }

  public void setSmartPtaDone(String smartPtaDone) {
    this.smartPtaDone = smartPtaDone;
  }

  public String getNoMmSellable() {
    return noMmSellable;
  }

  public void setNoMmSellable(String noMmSellable) {
    this.noMmSellable = noMmSellable;
  }

  public String getIsLiveAtGoogle() {
    return isLiveAtGoogle;
  }

  public void setIsLiveAtGoogle(String isLiveAtGoogle) {
    this.isLiveAtGoogle = isLiveAtGoogle;
  }

  public String getGstnNo() {
    return gstnNo;
  }

  public void setGstnNo(String gstnNo) {
    this.gstnNo = gstnNo;
  }

  public String getShortAddress() {
    return shortAddress;
  }

  public void setShortAddress(String shortAddress) {
    this.shortAddress = shortAddress;
  }

  public String getUseOyoName() {
    return useOyoName;
  }

  public void setUseOyoName(String useOyoName) {
    this.useOyoName = useOyoName;
  }

  public String getDisplayCategory() {
    return displayCategory;
  }

  public void setDisplayCategory(String displayCategory) {
    this.displayCategory = displayCategory;
  }

  public String getSapSync() {
    return sapSync;
  }

  public void setSapSync(String sapSync) {
    this.sapSync = sapSync;
  }

  public String getAlcottSapSync() {
    return alcottSapSync;
  }

  public void setAlcottSapSync(String alcottSapSync) {
    this.alcottSapSync = alcottSapSync;
  }

  public String getOnBoardingDetails() {
    return onBoardingDetails;
  }

  public void setOnBoardingDetails(String onBoardingDetails) {
    this.onBoardingDetails = onBoardingDetails;
  }

  public Long getUniqueCode() {
    return uniqueCode;
  }

  public void setUniqueCode(Long uniqueCode) {
    this.uniqueCode = uniqueCode;
  }

  public String getAtlasId() {
    return atlasId;
  }

  public void setAtlasId(String atlasId) {
    this.atlasId = atlasId;
  }

  public String getHawkEyeHotelId() {
    return hawkEyeHotelId;
  }

  public void setHawkEyeHotelId(String hawkEyeHotelId) {
    this.hawkEyeHotelId = hawkEyeHotelId;
  }

  public String getHawkEyeUrl() {
    return hawkEyeUrl;
  }

  public void setHawkEyeUrl(String hawkEyeUrl) {
    this.hawkEyeUrl = hawkEyeUrl;
  }


}