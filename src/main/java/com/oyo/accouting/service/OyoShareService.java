package com.oyo.accouting.service;

import com.oyo.accouting.bean.OyoShareDto;
import com.oyo.accouting.mapper.accounting.AccountingOyoShareMapper;
import com.oyo.accouting.mapper.accounting.AccountingSyncLogMapper;
import com.oyo.accouting.pojo.OyoShare;
import com.oyo.accouting.pojo.SyncLog;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class OyoShareService {

    @Autowired
    private AccountingOyoShareMapper accountingOyoShareMapper;

    public List<OyoShareDto> queryOyoShare(OyoShareDto info){
        List list = null;
        try {
            list = this.accountingOyoShareMapper.queryOyoShareList(info);
        }catch (Exception e){
            e.printStackTrace();
        }
        return list;
    }

    public List<OyoShareDto> queryBatchData(OyoShareDto info){
        List list = null;
        try {
            list = this.accountingOyoShareMapper.queryBatchData(info);
        }catch (Exception e){
            e.printStackTrace();
        }
        return list;
    }

    public int insertOyoShare(OyoShare info){
        int i = 0;
        try {
            i = this.accountingOyoShareMapper.insert(info);
        }catch (Exception e){
            e.printStackTrace();
        }
        return i;
    }


    public int deleteOyoShare(OyoShare info){
        if(info==null)return -99;
        int i = 0;
        try {
            i = this.accountingOyoShareMapper.delete(info);
        }catch (Exception e){
            e.printStackTrace();
        }
        return i;
    }

    public int updateOyoShare(OyoShare info){
        int i = 0;
        try {
            OyoShareDto example =  new OyoShareDto();
            example.setHotelId(info.getHotelId());
            i = this.accountingOyoShareMapper.updateByExampleSelective(info,example);
        }catch (Exception e){
            e.printStackTrace();
        }
        return i;
    }

    public int insertOyoShareList(List<OyoShare> oyoSharesList){
        if(oyoSharesList==null || oyoSharesList.size() == 0)return -99;
        int i = 0;
        try {
            i = this.accountingOyoShareMapper.insertOyoShareList(oyoSharesList);
        }catch (Exception e){
            e.printStackTrace();
        }
        return i;
    }

    public int deleteByIds(String ids){
        if(StringUtils.isEmpty(ids)){
            return -1;
        }
        int i =0;
        try{
            i = this.accountingOyoShareMapper.deleteByIds(ids);
        }catch (Exception e){
            e.printStackTrace();
        }
        return i;
    }
}
