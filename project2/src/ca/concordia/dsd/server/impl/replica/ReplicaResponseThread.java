package ca.concordia.dsd.server.impl.replica;

import ca.concordia.dsd.server.frontend.helper.ResponseThread;
import ca.concordia.dsd.util.Constants;
import ca.concordia.dsd.util.LocationEnum;
import ca.concordia.dsd.util.LogUtil;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.SocketException;
import java.util.HashMap;


public class ReplicaResponseThread extends Thread {
    private final String TAG = "|" + ReplicaResponseThread.class.getSimpleName() + "| ";

    private DatagramSocket serverSocket;
    private DatagramPacket receivePacket;
    private DatagramPacket sendPacket;
    private int udpPortNum;
    private LocationEnum location;
    private LogUtil logUtil;
    private String recordCount;
    private HashMap<Integer, ResponseThread> responses;
    private int c;

    public ReplicaResponseThread(LogUtil logUtil) {
        try {
            this.logUtil = logUtil;
            serverSocket = new DatagramSocket(Constants.CURRENT_PRIMARY_PORT_FOR_REPLICAS);
        } catch (SocketException e) {
            System.out.println(e.getMessage());
        }
    }


    @Override
    public synchronized void run() {
        byte[] receiveData;
        while (true) {
            try {
                receiveData = new byte[1024];
                receivePacket = new DatagramPacket(receiveData, receiveData.length);
                serverSocket.receive(receivePacket);
                byte[] receivedData = receivePacket.getData();
                String inputPkt = new String(receivedData).trim();
                if (inputPkt.contains("ACKNOWLEDGEMENT")) {
                    logUtil.log(TAG + "ACK : " + inputPkt);
                } else {
                    logUtil.log(TAG + "Received response in Primary " + inputPkt);
                }
            } catch (Exception e) {

            }
        }
    }
}
