package idlmodule;


/**
* idlmodule/corbaOperations.java .
* Generated by the IDL-to-Java compiler (portable), version "3.2"
* from corba.idl
* Thursday, 5 August, 2021 8:11:48 PM EDT
*/

public interface corbaOperations 
{
  String createTRecord (String managerID, String teacher);
  String createSRecord (String managerID, String student);
  String getRecordCount (String managerID);
  String editRecord (String managerID, String recordID, String fieldname, String newvalue);
  String transferRecord (String managerID, String recordID, String location);
  String killPrimaryServer (String location);
} // interface corbaOperations