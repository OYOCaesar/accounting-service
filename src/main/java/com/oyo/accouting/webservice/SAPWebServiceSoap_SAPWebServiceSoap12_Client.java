
package com.oyo.accouting.webservice;

/**
 * Please modify this class to meet your needs
 * This class is not complete
 */

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import javax.xml.namespace.QName;
import javax.jws.WebMethod;
import javax.jws.WebParam;
import javax.jws.WebResult;
import javax.jws.WebService;
import javax.xml.bind.annotation.XmlSeeAlso;
import javax.xml.ws.RequestWrapper;
import javax.xml.ws.ResponseWrapper;

/**
 * This class was generated by Apache CXF 3.2.2
 * 2018-08-02T14:29:33.871+08:00
 * Generated source version: 3.2.2
 *
 */
public final class SAPWebServiceSoap_SAPWebServiceSoap12_Client {

    private static final QName SERVICE_NAME = new QName("http://tempuri.org/", "SAPWebService");

    private SAPWebServiceSoap_SAPWebServiceSoap12_Client() {
    }

    public static void main(String args[]) throws java.lang.Exception {
        URL wsdlURL = SAPWebService.WSDL_LOCATION;
        if (args.length > 0 && args[0] != null && !"".equals(args[0])) {
            File wsdlFile = new File(args[0]);
            try {
                if (wsdlFile.exists()) {
                    wsdlURL = wsdlFile.toURI().toURL();
                } else {
                    wsdlURL = new URL(args[0]);
                }
            } catch (MalformedURLException e) {
                e.printStackTrace();
            }
        }

        SAPWebService ss = new SAPWebService(wsdlURL, SERVICE_NAME);
        SAPWebServiceSoap port = ss.getSAPWebServiceSoap12();

        {
        System.out.println("Invoking journalEntries...");
        java.lang.String _journalEntries_json = "";
        java.lang.String _journalEntries__return = port.journalEntries(_journalEntries_json);
        System.out.println("journalEntries.result=" + _journalEntries__return);


        }
        {
        System.out.println("Invoking invoices...");
        java.lang.String _invoices_json = "";
        java.lang.String _invoices__return = port.invoices(_invoices_json);
        System.out.println("invoices.result=" + _invoices__return);


        }
        {
        System.out.println("Invoking helloWorld...");
        java.lang.String _helloWorld__return = port.helloWorld();
        System.out.println("helloWorld.result=" + _helloWorld__return);


        }
        {
        System.out.println("Invoking businessPartners...");
        java.lang.String _businessPartners_json = "";
        java.lang.String _businessPartners__return = port.businessPartners(_businessPartners_json);
        System.out.println("businessPartners.result=" + _businessPartners__return);


        }

        System.exit(0);
    }

}
