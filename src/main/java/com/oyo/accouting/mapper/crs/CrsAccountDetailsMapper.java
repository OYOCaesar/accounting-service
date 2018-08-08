package com.oyo.accouting.mapper.crs;

import com.oyo.accouting.bean.AccountDetailsDto;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface CrsAccountDetailsMapper {

    public AccountDetailsDto queryAccountDetailsByItemId(Integer itemId);

}
