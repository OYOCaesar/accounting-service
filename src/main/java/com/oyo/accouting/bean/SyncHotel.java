package com.oyo.accouting.bean;

import com.oyo.accouting.common.SapConfig;
import org.springframework.util.StringUtils;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.regex.Pattern;

public class SyncHotel {

    private ConcurrentMap<String ,Object> syncHotelMap;

    public SyncHotel(){
        if(this.syncHotelMap != null)return;
        syncHotelMap = new ConcurrentHashMap<String ,Object>();
        syncHotelMap.put("CardCode","");
        syncHotelMap.put("CRSID","");
        syncHotelMap.put("CardName","");
        syncHotelMap.put("CardFName","");
        syncHotelMap.put("BillingName","");
        syncHotelMap.put("CardType","");
        syncHotelMap.put("GroupCode","");
        syncHotelMap.put("PhoneNum","");
        syncHotelMap.put("Cellular","");
        syncHotelMap.put("EmailAddress","");
        syncHotelMap.put("Remarks","");
        syncHotelMap.put("U_EComMerchID","");
        syncHotelMap.put("PayTermsGrpCode","");

        Map<String ,Object> bPFiscalTaxId = new HashMap<String ,Object>();
        bPFiscalTaxId.put("association",new String[]{""});
        Map<String ,Object>[] bPFiscalTaxIdFields = new HashMap[1];
        Map<String ,Object> bPFiscalTaxIdFields0 = new HashMap<String ,Object>();
        bPFiscalTaxIdFields0.put("TaxId0","");
        bPFiscalTaxIdFields0.put("TaxId1","");
        bPFiscalTaxIdFields0.put("TaxId2","");
        bPFiscalTaxIdFields0.put("TaxId3","");
        bPFiscalTaxIdFields[0] = bPFiscalTaxIdFields0;
        bPFiscalTaxId.put("fields",bPFiscalTaxIdFields);
        syncHotelMap.put("BPFiscalTaxID",bPFiscalTaxId);


        Map<String ,Object> contactEmployees = new HashMap<String ,Object>();
        contactEmployees.put("association",new String[]{""});
        Map<String ,Object>[] contactEmployeesFields = new HashMap[1];
        Map<String ,Object> contactEmployeesFields0 = new HashMap<String ,Object>();
        contactEmployeesFields0.put("Title","get_sap_pan_no");
        contactEmployeesFields0.put("Name","name");
        contactEmployeesFields0.put("FirstName","vat_no");
        contactEmployeesFields0.put("LastName","last_name");
        contactEmployeesFields0.put("MobilePhone","phone");
        contactEmployeesFields0.put("Phone1","phone");
        contactEmployeesFields0.put("E_Mail","email");
        contactEmployeesFields0.put("Gender","get_sap_sex");
        contactEmployeesFields[0] = contactEmployeesFields0;
        contactEmployees.put("fields",contactEmployeesFields);
        syncHotelMap.put("ContactEmployees",contactEmployees);

        Map<String ,Object> bPAddresses = new HashMap<String ,Object>();
        bPAddresses.put("association",new String[]{"",""});
        Map<String ,Object>[] bPAddressesFields = new HashMap[2];
        Map<String ,Object> bPAddressesFields0 = new HashMap<String ,Object>();
        bPAddressesFields0.put("RowNum","");
        bPAddressesFields0.put("AddressName","get_sap_address");
        bPAddressesFields0.put("AddressType","set_bill_to");
        bPAddressesFields0.put("City","get_sap_city_code");
        bPAddressesFields0.put("State","get_state_name");
        bPAddressesFields0.put("ZipCode","pincode");
        bPAddressesFields0.put("Block","plot_number");
        bPAddressesFields0.put("Street","street");
        bPAddressesFields0.put("GSTIN","get_gstin");
        bPAddressesFields0.put("GstType","");
        Map<String ,Object> bPAddressesFields1 = new HashMap<String ,Object>();
        bPAddressesFields1.put("RowNum","");
        bPAddressesFields1.put("AddressName","get_sap_address");
        bPAddressesFields1.put("AddressType","set_ship_to");
        bPAddressesFields1.put("City","get_sap_city_code");
        bPAddressesFields1.put("State","get_state_name");
        bPAddressesFields1.put("ZipCode","pincode");
        bPAddressesFields1.put("Block","plot_number");
        bPAddressesFields1.put("Street","street");
        bPAddressesFields[0] = bPAddressesFields0;
        bPAddressesFields[1] = bPAddressesFields1;
        bPAddresses.put("fields",bPAddressesFields);
        syncHotelMap.put("BPAddresses",bPAddresses);


        Map<String ,Object> bankAccounts = new HashMap<String ,Object>();
        bankAccounts.put("association",new String[]{""});
        Map<String ,Object>[] bankAccountsFields = new HashMap[1];
        Map<String ,Object> bankAccountsFields0 = new HashMap<String ,Object>();
        bankAccountsFields0.put("AccountNo","bank_account_details('oyo')['account_no']");
        bankAccountsFields0.put("AccountName","bank_account_details('oyo')['account_name']");
        bankAccountsFields0.put("BankName","bank_account_details('alcott')['bank_name']");
        bankAccountsFields0.put("Branch","");
        bankAccountsFields0.put("IFSC","bank_account_details('oyo')['ifsc']");
        bankAccountsFields0.put("MICR","bank_account_details('oyo')['micr']");
        bankAccountsFields0.put("BankAddress","bank_account_details('oyo')['bank_address']");
        bankAccountsFields[0] = bankAccountsFields0;
        bankAccounts.put("fields",bankAccountsFields);
        syncHotelMap.put("BankAccounts",bankAccounts);
    }

    public Map<String ,Object> getSyncHotelMap(){
        return syncHotelMap;
    }


    public Map<String ,Object> setSyncHotelMap(HotelDto hotel,Map<String ,Object> syncHotel){

        syncHotel.put("CardCode",(hotel.getId().toString().length()<7?"H-":"")+hotel.getId().toString());
        syncHotel.put("CRSID",checkNull(hotel.getOyoId()));
        syncHotel.put("CardName",hotel.getOyoId()==null? "" : (hotel.getOyoId() + " - " + hotel.getName()));
        syncHotel.put("CardFName",checkNull(hotel.getAlternateName()));
        syncHotel.put("BillingName",checkNull(hotel.getBillingEntity()));
        syncHotel.put("Cellular",checkNull(hotel.getPhone()));
        syncHotel.put("EmailAddress",checkNull(hotel.getEmail()));
        syncHotel.put("U_EComMerchID",hotel.getId());
        syncHotel.put("PayTermsGrpCode","-1");

        Map<String ,Object> bPFiscalTaxId = (Map<String, Object>) syncHotel.get("BPFiscalTaxID");
        AccountDetailsDto accountDetails = hotel.getAccountDetails();
        Map<String ,Object>[] bPFiscalTaxIdFields = (Map<String, Object>[]) bPFiscalTaxId.get("fields");

        String regEx = "/\\A[A-Z]{5}[0-9]{4}[A-Z]{1}\\z/";
        Pattern pattern = Pattern.compile(regEx);
        boolean checkPanNo = accountDetails!=null && !StringUtils.isEmpty(accountDetails.getPanNo()) && pattern.matcher(accountDetails.getPanNo().trim()).find();
        bPFiscalTaxIdFields[0].put("TaxId0",checkPanNo?accountDetails.getPanNo():SapConfig.NILCLASS_PAN_NO);
        bPFiscalTaxIdFields[0].put("TaxId1",checkNull(accountDetails.getCstNo()));
        bPFiscalTaxIdFields[0].put("TaxId2",checkNull(accountDetails.getVatNo()));
        bPFiscalTaxIdFields[0].put("TaxId3",checkNull(accountDetails.getServiceTaxNo()));

        Map<String ,Object> contactEmployees = (Map<String, Object>) syncHotel.get("ContactEmployees");
        UserProfilesDto userProfiles = hotel.getUserProfiles();
        Map<String ,Object>[] contactEmployeesFields = (Map<String, Object>[]) contactEmployees.get("fields");
        contactEmployeesFields[0].put("Name",checkNull(userProfiles.getFirstName()));
        contactEmployeesFields[0].put("FirstName",checkNull(userProfiles.getFirstName()));
        contactEmployeesFields[0].put("LastName",checkNull(userProfiles.getLastName()));
        contactEmployeesFields[0].put("MobilePhone",checkNull(userProfiles.getPhone()));
        contactEmployeesFields[0].put("E_Mail",checkNull(userProfiles.getEmail()));
        contactEmployeesFields[0].put("Gender",(StringUtils.isEmpty(userProfiles.getSex())?userProfiles.getSex() : 0)==0?"Male":"Female");

        Map<String ,Object> bPAddresses = (Map<String, Object>) syncHotel.get("BPAddresses");
        Map<String ,Object>[] bPAddressesFields = (Map<String, Object>[]) bPAddresses.get("fields");
        bPAddressesFields[0].put("RowNum","0");
        bPAddressesFields[0].put("AddressName",hotel.getStreet()+","+hotel.getCity());
        bPAddressesFields[0].put("AddressType",SapConfig.SET_BILL_TO);
        bPAddressesFields[0].put("City",hotel.getCities() == null? "" : hotel.getCities().getCode());
        bPAddressesFields[0].put("State",StringUtils.isEmpty(userProfiles.getState())?userProfiles.getState():SapConfig.USERPROFILE_STATE);
        bPAddressesFields[1].put("RowNum","1");
        bPAddressesFields[1].put("AddressName",hotel.getStreet()+","+hotel.getCity());
        bPAddressesFields[1].put("AddressType",SapConfig.SET_BILL_TO);
        bPAddressesFields[1].put("City",hotel.getCities() == null? "" : hotel.getCities().getCode());

        Map<String ,Object> bankAccounts = (Map<String, Object>) syncHotel.get("BankAccounts");
        Map<String ,Object>[] bankAccountsFields = (Map<String, Object>[]) bankAccounts.get("fields");
        bankAccountsFields[0].put("AccountNo",checkNull(accountDetails.getBankAccountNo()));
        bankAccountsFields[0].put("AccountName",checkNull(accountDetails.getName()));
        bankAccountsFields[0].put("BankName",checkNull(accountDetails.getBankName()));
        bankAccountsFields[0].put("IFSC",checkNull(accountDetails.getBankIfscCode()));
        bankAccountsFields[0].put("MICR",checkNull(accountDetails.getBankMicrCode()));
        bankAccountsFields[0].put("BankAddress",checkNull(accountDetails.getBankAddress()));
        return syncHotel;
    }

    private Object checkNull(Object t){
        return t == null? "" : t;
    }


    @Override
    public String toString() {
        for (Map.Entry<String, Object> s : this.syncHotelMap.entrySet()) {
            System.out.println(s);
        }
        return null;
    }
}
