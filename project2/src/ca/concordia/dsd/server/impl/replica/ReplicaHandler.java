package ca.concordia.dsd.server.impl.replica;

import ca.concordia.dsd.database.Record;
import ca.concordia.dsd.server.impl.multicast.MultiCastSenderThread;
import ca.concordia.dsd.util.Constants;
import ca.concordia.dsd.util.LogUtil;
import ca.concordia.dsd.util.OperationsEnum;

import java.util.HashMap;
import java.util.List;

public class ReplicaHandler {
    private final String TAG = "|" + ReplicaHandler.class.getSimpleName() + "| ";
    public HashMap<String, List<Record>> recordsMap;
    private final LogUtil logUtil;
    private final Integer requestId;
    private final HashMap<Integer, String> requestBuffer;
    private final Integer replicaID;

    public ReplicaHandler(Integer replicaID, LogUtil logUtil) {
        recordsMap = new HashMap<>();
        requestBuffer = new HashMap<>();
        requestId = 0;
        this.replicaID = replicaID;
        this.logUtil = logUtil;
    }

    private void sendMulticastRequest(String req) {
        MultiCastSenderThread sender = new MultiCastSenderThread(req, logUtil);
        sender.start();
    }

    public String createTRecord(String managerID, String teacher) {
        teacher = replicaID + Constants.RECEIVED_SPLITTER + OperationsEnum.CREATE_TR_RECORD
                + Constants.RECEIVED_SPLITTER + getServerLoc(managerID) + Constants.RECEIVED_SPLITTER
                + managerID + Constants.RECEIVED_SPLITTER + teacher;
        logUtil.log(TAG + "Preparing Multicast request for Create Teacher record : " + teacher);
        sendMulticastRequest(teacher);
        return "";
    }

    private String getServerLoc(String managerID) {
        return managerID.substring(0, 3);
    }

    public String createSRecord(String managerID, String student) {
        student = replicaID + Constants.RECEIVED_SPLITTER + OperationsEnum.CREATE_SR_RECORD
                + Constants.RECEIVED_SPLITTER + getServerLoc(managerID) + Constants.RECEIVED_SPLITTER
                + managerID + Constants.RECEIVED_SPLITTER + student;
        sendMulticastRequest(student);
        logUtil.log(TAG + "Preparing Multicast request for Create Student record : " + student);
        return "";
    }


    public String getRecordCount(String manager) {
        String[] data = manager.split(Constants.RECEIVED_SPLITTER);
        String req = replicaID + Constants.RECEIVED_SPLITTER + OperationsEnum.GET_RECORD_COUNT
                + Constants.RECEIVED_SPLITTER + getServerLoc(data[0]) + Constants.RECEIVED_SPLITTER
                + manager;
        sendMulticastRequest(req);
        logUtil.log(TAG + "Preparing Multicast request for get record Count :" + req);
        return "";
    }


    public String editRecord(String id, String recordID, String fieldName, String newValue) {
        String editData = replicaID + Constants.RECEIVED_SPLITTER + OperationsEnum.EDIT_RECORD
                + Constants.RECEIVED_SPLITTER + getServerLoc(id) + Constants.RECEIVED_SPLITTER
                + id + Constants.RECEIVED_SPLITTER + recordID + Constants.RECEIVED_SPLITTER
                + fieldName + Constants.RECEIVED_SPLITTER + newValue;
        sendMulticastRequest(editData);
        logUtil.log(TAG + "Preparing Multicast request for editRecord : " + editData);
        return "";
    }


    public String transferRecord(String managerID, String recordID, String remoteCenterServerName) {
        String req = replicaID + Constants.RECEIVED_SPLITTER + OperationsEnum.TRANSFER_RECORD
                + Constants.RECEIVED_SPLITTER + getServerLoc(managerID) + Constants.RECEIVED_SPLITTER
                + managerID + Constants.RECEIVED_SPLITTER + recordID + Constants.RECEIVED_SPLITTER
                + remoteCenterServerName;
        sendMulticastRequest(req);
        logUtil.log(TAG + "Preparing Multicast request for transferRecord : " + req);
        return "";
    }

}