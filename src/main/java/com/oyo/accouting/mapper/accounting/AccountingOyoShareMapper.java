package com.oyo.accouting.mapper.accounting;

import com.oyo.accouting.bean.OyoShareDto;
import org.apache.ibatis.annotations.Mapper;

import com.oyo.accouting.pojo.OyoShare;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface AccountingOyoShareMapper extends com.github.abel533.mapper.Mapper<OyoShare> {

    public List<OyoShareDto> queryOyoShareList(@Param("oyoShare") OyoShareDto oyoShare);

    public List<OyoShareDto> queryBatchData(@Param("oyoShare") OyoShareDto oyoShare);

    public int insertOyoShareList(@Param("oyoShareList") List<OyoShare> oyoShareList);

    public int deleteByIds(@Param("ids") String ids);

}
