package ca.concordia.dsd.server.frontend;

import ca.concordia.dsd.server.OperationsType;
import ca.concordia.dsd.server.impl.CenterServerImpl;
import ca.concordia.dsd.util.Constants;
import ca.concordia.dsd.util.LogUtil;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;

public class TransferRequestThread extends Thread {

    private String dataStr;
    private CenterServerImpl server;
    private String responseStr;
    private LogUtil logUtil;

    public TransferRequestThread(byte[] data, LogUtil logUtil){
        this.dataStr = new String(data);
        this.server = null;
        this.responseStr = null;
        this.logUtil = logUtil;
    }

    private synchronized CenterServerImpl pickServer(String location){
        return FrontEnd.repo.get(Constants.LEADER_ID).get(location);
    }

    private void reply(String requestId, String responseStr){
        DatagramSocket socket;
        try{
            socket =new DatagramSocket();
            responseStr += Constants.RESPONSE_DATA_SPLITTER +requestId;
            byte[] data = responseStr.getBytes();
            DatagramPacket sPacket = new DatagramPacket(data,data.length,
                    InetAddress.getByName(Constants.FRONT_END_UDP_HOST),
                    Constants.FRONT_END_UDP_PORT);
            socket.send(sPacket);
        }catch (Exception e){
            System.out.println(e.getMessage());
        }
    }

    @Override
    public void run() {
        String[] strArr;
        String[] strToSend = this.dataStr.trim().split(Constants.RESPONSE_DATA_SPLITTER);
        OperationsType oT = OperationsType.valueOf(strToSend[0]);
        String requestId = strToSend[strToSend.length -1];
        System.out.println("processing request id : " + requestId);

        switch (oT){
            case CREATE_TR_RECORD:
                //TODO
                break;
            case CREATE_SR_RECORD:
                //TODO
                break;
            case GET_RECORD_COUNT:
                //TODO
                break;
            case EDIT_RECORD:
                //TODO
                break;
            case TRANSFER_RECORD:
                //TODO
                break;
        }
    }
}