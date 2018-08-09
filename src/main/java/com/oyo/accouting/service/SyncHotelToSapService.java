package com.oyo.accouting.service;

import com.oyo.accouting.bean.*;
import com.oyo.accouting.mapper.accounting.AccountingOyoShareMapper;
import com.oyo.accouting.mapper.accounting.AccountingSyncLogMapper;
import com.oyo.accouting.mapper.crs.CrsAccountDetailsMapper;
import com.oyo.accouting.mapper.crs.CrsCitiesMapper;
import com.oyo.accouting.mapper.crs.CrsHotelMapper;
import com.oyo.accouting.mapper.crs.CrsUserProfilesMapper;

import com.oyo.accouting.pojo.OyoShare;
import com.oyo.accouting.pojo.SyncLog;
import com.oyo.accouting.webservice.SAPWebServiceSoap;
import net.sf.json.JSONObject;
import org.apache.cxf.jaxws.JaxWsProxyFactoryBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * @author zfguo
 */
@Service
public class SyncHotelToSapService {

    @Autowired
    private CrsHotelMapper crsHotelMapper;

    @Autowired
    private CrsAccountDetailsMapper crsAccountDetailsMapper;

    @Autowired
    private CrsUserProfilesMapper crsUserProfilesMapper;

    @Autowired
    private CrsCitiesMapper crsCitiesMapper;

    @Autowired
    private AccountingSyncLogMapper accountingSyncLogMapper;

    @Autowired
    private AccountingOyoShareMapper accountingOyoShareMapper;

    public String syncHotelToSap(){

        List<HotelDto> hotelList = this.crsHotelMapper.queryHotelList();

        JaxWsProxyFactoryBean jwpfb = new JaxWsProxyFactoryBean();
        jwpfb.setServiceClass(SAPWebServiceSoap.class);
        jwpfb.setAddress("http://52.80.99.224:8080/SAPWebService.asmx");
        SAPWebServiceSoap s = (SAPWebServiceSoap) jwpfb.create();

        for(HotelDto h:hotelList){
            //查询AccountDetails
            AccountDetailsDto accountDetails = crsAccountDetailsMapper.queryAccountDetailsByItemId(h.getId());
            h.setAccountDetails(accountDetails);
            //查询UserProfiles
            UserProfilesDto userProfiles = crsUserProfilesMapper.queryUserProfilesByHotelIdAndRole(h.getId());
            h.setUserProfiles(userProfiles);
            //查询Cities
            CitiesDto cities = this.crsCitiesMapper.queryCityesById(h.getCityId());
            h.setCities(cities);

            //初始化SyncHotel
            SyncHotel syncHotel = new SyncHotel();
            //处理业务逻辑
            Map<String ,Object> syncHotemMap = syncHotel.getSyncHotelMap();
            syncHotel.setSyncHotelMap(h,syncHotemMap);

            //查询同步日志，判断是否需要同步
            SyncLog syncLogSearch = new SyncLog();
            syncLogSearch.setSourceId(h.getId());
            syncLogSearch.setType("Hotel");
            List<SyncLogDto> syncLogDtoList = this.accountingSyncLogMapper.querySyncList(syncLogSearch);

            boolean syncLogDtoListIsNull = syncLogDtoList==null || syncLogDtoList.size()== 0;  //同步日志是否为null
            SyncLogDto syncLogDto = null;
            if(!syncLogDtoListIsNull)syncLogDto = syncLogDtoList.get(0);   //不为null
            //判断是否需要同步
            boolean isSync = syncLogDtoListIsNull || (!syncLogDtoListIsNull && h.getUpdatedAt().equals(syncLogDto.getSourceUpdateTime()));
            //同步sap
            if(isSync){
                //同步到sap
                JSONObject hotelMapJson = JSONObject.fromObject(syncHotel.getSyncHotelMap());
                String hotelMapStr = hotelMapJson.toString();
                String result = s.businessPartners(hotelMapStr);
                
                JSONObject resultJsonObj = JSONObject.fromObject(result);

                //插入日志
                SyncLog sl = new SyncLog();
                sl.setSourceId(h.getId());
                sl.setCreateTime(new Timestamp(new Date().getTime()));
                sl.setSourceUpdateTime(h.getUpdatedAt());
                sl.setType("Hotel");
                sl.setVersion(syncLogDtoListIsNull?1:syncLogDto.getVersion()+1);
                sl.setJsonData(hotelMapStr);
                sl.setStatus(Integer.valueOf(resultJsonObj.get("Code").toString()));
                this.accountingSyncLogMapper.insert(sl);
                return result;
            }
        }
        return null;
    }
}
