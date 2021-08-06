package server.impl;

import arch.*;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;

import conf.Constants;
import conf.LogManager;
import conf.ServerOperations;

import model.Record;


//public class DcmsServerPrepareReplicasRequest extends corbaPOA {
public class DcmsServerPrepareReplicasRequest{
	LogManager logManager;
	Logger logger;
	String IPaddress;
	public HashMap<String, List<Record>> recordsMap;
	int studentCount = 0;
	int teacherCount = 0;
	String recordsCount;
	String location;
	Integer requestId;
	HashMap<Integer, String> requestBuffer;
	Integer replicaID;


	public DcmsServerPrepareReplicasRequest(Integer replicaID, Logger logger) {
		recordsMap = new HashMap<>();
		requestBuffer = new HashMap<>();
		requestId = 0;
		this.replicaID = replicaID;
		this.logger = logger;
	}


	private void sendMulticastRequest(String req) {
		DcmsServerMultiCastSender sender = new DcmsServerMultiCastSender(req, logger);
		sender.start();
	}

	//@Override
	public String createTRecord(String managerID, String teacher) {
		teacher = Integer.toString(replicaID) + Constants.RECEIVED_DATA_SEPERATOR + ServerOperations.CREATE_T_RECORD
				+ Constants.RECEIVED_DATA_SEPERATOR + getServerLoc(managerID) + Constants.RECEIVED_DATA_SEPERATOR
				+ managerID + Constants.RECEIVED_DATA_SEPERATOR + teacher;
		logger.log(Level.INFO, "Preparing Multicast request for Create Teacher record : " + teacher);
		sendMulticastRequest(teacher);
		return "";
	}

	private String getServerLoc(String managerID) {
		return managerID.substring(0, 3);
	}


	//@Override
	public String createSRecord(String managerID, String student) {
		student = Integer.toString(replicaID) + Constants.RECEIVED_DATA_SEPERATOR + ServerOperations.CREATE_S_RECORD
				+ Constants.RECEIVED_DATA_SEPERATOR + getServerLoc(managerID) + Constants.RECEIVED_DATA_SEPERATOR
				+ managerID + Constants.RECEIVED_DATA_SEPERATOR + student;
		sendMulticastRequest(student);
		logger.log(Level.INFO, "Preparing Multicast request for Create Student record : " + student);
		return "";
	}


	//@Override
	public String getRecordCount(String manager) {
		String data[] = manager.split(Constants.RECEIVED_DATA_SEPERATOR);
		String req = Integer.toString(replicaID) + Constants.RECEIVED_DATA_SEPERATOR + ServerOperations.GET_REC_COUNT
				+ Constants.RECEIVED_DATA_SEPERATOR + getServerLoc(data[0]) + Constants.RECEIVED_DATA_SEPERATOR
				+ manager;
		sendMulticastRequest(req);
		logger.log(Level.INFO, "Preparing Multicast request for get record Count :" + req);
		return "";
	}



	//@Override
	public String editRecord(String id, String recordID, String fieldName, String newValue) {
		String editData = Integer.toString(replicaID) + Constants.RECEIVED_DATA_SEPERATOR + ServerOperations.EDIT_RECORD
				+ Constants.RECEIVED_DATA_SEPERATOR + getServerLoc(id) + Constants.RECEIVED_DATA_SEPERATOR
				+ id + Constants.RECEIVED_DATA_SEPERATOR + recordID + Constants.RECEIVED_DATA_SEPERATOR
				+ fieldName + Constants.RECEIVED_DATA_SEPERATOR + newValue;
		sendMulticastRequest(editData);
		logger.log(Level.INFO, "Preparing Multicast request for editRecord : " + editData);
		return "";
	}


	public String transferRecord(String managerID, String recordID, String remoteCenterServerName) {
		String req = Integer.toString(replicaID) + Constants.RECEIVED_DATA_SEPERATOR + ServerOperations.TRANSFER_RECORD
				+ Constants.RECEIVED_DATA_SEPERATOR + getServerLoc(managerID) + Constants.RECEIVED_DATA_SEPERATOR
				+ managerID + Constants.RECEIVED_DATA_SEPERATOR + recordID + Constants.RECEIVED_DATA_SEPERATOR
				+ remoteCenterServerName;
		sendMulticastRequest(req);
		logger.log(Level.INFO, "Preparing Multicast request for transferRecord : " + req);
		return "";
	}

	//@Override
	public String killPrimaryServer(String id) {
		return null;
	}
}