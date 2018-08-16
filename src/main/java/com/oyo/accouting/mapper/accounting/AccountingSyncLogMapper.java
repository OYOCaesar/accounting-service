package com.oyo.accouting.mapper.accounting;

import com.oyo.accouting.bean.SyncLogDto;
import org.apache.ibatis.annotations.Mapper;
import com.oyo.accouting.pojo.SyncLog;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface AccountingSyncLogMapper extends com.github.abel533.mapper.Mapper<SyncLog> {

    public List<SyncLogDto> querySyncList(@Param("syncLog") SyncLogDto syncLog);

}
