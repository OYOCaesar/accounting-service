package com.oyo.accouting.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.oyo.accouting.bean.CitiesDto;
import com.oyo.accouting.bean.ZonesDto;
import com.oyo.accouting.mapper.crs.CrsCitiesMapper;

@Service
public class CrsCityService {

    @Autowired
    private CrsCitiesMapper crsCitiesMapper;

    public List<CitiesDto> queryAllChinaCities() {
        return crsCitiesMapper.queryAllChinaCities();
    }
    
    public List<ZonesDto> queryAllChinaZones() {
        return crsCitiesMapper.queryAllChinaZones();
    }

}
