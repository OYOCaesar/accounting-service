
package com.oyo.accouting.webservice;

import javax.xml.bind.annotation.XmlRegistry;


/**
 * This object contains factory methods for each 
 * Java content interface and Java element interface 
 * generated in the com.oyo.accouting.webservice package. 
 * <p>An ObjectFactory allows you to programatically 
 * construct new instances of the Java representation 
 * for XML content. The Java representation of XML 
 * content can consist of schema derived interfaces 
 * and classes representing the binding of schema 
 * type definitions, element declarations and model 
 * groups.  Factory methods for each of these are 
 * provided in this class.
 * 
 */
@XmlRegistry
public class ObjectFactory {


    /**
     * Create a new ObjectFactory that can be used to create new instances of schema derived classes for package: com.oyo.accouting.webservice
     * 
     */
    public ObjectFactory() {
    }

    /**
     * Create an instance of {@link BusinessPartners }
     * 
     */
    public BusinessPartners createBusinessPartners() {
        return new BusinessPartners();
    }

    /**
     * Create an instance of {@link BusinessPartnersResponse }
     * 
     */
    public BusinessPartnersResponse createBusinessPartnersResponse() {
        return new BusinessPartnersResponse();
    }

    /**
     * Create an instance of {@link Invoices }
     * 
     */
    public Invoices createInvoices() {
        return new Invoices();
    }

    /**
     * Create an instance of {@link InvoicesResponse }
     * 
     */
    public InvoicesResponse createInvoicesResponse() {
        return new InvoicesResponse();
    }

    /**
     * Create an instance of {@link JournalEntries }
     * 
     */
    public JournalEntries createJournalEntries() {
        return new JournalEntries();
    }

    /**
     * Create an instance of {@link JournalEntriesResponse }
     * 
     */
    public JournalEntriesResponse createJournalEntriesResponse() {
        return new JournalEntriesResponse();
    }

    /**
     * Create an instance of {@link HelloWorld }
     * 
     */
    public HelloWorld createHelloWorld() {
        return new HelloWorld();
    }

    /**
     * Create an instance of {@link HelloWorldResponse }
     * 
     */
    public HelloWorldResponse createHelloWorldResponse() {
        return new HelloWorldResponse();
    }

}
