package com.oyo.accouting.mapper.crs;

import com.oyo.accouting.bean.CitiesDto;
import org.apache.ibatis.annotations.Mapper;


@Mapper
public interface CrsCitiesMapper {

    public CitiesDto queryCityesById(Long id);

}
