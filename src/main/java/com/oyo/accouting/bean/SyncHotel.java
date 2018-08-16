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
        if(this.syncHotelMap != null)return;
        this.syncHotelMap = new LinkedHashMap<>();
    }

    public Map<String ,Object> getSyncHotelMap(){
        return syncHotelMap;
    }


    public Map<String ,Object> setSyncHotelMapC(HotelDto hotel,Map<String ,Object> syncHotel){

        syncHotel.put("cardcode","C"+(hotel.getId().toString().length()<7?"H-":"")+hotel.getId().toString());
        syncHotel.put("cardname",hotel.getOyoId()==null? "" : (hotel.getOyoId() + " - " + hotel.getName()));
        syncHotel.put("valid","Y");
        syncHotel.put("CntctPrsn",hotel.getManagerName());
        syncHotel.put("LicTradNum","");
        syncHotel.put("U_CRSID",hotel.getOyoId());
        syncHotel.put("CardType","C");
        syncHotel.put("DebPayAcct",AccountingCode.CODE_11220203.getCode());


        Map<String ,Object>[] contactEmployees = new LinkedHashMap[1];
        UserProfilesDto userProfiles = hotel.getUserProfiles();
        contactEmployees[0] = new LinkedHashMap<>();
        contactEmployees[0].put("Name",checkNull(userProfiles.getFirstName()));
        contactEmployees[0].put("Tel1",checkNull(userProfiles.getPhone()));
        syncHotel.put("Contacts",contactEmployees);

        Map<String ,Object>[] bPAddresses = new LinkedHashMap[1];
        bPAddresses[0] = new LinkedHashMap<>();
        bPAddresses[0].put("Address",hotel.getStreet()+","+hotel.getCity());
        bPAddresses[0].put("City",checkNull(hotel.getCity()));
        syncHotel.put("Address",bPAddresses);

        return syncHotel;
    }

    public Map<String ,Object> setSyncHotelMapV(HotelDto hotel,Map<String ,Object> syncHotel){

        syncHotel.put("cardcode",(hotel.getId().toString().length()<7?"H-":"")+hotel.getId().toString());
        syncHotel.put("cardname",hotel.getOyoId()==null? "" : (hotel.getOyoId() + " - " + hotel.getName()));
        syncHotel.put("valid","Y");
        syncHotel.put("CntctPrsn",hotel.getManagerName());
        syncHotel.put("LicTradNum","");
        syncHotel.put("U_CRSID",hotel.getOyoId());
        syncHotel.put("CardType","V");
        syncHotel.put("DebPayAcct",AccountingCode.CODE_22020203.getCode());


        Map<String ,Object>[] contactEmployees = new LinkedHashMap[1];
        UserProfilesDto userProfiles = hotel.getUserProfiles();
        contactEmployees[0] = new LinkedHashMap<>();
        contactEmployees[0].put("Name",checkNull(userProfiles.getFirstName()));
        contactEmployees[0].put("Tel1",checkNull(userProfiles.getPhone()));
        syncHotel.put("Contacts",contactEmployees);

        Map<String ,Object>[] bPAddresses = new LinkedHashMap[1];
        bPAddresses[0] = new LinkedHashMap<>();
        bPAddresses[0].put("Address",hotel.getStreet()+","+hotel.getCity());
        bPAddresses[0].put("City",checkNull(hotel.getCity()));
        syncHotel.put("Address",bPAddresses);

        return syncHotel;
    }

    private Object checkNull(Object t){
        return t == null? "" : t;
    }


}
