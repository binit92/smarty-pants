package ca.concordia.dsd.server.impl;

import ca.concordia.dsd.server.frontend.TransferResponseThread;
import ca.concordia.dsd.util.Constants;
import ca.concordia.dsd.util.LogUtil;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.SocketException;
import java.util.HashMap;

/*
 * Thread class that receives the acknowledgement or response from the replicas
 * and passes it on to the primary server and
 */
public class DcmsServerReplicaResponseReceiver extends Thread {

    DatagramSocket serverSocket;
    DatagramPacket receivePacket;
    DatagramPacket sendPacket;
    int udpPortNum;
    String location;
    LogUtil logUtil;
    String recordCount;
    HashMap<Integer, TransferResponseThread> responses;
    int c;

    public DcmsServerReplicaResponseReceiver(LogUtil logUtil) {
        try {
            this.logUtil = logUtil;
            serverSocket = new DatagramSocket(Constants.CURRENT_PRIMARY_PORT_FOR_REPLICAS);
        } catch (SocketException e) {
            System.out.println(e.getMessage());
        }
    }

    /*
     * (non-Javadoc)
     * @see java.lang.Thread#run()
     */
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
                    logUtil.log(inputPkt);
                } else {
                    System.out.println("Received response packet in PRIMARY:: " + new String(receivedData));
                    logUtil.log("Received response in Primary " + inputPkt);
                }
            } catch (Exception e) {

            }
        }
    }
}
