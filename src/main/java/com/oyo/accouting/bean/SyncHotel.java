package com.oyo.accouting.bean;


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


    public Map<String ,Object> setSyncHotelMap(HotelDto hotel,Map<String ,Object> syncHotel){

        syncHotel.put("cardcode",(hotel.getId().toString().length()<7?"H-":"")+hotel.getId().toString());
        syncHotel.put("cardname",hotel.getOyoId()==null? "" : (hotel.getOyoId() + " - " + hotel.getName()));
        syncHotel.put("valid",hotel.getStatus()==1?"Y":"N");
        syncHotel.put("CntctPrsn",hotel.getManagerName());
        syncHotel.put("LicTradNum","");
        syncHotel.put("U_CRSID",hotel.getOyoId());


        Map<String ,Object>[] contactEmployees = new LinkedHashMap[1];
        UserProfilesDto userProfiles = hotel.getUserProfiles();
        contactEmployees[0] = new LinkedHashMap<>();
        contactEmployees[0].put("Name",checkNull(userProfiles.getFirstName()));
        contactEmployees[0].put("Tel1",checkNull(userProfiles.getPhone()));
        syncHotel.put("Contacts",contactEmployees);

        Map<String ,Object>[] bPAddresses = new LinkedHashMap[1];
        bPAddresses[0] = new LinkedHashMap<>();
        bPAddresses[0].put("Address",hotel.getStreet()+","+hotel.getCity());
        bPAddresses[0].put("City",hotel.getCity());
        syncHotel.put("Address",bPAddresses);

        /**syncHotel.put("cardfname",checkNull(hotel.getAlternateName()));
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
         contactEmployeesFields[0].put("phone",checkNull(userProfiles.getPhone()));
         contactEmployeesFields[0].put("e_mail",checkNull(userProfiles.getEmail()));
         contactEmployeesFields[0].put("gender",(StringUtils.isEmpty(userProfiles.getSex())?userProfiles.getSex() : 0)==0?"Male":"Female");

         Map<String ,Object> bPAddresses = (Map<String, Object>) syncHotel.get("bpaddresses");
         Map<String ,Object>[] bPAddressesFields = (Map<String, Object>[]) bPAddresses.get("fields");
         bPAddressesFields[0].put("addressname",hotel.getStreet()+","+hotel.getCity());
         bPAddressesFields[0].put("addresstype",SapConfig.SET_BILL_TO);
         bPAddressesFields[0].put("city",hotel.getCity());
         bPAddressesFields[0].put("state",hotel.getState());
         bPAddressesFields[0].put("street",hotel.getStreet());

         Map<String ,Object> bankAccounts = (Map<String, Object>) syncHotel.get("bankaccounts");
         Map<String ,Object>[] bankAccountsFields = (Map<String, Object>[]) bankAccounts.get("fields");
         bankAccountsFields[0].put("accountno",checkNull(accountDetails.getBankAccountNo()));
         bankAccountsFields[0].put("accountname",checkNull(accountDetails.getName()));
         bankAccountsFields[0].put("bankname",checkNull(accountDetails.getBankName()));
         bankAccountsFields[0].put("ifsc",checkNull(accountDetails.getBankIfscCode()));
         bankAccountsFields[0].put("micr",checkNull(accountDetails.getBankMicrCode()));
         bankAccountsFields[0].put("bankaddress",checkNull(accountDetails.getBankAddress()));
         **/
        //syncHotelMap.put("oyoShare",hotel.getOyoShare());
        return syncHotel;
    }

    private Object checkNull(Object t){
        return t == null? "" : t;
    }


}
