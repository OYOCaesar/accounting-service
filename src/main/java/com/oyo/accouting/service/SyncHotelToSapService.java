package com.oyo.accouting.service;

import com.oyo.accouting.mapper.crs.CrsHotelMapper;
import com.oyo.accouting.pojo.Hotel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class SyncHotelToSapService {

    @Autowired
    private CrsHotelMapper crsHotelMapper;

    public List<Hotel> syncHotelToSap(){

        List<Hotel> hotelList = this.crsHotelMapper.queryHotelList();
        return hotelList;
    }
}
