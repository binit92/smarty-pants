package ca.concordia.dsd.server.frontend.helper;

import ca.concordia.dsd.server.frontend.FrontEnd;
import ca.concordia.dsd.util.Constants;
import ca.concordia.dsd.util.LogUtil;
import ca.concordia.dsd.util.OperationsEnum;
import ca.concordia.dsd.server.impl.CenterServer;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.util.Arrays;
import java.util.logging.Logger;

public class RequestThread extends Thread {
    String currentOperationData;
    CenterServer server;
    String response;
    LogUtil logUtil;

    public RequestThread(byte[] operationData, LogUtil logUtil) {
        this.currentOperationData = new String(operationData);
        this.server = null;
        response = null;
        this.logUtil = logUtil;
    }


    public void run() {
        String[] dataArr;
        String[] dataToBeSent = this.currentOperationData.trim().split(Constants.RECEIVED_DATA_SEPERATOR);
        OperationsEnum oprn = OperationsEnum.valueOf(dataToBeSent[0]);
        String requestId = dataToBeSent[dataToBeSent.length - 1];
        System.out.println("Currently serving request with id :: " + requestId);

        switch (oprn) {
            case CREATE_T_RECORD:
                System.out.println(dataToBeSent[1]);
                this.server = chooseServer(dataToBeSent[1]);
                dataArr = Arrays.copyOfRange(dataToBeSent, 3, dataToBeSent.length);
                String teacherData = String.join(Constants.RECEIVED_DATA_SEPERATOR, dataArr);
                response = this.server.createTRecord(dataToBeSent[2], teacherData);
                sendReply(requestId, response);
                break;
            case CREATE_S_RECORD:
                this.server = chooseServer(dataToBeSent[1]);
                dataArr = Arrays.copyOfRange(dataToBeSent, 3, dataToBeSent.length);
                String studentData = String.join(Constants.RECEIVED_DATA_SEPERATOR, dataArr);
                response = this.server.createSRecord(dataToBeSent[2], studentData);
                sendReply(requestId, response);
                break;
            case GET_REC_COUNT:
                this.server = chooseServer(dataToBeSent[1]);
                response = this.server
                        .getRecordCount(dataToBeSent[2] + Constants.RECEIVED_DATA_SEPERATOR + dataToBeSent[3]);
                System.out.println("Received response here ::::::::::::::::::::::: " + response);
                sendReply(requestId, response);
                break;
            case EDIT_RECORD:
                this.server = chooseServer(dataToBeSent[1]);
                String newdata = dataToBeSent[5] + Constants.RECEIVED_DATA_SEPERATOR + dataToBeSent[6];
                response = this.server.editRecord(dataToBeSent[2], dataToBeSent[3], dataToBeSent[4], newdata);
                sendReply(requestId, response);
                break;
            case TRANSFER_RECORD:
                this.server = chooseServer(dataToBeSent[1]);
                String newdata1 = dataToBeSent[4] + Constants.RECEIVED_DATA_SEPERATOR + dataToBeSent[5];
                response = this.server.transferRecord(dataToBeSent[2], dataToBeSent[3], newdata1);
                sendReply(requestId, response);
                break;
        }
    }

    public String getResponse() {
        return response;
    }


    private synchronized CenterServer chooseServer(String loc) {
        return FrontEnd.repo.get(Constants.PRIMARY_SERVER_ID).get(loc);
    }


    private void sendReply(String requestId, String response) {
        DatagramSocket ds;
        try {
            ds = new DatagramSocket();
            response = response + Constants.RESPONSE_DATA_SEPERATOR + requestId;
            byte[] dataBytes = response.getBytes();
            DatagramPacket dp = new DatagramPacket(dataBytes, dataBytes.length,
                    InetAddress.getByName(Constants.FRONT_END_IP), Constants.FRONT_END_UDP_PORT);
            ds.send(dp);
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }
}
