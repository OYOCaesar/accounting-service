package com.oyo.accouting.bean;


import com.oyo.accouting.constants.AccountingCode;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * @author zfguo
 */
public class SyncHotel {
    private HashMap<String ,Object> syncHotelMap;

    public SyncHotel(){
        this.syncHotelMap = new LinkedHashMap<>();
    }

    public Map<String ,Object> getSyncHotelMap(){
        return syncHotelMap;
    }


    /**
     *
     * @param hotel
     * @param isC true为客户，false为商家
     * @return
     */
    public Map<String ,Object> setSyncHotelMap(HotelDto hotel,boolean isC){

        this.syncHotelMap.put("cardcode",(isC?"C":"")+(hotel.getId().toString().length()<7?"H-":"")+hotel.getId().toString());
        this.syncHotelMap.put("cardname",hotel.getOyoId()==null? "" : (hotel.getOyoId() + " - " + hotel.getName()));
        this.syncHotelMap.put("valid",hotel.getStatus()==1||hotel.getStatus()==2?"Y":"N");//Active/Live
        this.syncHotelMap.put("CntctPrsn",hotel.getManagerName());
        this.syncHotelMap.put("LicTradNum","");
        this.syncHotelMap.put("U_CRSID",hotel.getOyoId());
        this.syncHotelMap.put("CardType",isC?"C":"V");
        this.syncHotelMap.put("DebPayAcct",isC?AccountingCode.CODE_11220203.getCode():AccountingCode.CODE_22020203.getCode());
        this.syncHotelMap.put("StartDate",hotel.getActivationDate()==null?"":hotel.getActivationDate().toString());
        String statusTrack = hotel.getStatusTrack()==null?"":hotel.getStatusTrack();
        this.syncHotelMap.put("UpdateRemark", "sold_out:"+hotel.getUpdateRemark()+"状态更新原因:"+statusTrack.substring(0,statusTrack.length()>100?100:statusTrack.length()));//



        Map<String ,Object>[] contactEmployees = new LinkedHashMap[1];
        UserProfilesDto userProfiles = hotel.getUserProfiles();
        contactEmployees[0] = new LinkedHashMap<>();
        contactEmployees[0].put("Name",checkNull(userProfiles.getFirstName()).toString()+checkNull(userProfiles.getLastName()).toString());
        contactEmployees[0].put("Tel1",checkNull(userProfiles.getPhone()));
        this.syncHotelMap.put("Contacts",contactEmployees);

        Map<String ,Object>[] bPAddresses = new LinkedHashMap[1];
        bPAddresses[0] = new LinkedHashMap<>();
        bPAddresses[0].put("Address",hotel.getStreet()+","+hotel.getCity());
        bPAddresses[0].put("City",checkNull(hotel.getCity()));
        this.syncHotelMap.put("Address",bPAddresses);

        return this.syncHotelMap;
    }

    private Object checkNull(Object t){
        return t == null? "" : t;
    }


}
