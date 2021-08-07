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

    DatagramSocket serverSocket;
    DatagramPacket receivePacket;
    DatagramPacket sendPacket;
    int udpPortNum;
    LocationEnum location;
    LogUtil logUtil;
    String recordCount;
    HashMap<Integer, ResponseThread> responses;
    int c;

    public ReplicaResponseThread(LogUtil logUtil) {
        try {
            logUtil = logUtil;
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
                    System.out.println(new String(receivedData));
                    logUtil.log(TAG + inputPkt);
                } else {
                    System.out.println("Received response packet in PRIMARY:: " + new String(receivedData));
                    logUtil.log(TAG + "Received response in Primary " + inputPkt);
                }
            } catch (Exception e) {

            }
        }
    }
}
