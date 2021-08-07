package ca.concordia.dsd.server.impl;

import ca.concordia.dsd.util.Constants;
import ca.concordia.dsd.conf.ServerCenterLocation;
import ca.concordia.dsd.server.frontend.TransferResponseToFE;
import ca.concordia.dsd.util.LogUtil;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.SocketException;
import java.util.HashMap;


public class DcmsServerReplicaResponseReceiver extends Thread {
    private final String TAG = "|" + DcmsServerReplicaResponseReceiver.class.getSimpleName() + "| ";

    DatagramSocket serverSocket;
    DatagramPacket receivePacket;
    DatagramPacket sendPacket;
    int udpPortNum;
    ServerCenterLocation location;
    LogUtil loggerInstance;
    String recordCount;
    HashMap<Integer, TransferResponseToFE> responses;
    int c;

    public DcmsServerReplicaResponseReceiver(LogUtil logManager) {
        try {
            loggerInstance = logManager;
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
                    loggerInstance.log(TAG + inputPkt);
                } else {
                    System.out.println("Received response packet in PRIMARY:: " + new String(receivedData));
                    loggerInstance.log(TAG + "Received response in Primary " + inputPkt);
                }
            } catch (Exception e) {

            }
        }
    }
}
