package ca.concordia.dsd.server.impl.replica;

import ca.concordia.dsd.server.impl.CenterServer;
import ca.concordia.dsd.util.Constants;
import ca.concordia.dsd.util.OperationsEnum;
import ca.concordia.dsd.server.frontend.FrontEnd;
import ca.concordia.dsd.util.LogUtil;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.util.Arrays;

public class ReplicaRequestThread extends Thread {
    private final String TAG = "|" + ReplicaRequestThread.class.getSimpleName() + "| ";
    String currentOperationData;
    CenterServer server;
    String response;
    LogUtil logUtil;

    public ReplicaRequestThread(String operationData, LogUtil logManager) {
        this.currentOperationData = operationData;
        this.server = null;
        response = null;
        this.logUtil = logManager;
    }


    public synchronized void run() {
        String[] dataArr;
        String[] dataToBeSent = this.currentOperationData.trim().split(Constants.RECEIVED_DATA_SEPERATOR);

        Integer replicaId = Integer.parseInt(dataToBeSent[0]);
        System.out.println("====================Currently serving replica with ID :: " + replicaId);
        OperationsEnum oprn = OperationsEnum.valueOf(dataToBeSent[1]);

        String requestId = dataToBeSent[dataToBeSent.length - 1];
        System.out.println("Currently serving request with id :: " + requestId);

        switch (oprn) {
            case CREATE_T_RECORD:
                this.server = chooseServer(replicaId, dataToBeSent[2]);
                dataArr = Arrays.copyOfRange(dataToBeSent, 4, dataToBeSent.length);
                String teacherData = String.join(Constants.RECEIVED_DATA_SEPERATOR, dataArr);
                response = this.server.createTRecord(dataToBeSent[3], teacherData);
                sendReply(response);
                break;
            case CREATE_S_RECORD:
                this.server = chooseServer(replicaId, dataToBeSent[2]);
                dataArr = Arrays.copyOfRange(dataToBeSent, 4, dataToBeSent.length);
                String studentData = String.join(Constants.RECEIVED_DATA_SEPERATOR, dataArr);
                response = this.server.createSRecord(dataToBeSent[3], studentData);
                sendReply(response);
                break;
            case GET_REC_COUNT:
                this.server = chooseServer(replicaId, dataToBeSent[2]);
                response = this.server
                        .getRecordCount(dataToBeSent[3] + Constants.RECEIVED_DATA_SEPERATOR + dataToBeSent[4]);
                sendReply(response);
                break;
            case EDIT_RECORD:
                this.server = chooseServer(replicaId, dataToBeSent[2]);
                String newdata = dataToBeSent[6] + Constants.RECEIVED_DATA_SEPERATOR + dataToBeSent[7];
                response = this.server.editRecord(dataToBeSent[3], dataToBeSent[4], dataToBeSent[5], newdata);
                //System.out.println("=======================================RESPONSE :: " + response);
                sendReply(response);
                break;
            case TRANSFER_RECORD:
                this.server = chooseServer(replicaId, dataToBeSent[2]);
                String newdata1 = dataToBeSent[5] + Constants.RECEIVED_DATA_SEPERATOR + dataToBeSent[6];
                response = this.server.transferRecord(dataToBeSent[3], dataToBeSent[4], newdata1);
                sendReply(response);
                break;
        }
    }

    public synchronized String getResponse() {
        return response;
    }

    private synchronized CenterServer chooseServer(int replicaId, String loc) {
        return FrontEnd.repo.get(replicaId).get(loc);
    }

    private synchronized void sendReply(String response) {
        DatagramSocket ds;
        try {
            ds = new DatagramSocket();
            byte[] dataBytes = response.getBytes();
            DatagramPacket dp = new DatagramPacket(dataBytes, dataBytes.length,
                    InetAddress.getByName(InetAddress.getLocalHost().getHostAddress()),
                    Constants.CURRENT_PRIMARY_PORT_FOR_REPLICAS);
            ds.send(dp);
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }
}
