package ca.concordia.dsd.server.impl;

import ca.concordia.dsd.conf.ServerCenterLocation;
import ca.concordia.dsd.util.LogUtil;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;


public class DcmsServerUDPReceiver extends Thread {

    private final String TAG = "|" + DcmsServerUDPReceiver.class.getSimpleName() + "| ";
    DatagramSocket serverSocket;
    DatagramPacket receivePacket;
    DatagramPacket sendPacket;
    int udpPortNum;
    ServerCenterLocation location;
    String recordCount;
    DcmsServerImpl server;
    int c;
    boolean isAlive;
    private final LogUtil loggerInstance;


    public DcmsServerUDPReceiver(boolean isAlive, int udpPort, ServerCenterLocation loc, LogUtil logger,
                                 DcmsServerImpl serverImp) {
        location = loc;
        loggerInstance = logger;
        this.server = serverImp;
        this.isAlive = isAlive;
        c = 0;
        try {
            serverSocket = new DatagramSocket(udpPort);
            udpPortNum = udpPort;
            logger.log(TAG + loc.toString() + " UDP Server Started");
        } catch (IOException e) {
            logger.log(TAG + e.getMessage());
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
                new DcmsServerUDPRequestServer(receivePacket, server, loggerInstance).start();
                loggerInstance.log(TAG + "2 Received in udp receiver " + inputPkt + " from " + location);
            } catch (Exception e) {
            }
        }
    }


    public void killUDPReceiver() {
        isAlive = false;
    }
}
