package com.oyo.accouting.service;

import com.oyo.accouting.mapper.accounting.AccountingSyncHotelMapper;
import com.oyo.accouting.pojo.SyncHotel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class SyncHotelService {

    @Autowired
    private AccountingSyncHotelMapper accountingSyncHotelMapper;

    public List<SyncHotel> querySyncHotelList(SyncHotel info){
        List list = null;
        try {
            list = this.accountingSyncHotelMapper.querySyncHotelList(info);
        }catch (Exception e){
            e.printStackTrace();
        }
        return list;
    }

}
