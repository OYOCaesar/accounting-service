package com.oyo.accouting.mapper.crs;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;

import com.oyo.accouting.bean.CitiesDto;
import com.oyo.accouting.bean.ZonesDto;


@Mapper
public interface CrsCitiesMapper {

    public CitiesDto queryCityesById(Integer id);
    
    //从CRS中获取所有中国城市
    public List<CitiesDto> queryAllChinaCities();
    
    //从CRS中获取所有中国区域
    public List<ZonesDto> queryAllChinaZones();

}
