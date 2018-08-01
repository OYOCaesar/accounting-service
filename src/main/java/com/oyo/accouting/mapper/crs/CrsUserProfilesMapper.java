package com.oyo.accouting.mapper.crs;

import com.oyo.accouting.bean.UserProfilesDto;
import org.apache.ibatis.annotations.Mapper;


@Mapper
public interface CrsUserProfilesMapper {

    public UserProfilesDto queryUserProfilesByHotelIdAndRole(Long hotelId);

}
