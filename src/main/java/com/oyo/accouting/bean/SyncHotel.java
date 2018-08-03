package com.oyo.accouting.bean;

import com.oyo.accouting.common.SapConfig;
import org.springframework.util.StringUtils;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.regex.Pattern;

/**
 * @author zfguo
 */
public class SyncHotel {

    private HashMap<String ,Object> syncHotelMap;

    public SyncHotel(){
        if(this.syncHotelMap != null)return;
        syncHotelMap = new LinkedHashMap<String ,Object>();
        syncHotelMap.put("cardcode","");
        syncHotelMap.put("crsid","");
        syncHotelMap.put("cardname","");
        syncHotelMap.put("cardfname","");
        syncHotelMap.put("billingname","");
        syncHotelMap.put("cardtype","");
        syncHotelMap.put("groupcode","");
        syncHotelMap.put("phonenum","");
        syncHotelMap.put("cellular","");
        syncHotelMap.put("emailaddress","");
        syncHotelMap.put("remarks","");
        syncHotelMap.put("u_ecommerchid","");
        syncHotelMap.put("paytermsgrpcode","");

        Map<String ,Object> bPFiscalTaxId = new LinkedHashMap<String ,Object>();
        Map<String ,Object>[] bPFiscalTaxIdFields = new LinkedHashMap[1];
        Map<String ,Object> bPFiscalTaxIdFields0 = new LinkedHashMap<String ,Object>();
        bPFiscalTaxIdFields0.put("taxid0","");
        bPFiscalTaxIdFields0.put("taxid1","");
        bPFiscalTaxIdFields0.put("taxid2","");
        bPFiscalTaxIdFields0.put("taxid3","");
        bPFiscalTaxIdFields[0] = bPFiscalTaxIdFields0;
        bPFiscalTaxId.put("fields",bPFiscalTaxIdFields);
        syncHotelMap.put("bpfiscaltaxid",bPFiscalTaxId);


        Map<String ,Object> contactEmployees = new LinkedHashMap<String ,Object>();
        Map<String ,Object>[] contactEmployeesFields = new LinkedHashMap[1];
        Map<String ,Object> contactEmployeesFields0 = new LinkedHashMap<String ,Object>();
        contactEmployeesFields0.put("title","");
        contactEmployeesFields0.put("name","");
        contactEmployeesFields0.put("firstname","");
        contactEmployeesFields0.put("lastname","");
        contactEmployeesFields0.put("mobilephone","");
        contactEmployeesFields0.put("phone1","");
        contactEmployeesFields0.put("e_mail","");
        contactEmployeesFields0.put("gender","");
        contactEmployeesFields[0] = contactEmployeesFields0;
        contactEmployees.put("fields",contactEmployeesFields);
        syncHotelMap.put("contactemployees",contactEmployees);

        Map<String ,Object> bPAddresses = new LinkedHashMap<String ,Object>();
        Map<String ,Object>[] bPAddressesFields = new LinkedHashMap[2];
        Map<String ,Object> bPAddressesFields0 = new LinkedHashMap<String ,Object>();
        bPAddressesFields0.put("rownum","0");
        bPAddressesFields0.put("addressname","");
        bPAddressesFields0.put("addresstype","");
        bPAddressesFields0.put("city","");
        bPAddressesFields0.put("state","");
        bPAddressesFields0.put("zipcode","");
        bPAddressesFields0.put("block","");
        bPAddressesFields0.put("street","");
        bPAddressesFields0.put("gstin","");
        bPAddressesFields0.put("gsttype","");
        Map<String ,Object> bPAddressesFields1 = new LinkedHashMap<String ,Object>();
        bPAddressesFields1.put("rownum","1");
        bPAddressesFields1.put("addressname","");
        bPAddressesFields1.put("addresstype","");
        bPAddressesFields1.put("city","");
        bPAddressesFields1.put("state","");
        bPAddressesFields1.put("zipcode","");
        bPAddressesFields1.put("block","");
        bPAddressesFields1.put("street","");
        bPAddressesFields[0] = bPAddressesFields0;
        bPAddressesFields[1] = bPAddressesFields1;
        bPAddresses.put("fields",bPAddressesFields);
        syncHotelMap.put("bpaddresses",bPAddresses);


        Map<String ,Object> bankAccounts = new LinkedHashMap<String ,Object>();
        Map<String ,Object>[] bankAccountsFields = new LinkedHashMap[1];
        Map<String ,Object> bankAccountsFields0 = new LinkedHashMap<String ,Object>();
        bankAccountsFields0.put("accountno","");
        bankAccountsFields0.put("accountname","");
        bankAccountsFields0.put("bankname","");
        bankAccountsFields0.put("branch","");
        bankAccountsFields0.put("ifsc","");
        bankAccountsFields0.put("micr","");
        bankAccountsFields0.put("bankaddress","");
        bankAccountsFields[0] = bankAccountsFields0;
        bankAccounts.put("fields",bankAccountsFields);
        syncHotelMap.put("bankaccounts",bankAccounts);
    }

    public Map<String ,Object> getSyncHotelMap(){
        return syncHotelMap;
    }


    public Map<String ,Object> setSyncHotelMap(HotelDto hotel,Map<String ,Object> syncHotel){

        syncHotel.put("cardcode",(hotel.getId().toString().length()<7?"H-":"")+hotel.getId().toString());
        syncHotel.put("crsid",checkNull(hotel.getOyoId()));
        syncHotel.put("cardname",hotel.getOyoId()==null? "" : (hotel.getOyoId() + " - " + hotel.getName()));
        syncHotel.put("cardfname",checkNull(hotel.getAlternateName()));
        syncHotel.put("billingname",checkNull(hotel.getBillingEntity()));
        syncHotel.put("cellular",checkNull(hotel.getPhone()));
        syncHotel.put("emailaddress",checkNull(hotel.getEmail()));
        syncHotel.put("u_ecommerchid",hotel.getId());
        syncHotel.put("paytermsgrpcode","-1");

        Map<String ,Object> bPFiscalTaxId = (Map<String, Object>) syncHotel.get("bpfiscaltaxid");
        AccountDetailsDto accountDetails = hotel.getAccountDetails();
        Map<String ,Object>[] bPFiscalTaxIdFields = (Map<String, Object>[]) bPFiscalTaxId.get("fields");

        String regEx = "/\\A[A-Z]{5}[0-9]{4}[A-Z]{1}\\z/";
        Pattern pattern = Pattern.compile(regEx);
        boolean checkPanNo = accountDetails!=null && !StringUtils.isEmpty(accountDetails.getPanNo()) && pattern.matcher(accountDetails.getPanNo().trim()).find();
        bPFiscalTaxIdFields[0].put("taxid0",checkPanNo?accountDetails.getPanNo():SapConfig.NILCLASS_PAN_NO);
        bPFiscalTaxIdFields[0].put("taxid1",checkNull(accountDetails.getCstNo()));
        bPFiscalTaxIdFields[0].put("taxid2",checkNull(accountDetails.getVatNo()));
        bPFiscalTaxIdFields[0].put("taxid3",checkNull(accountDetails.getServiceTaxNo()));

        Map<String ,Object> contactEmployees = (Map<String, Object>) syncHotel.get("contactemployees");
        UserProfilesDto userProfiles = hotel.getUserProfiles();
        Map<String ,Object>[] contactEmployeesFields = (Map<String, Object>[]) contactEmployees.get("fields");
        contactEmployeesFields[0].put("name",checkNull(userProfiles.getFirstName()));
        contactEmployeesFields[0].put("firstname",checkNull(userProfiles.getFirstName()));
        contactEmployeesFields[0].put("lastname",checkNull(userProfiles.getLastName()));
        contactEmployeesFields[0].put("mobilephone",checkNull(userProfiles.getPhone()));
        contactEmployeesFields[0].put("e_mail",checkNull(userProfiles.getEmail()));
        contactEmployeesFields[0].put("gender",(StringUtils.isEmpty(userProfiles.getSex())?userProfiles.getSex() : 0)==0?"Male":"Female");

        Map<String ,Object> bPAddresses = (Map<String, Object>) syncHotel.get("bpaddresses");
        Map<String ,Object>[] bPAddressesFields = (Map<String, Object>[]) bPAddresses.get("fields");
        bPAddressesFields[0].put("addressname",hotel.getStreet()+","+hotel.getCity());
        bPAddressesFields[0].put("addresstype",SapConfig.SET_BILL_TO);
        bPAddressesFields[0].put("city",hotel.getCities() == null? "" : hotel.getCities().getCode());
        bPAddressesFields[0].put("state",StringUtils.isEmpty(userProfiles.getState())?userProfiles.getState():SapConfig.USERPROFILE_STATE);
        bPAddressesFields[1].put("addressname",hotel.getStreet()+","+hotel.getCity());
        bPAddressesFields[1].put("addresstype",SapConfig.SET_BILL_TO);
        bPAddressesFields[1].put("city",hotel.getCities() == null? "" : hotel.getCities().getCode());

        Map<String ,Object> bankAccounts = (Map<String, Object>) syncHotel.get("bankaccounts");
        Map<String ,Object>[] bankAccountsFields = (Map<String, Object>[]) bankAccounts.get("fields");
        bankAccountsFields[0].put("accountno",checkNull(accountDetails.getBankAccountNo()));
        bankAccountsFields[0].put("accountname",checkNull(accountDetails.getName()));
        bankAccountsFields[0].put("bankname",checkNull(accountDetails.getBankName()));
        bankAccountsFields[0].put("ifsc",checkNull(accountDetails.getBankIfscCode()));
        bankAccountsFields[0].put("micr",checkNull(accountDetails.getBankMicrCode()));
        bankAccountsFields[0].put("bankaddress",checkNull(accountDetails.getBankAddress()));
        return syncHotel;
    }

    private Object checkNull(Object t){
        return t == null? "" : t;
    }

}
