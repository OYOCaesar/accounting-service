package com.oyo.accouting.service;

import com.oyo.accouting.bean.*;
import com.oyo.accouting.mapper.crs.CrsAccountDetailsMapper;
import com.oyo.accouting.mapper.crs.CrsCitiesMapper;
import com.oyo.accouting.mapper.crs.CrsHotelMapper;
import com.oyo.accouting.mapper.crs.CrsUserProfilesMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
public class SyncHotelToSapService {

    @Autowired
    private CrsHotelMapper crsHotelMapper;

    @Autowired
    private CrsAccountDetailsMapper crsAccountDetailsMapper;

    @Autowired
    private CrsUserProfilesMapper crsUserProfilesMapper;

    @Autowired
    private CrsCitiesMapper crsCitiesMapper;

    public SyncHotel syncHotelToSap(){

        List<HotelDto> hotelList = this.crsHotelMapper.queryHotelList();

        for(HotelDto h:hotelList){
            //查询AccountDetails
            AccountDetailsDto accountDetails = crsAccountDetailsMapper.queryAccountDetailsByItemId(h.getId());
            h.setAccountDetails(accountDetails);
            //查询UserProfiles
            UserProfilesDto userProfiles = crsUserProfilesMapper.queryUserProfilesByHotelIdAndRole(h.getId());
            h.setUserProfiles(userProfiles);

            CitiesDto cities = this.crsCitiesMapper.queryCityesById(h.getCityId());
            h.setCities(cities);

            SyncHotel syncHotel = new SyncHotel();
            Map<String ,Object> syncHotemMap = syncHotel.getSyncHotelMap();
            syncHotel.setSyncHotelMap(h,syncHotemMap);
            return syncHotel;
        }
        return null;
    }
}
