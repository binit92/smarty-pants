package ca.concordia.dsd.server.impl.udp;

import ca.concordia.dsd.server.impl.CenterServer;
import ca.concordia.dsd.util.LocationEnum;
import ca.concordia.dsd.util.LogUtil;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;


public class UDPRequestReceiverThread extends Thread {

    private final String TAG = "|" + UDPRequestReceiverThread.class.getSimpleName() + "| ";
    DatagramSocket serverSocket;
    DatagramPacket receivePacket;
    DatagramPacket sendPacket;
    int udpPortNum;
    LocationEnum location;
    String recordCount;
    CenterServer server;
    int c;
    boolean isAlive;
    private LogUtil logUtil;


    public UDPRequestReceiverThread(boolean isAlive, int udpPort, LocationEnum loc, LogUtil logUtil,
                                    CenterServer serverImp) {
        location = loc;
        logUtil = logUtil;
        this.server = serverImp;
        this.isAlive = isAlive;
        c = 0;
        try {
            serverSocket = new DatagramSocket(udpPort);
            udpPortNum = udpPort;
            logUtil.log(TAG + loc.toString() + " UDP Server Started");
        } catch (IOException e) {
            logUtil.log(TAG + e.getMessage());
        }
    }


    public synchronized void run() {
        byte[] receiveData;
        while (isAlive) {
            try {
                receiveData = new byte[1024];
                receivePacket = new DatagramPacket(receiveData, receiveData.length);
                serverSocket.receive(receivePacket);
                System.out.println("LOc :: " + location + "1 Received pkt in udp Receiver :: "
                        + new String(receivePacket.getData()));
                String inputPkt = new String(receivePacket.getData()).trim();
                new UDPRequestSenderThread(receivePacket, server, logUtil).start();
                logUtil.log(TAG + "2 Received in udp receiver " + inputPkt + " from " + location);
            } catch (Exception e) {
            }
        }
    }


    public void killUDPReceiver() {
        isAlive = false;
    }
}
