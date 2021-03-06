package ca.concordia.dsd.arch;


/**
 * ca.concordia.dsd.ca.concordia.dsd.arch/corbaOperations.java .
 * Generated by the IDL-to-Java compiler (portable), version "3.2"
 * from corba.ca.concordia.dsd.idl
 * Thursday, 5 August, 2021 2:30:22 PM EDT
 */

public interface corbaOperations {
    String createTRecord(String id, String fName, String lName, String address, String phone, String specialization, String location);

    String createSRecord(String id, String fName, String lName, String courses, boolean status, String statusDate);

    String getRecordCounts(String id);

    String editRecord(String id, String recordID, String fieldName, String newValue);

    String transferRecord(String id, String recordId, String remoteCenterServerName);

    String killPrimaryServer(String id);
} // interface corbaOperations
