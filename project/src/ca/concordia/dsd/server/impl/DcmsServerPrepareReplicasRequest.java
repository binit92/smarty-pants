package ca.concordia.dsd.server.impl;


import ca.concordia.dsd.arch.corbaPOA;
import ca.concordia.dsd.database.Records;
import ca.concordia.dsd.server.OperationsType;
import ca.concordia.dsd.util.Constants;
import ca.concordia.dsd.util.LogUtil;

import java.util.HashMap;
import java.util.List;

/**
 * DcmsServerImpl class includes all the server operations' implementations,
 * implements all the methods in the IDL interface Performs the necessary
 * operations and returns the result/acknowledgement back to the Client.
 */

public class DcmsServerPrepareReplicasRequest extends corbaPOA {
    private final String LOG_TAG = "| " + DcmsServerPrepareReplicasRequest.class.getSimpleName() + " |  ";
    public HashMap<String, List<Records>> recordsMap;
    LogUtil logUtil;
    String IPaddress;
    int studentCount = 0;
    int teacherCount = 0;
    String recordsCount;
    String location;
    Integer requestId;
    HashMap<Integer, String> requestBuffer;
    Integer replicaID;

    /*
     * DcmsServerImpl Constructor to initializes the variables used for the
     * implementation
     *
     * @param loc The server location for which the server implementation should
     * be initialized
     */
    public DcmsServerPrepareReplicasRequest(Integer replicaID, LogUtil logUtil) {
        recordsMap = new HashMap<>();
        requestBuffer = new HashMap<>();
        requestId = 0;
        this.replicaID = replicaID;
        this.logUtil = logUtil;
    }


    private void sendMulticastRequest(String req) {
        DcmsServerMultiCastSender sender = new DcmsServerMultiCastSender(req, logUtil);
        sender.start();
    }


    private String getServerLoc(String managerID) {
        return managerID.substring(0, 3);
    }


    @Override
    public String createTRecord(String id, String fName, String lName, String address, String phone, String specialization, String location) {
        StringBuilder builder = new StringBuilder();
        builder.append(OperationsType.CREATE_TR_RECORD);
        builder.append(Constants.RESPONSE_DATA_SPLITTER);
        builder.append(id);
        builder.append("|");
        builder.append(fName);
        builder.append("|");
        builder.append(lName);
        builder.append("|");
        builder.append(address);
        builder.append("|");
        builder.append(phone);
        builder.append("|");
        builder.append(specialization);
        builder.append("|");
        builder.append(location);
        builder.append("|");
        logUtil.log(LOG_TAG + " cccccccccccccc : " + builder);

		/*
		teacher = Integer.toString(replicaID) + Constants.RESPONSE_DATA_SPLITTER + OperationsType.CREATE_TR_RECORD
				+ Constants.RESPONSE_DATA_SPLITTER  + getServerLoc(managerID) + Constants.RESPONSE_DATA_SPLITTER
				+ managerID + Constants.RESPONSE_DATA_SPLITTER  + teacher;

		 */
        logUtil.log("Preparing Multicast request for Create Teacher record : " + builder);
        sendMulticastRequest(builder.toString());
        return "";
    }

    @Override
    public String createSRecord(String id, String fName, String lName, String courses, boolean status, String statusDate) {
		/*
		student = Integer.toString(replicaID) + Constants.RESPONSE_DATA_SPLITTER  + OperationsType.CREATE_SR_RECORD
				+ Constants.RESPONSE_DATA_SPLITTER  + getServerLoc(managerID) + Constants.RESPONSE_DATA_SPLITTER
				+ managerID + Constants.RESPONSE_DATA_SPLITTER  + student;

		 */
        System.out.println(LOG_TAG + "createSRecord: " + id + " " + fName + " " + lName);
        StringBuilder builder = new StringBuilder();
        builder.append(OperationsType.CREATE_SR_RECORD);
        builder.append(Constants.RESPONSE_DATA_SPLITTER);
        builder.append(id);
        builder.append("|");
        builder.append(fName);
        builder.append("|");
        builder.append(lName);
        builder.append("|");
        builder.append(courses);
        builder.append("|");
        builder.append(status);
        builder.append("|");
        builder.append(statusDate);
        builder.append("|");
        logUtil.log(LOG_TAG + " xxxxxxxxxxxxxxxxxxx: " + builder);

        sendMulticastRequest(builder.toString());
        logUtil.log("Preparing Multicast request for Create Student record : " + builder);
        return "";
    }

    @Override
    public String getRecordCounts(String id) {
        return null;
    }

    @Override
    public String editRecord(String id, String recordID, String fieldName, String newValue) {
		/*
		String editData = Integer.toString(replicaID) + Constants.RESPONSE_DATA_SPLITTER + ServerOperations.EDIT_RECORD
				+ Constants.RESPONSE_DATA_SPLITTER + getServerLoc(managerID) + Constants.RESPONSE_DATA_SPLITTER
				+ managerID + Constants.RESPONSE_DATA_SPLITTER + recordID + Constants.RESPONSE_DATA_SPLITTER
				+ fieldname + Constants.RESPONSE_DATA_SPLITTER + newvalue;

		 */
        System.out.println(id + " " + recordID + " " + fieldName + " " + newValue);
        StringBuilder builder = new StringBuilder();
        builder.append(OperationsType.EDIT_RECORD);
        builder.append(Constants.RESPONSE_DATA_SPLITTER);
        builder.append(id);
        builder.append("|");
        builder.append(recordID);
        builder.append("|");
        builder.append(fieldName);
        builder.append("|");
        builder.append(newValue);
        builder.append("|");
        logUtil.log(LOG_TAG + " dispatching editRecord to server : " + builder);
        sendMulticastRequest(builder.toString());
        logUtil.log("Preparing Multicast request for editRecord : " + builder);
        return "";
    }

    @Override
    public String transferRecord(String id, String recordId, String remoteCenterServerName) {
		/*String req = Integer.toString(replicaID) + Constants.RESPONSE_DATA_SPLITTER + ServerOperations.TRANSFER_RECORD
				+ Constants.RESPONSE_DATA_SPLITTER + getServerLoc(managerID) + Constants.RESPONSE_DATA_SPLITTER
				+ managerID + Constants.RESPONSE_DATA_SPLITTER + recordID + Constants.RESPONSE_DATA_SPLITTER
				+ remoteCenterServerName;*/
        System.out.println(LOG_TAG + "transferRecord : " + id + " " + recordId + " " + remoteCenterServerName);
        StringBuilder builder = new StringBuilder();
        builder.append(OperationsType.TRANSFER_RECORD);
        builder.append(Constants.RESPONSE_DATA_SPLITTER);
        builder.append(id);
        builder.append("|");
        builder.append(recordId);
        builder.append("|");
        builder.append(remoteCenterServerName);
        builder.append("|");
        logUtil.log(LOG_TAG + " dispatching transferRecord to server : " + builder);
        //return dispatchToCurrentServer(builder.toString());
        sendMulticastRequest(builder.toString());
        logUtil.log("Preparing Multicast request for transferRecord : " + builder);
        return "";

    }

    @Override
    public String killPrimaryServer(String id) {
        return "fail";
    }


}