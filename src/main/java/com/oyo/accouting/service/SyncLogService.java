package com.oyo.accouting.service;

import com.oyo.accouting.bean.SyncLogDto;
import com.oyo.accouting.mapper.accounting.AccountingSyncLogMapper;
import com.oyo.accouting.pojo.SyncLog;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class SyncLogService {

    @Autowired
    private AccountingSyncLogMapper accountingSyncLogMapper;

    public List<SyncLog> querySyncLog(SyncLogDto syncLog){
        List list = null;
        try {
            list = this.accountingSyncLogMapper.querySyncList(syncLog);
        }catch (Exception e){
            e.printStackTrace();
        }
        return list;
    }

    public int insertSyncLog(SyncLog syncLog){
        int i = 0;
        try {
            i = this.accountingSyncLogMapper.insert(syncLog);
        }catch (Exception e){
            e.printStackTrace();
        }
        return i;
    }


    public int deleteSyncLog(SyncLog syncLog){
        if(syncLog==null)return -99;
        int i = 0;
        try {
            i = this.accountingSyncLogMapper.delete(syncLog);
        }catch (Exception e){
            e.printStackTrace();
        }
        return i;
    }
}
