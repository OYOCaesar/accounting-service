package com.oyo.accouting.service;

import com.oyo.accouting.bean.*;
import com.oyo.accouting.mapper.accounting.AccountingOyoShareMapper;
import com.oyo.accouting.mapper.accounting.AccountingSyncHotelMapper;
import com.oyo.accouting.mapper.accounting.AccountingSyncLogMapper;
import com.oyo.accouting.mapper.crs.CrsAccountDetailsMapper;
import com.oyo.accouting.mapper.crs.CrsCitiesMapper;
import com.oyo.accouting.mapper.crs.CrsHotelMapper;
import com.oyo.accouting.mapper.crs.CrsUserProfilesMapper;

import com.oyo.accouting.pojo.SyncLog;
import com.oyo.accouting.webservice.SAPWebServiceSoap;
import net.sf.json.JSONObject;
import org.apache.cxf.jaxws.JaxWsProxyFactoryBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;
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
    private AccountingSyncHotelMapper accountingSyncHotelMapper;


    @Autowired
    private AccountingOyoShareMapper accountingOyoShareMapper;

    public String syncHotelToSap(HotelDto searchHotel) {

        //SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String batch = String.valueOf(new Date().getTime());//sdf.format(new Date());

        int sCount = 0, fCount = 0;//成功，失败计数

        //查询需要同步的酒店数据
        if (searchHotel == null) {
            searchHotel = new HotelDto();
        }
        searchHotel.setCountry("China");
        List<HotelDto> hotelList = this.crsHotelMapper.queryHotelList(searchHotel);

        JaxWsProxyFactoryBean jwpfb = new JaxWsProxyFactoryBean();
        jwpfb.setServiceClass(SAPWebServiceSoap.class);
        jwpfb.setAddress("http://52.80.99.224:8080/SAPWebService.asmx");
        SAPWebServiceSoap s = (SAPWebServiceSoap) jwpfb.create();

        for (HotelDto h : hotelList) {

            //查询同步日志，判断是否需要同步
            SyncLogDto syncLogSearch = new SyncLogDto();
            syncLogSearch.setSourceId(h.getId());
            syncLogSearch.setStatus(0);//同步成功的日志
            syncLogSearch.setType("Hotel");
            List<SyncLogDto> syncLogDtoList = this.accountingSyncLogMapper.querySyncList(syncLogSearch);

            boolean syncLogDtoListIsNull = syncLogDtoList == null || syncLogDtoList.size() == 0;  //同步日志是否为null
            SyncLogDto syncLogDto = null;
            if (!syncLogDtoListIsNull) syncLogDto = syncLogDtoList.get(0);   //不为null
            //判断是否需要同步
            boolean isSync = syncLogDtoListIsNull || (!syncLogDtoListIsNull && syncLogDto != null && (h.getUpdatedAt().getTime() - syncLogDto.getSourceUpdateTime().getTime() > 60000));
            //需要同步sap
            if (isSync) {
                //1 准备数据
                //查询AccountDetails
                //AccountDetailsDto accountDetails = crsAccountDetailsMapper.queryAccountDetailsByItemId(h.getId());
                //h.setAccountDetails(accountDetails);
                //查询UserProfiles
                UserProfilesDto userProfiles = crsUserProfilesMapper.queryUserProfilesByHotelIdAndRole(h.getId());
                h.setUserProfiles(userProfiles);

                //初始化SyncHotel
                SyncHotel syncHotel = new SyncHotel();
                //处理业务逻辑
                Map<String, Object> syncHotemMap = syncHotel.getSyncHotelMap();
                //客户
                syncHotel.setSyncHotelMapC(h, syncHotemMap);

                //2 同步到sap
                boolean successC = syncHotelMethod(h,syncHotel,s,batch,syncLogDto);
                //商家
                syncHotel.setSyncHotelMapV(h, syncHotemMap);
                // 同步到sap
                boolean successV = syncHotelMethod(h,syncHotel,s,batch,syncLogDto);

                if(successC && successV){
                    sCount++;
                }else{
                    fCount++;
                }
            }
        }
        return "成功" + sCount + "条，失败" + fCount + "条";
    }


    private boolean syncHotelMethod(HotelDto h,SyncHotel syncHotel,SAPWebServiceSoap s,String batch,SyncLogDto syncLogDto){

        boolean success = false;
        Map syncHotelMap = syncHotel.getSyncHotelMap();
        JSONObject hotelMapJson = JSONObject.fromObject(syncHotelMap);
        String hotelMapStr = hotelMapJson.toString();
        String result = s.businessPartners(hotelMapStr);

        JSONObject resultJsonObj = JSONObject.fromObject(result);

        //3
        if(Integer.valueOf(resultJsonObj.get("Code").toString().trim())==0){ //同步成功

            //4 插入同步的数据
            com.oyo.accouting.pojo.SyncHotel sh = new com.oyo.accouting.pojo.SyncHotel();
            sh.setCardcode(hotelMapJson.get("cardcode").toString());
            sh.setCardname(hotelMapJson.get("cardname").toString());
            sh.setCntctPrsn(hotelMapJson.get("CntctPrsn").toString());
            sh.setValid(hotelMapJson.get("valid").toString());
            sh.setLicTradNum(hotelMapJson.get("LicTradNum").toString());
            sh.setUCrsid(hotelMapJson.get("U_CRSID").toString());
            sh.setContacts(hotelMapJson.get("Contacts").toString());
            sh.setAddress(hotelMapJson.get("Address").toString());
            sh.setBatch(batch);
            this.accountingSyncHotelMapper.insert(sh);
            success = true;
        }
        //插入日志
        SyncLog sl = new SyncLog();
        sl.setSourceId(h.getId());
        sl.setCreateTime(new Timestamp(new Date().getTime()));
        sl.setSourceUpdateTime(h.getUpdatedAt());
        sl.setType("Hotel");
        sl.setVersion(syncLogDto==null?1:syncLogDto.getVersion()+1);
        sl.setJsonData(hotelMapStr);
        sl.setStatus(Integer.valueOf(resultJsonObj.get("Code").toString()));
        sl.setBatch(batch);
        sl.setMessage(resultJsonObj.get("Message").toString());
        this.accountingSyncLogMapper.insert(sl);

        return success;
    }
}