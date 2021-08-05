package ca.concordia.dsd.server.impl;

import ca.concordia.dsd.util.LogUtil;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;


/**
 * DcmsServerUDPReceiver is the class that serves other servers' requests in
 * form of UDP communication with RECPAKT functionality
 */

public class DcmsServerUDPReceiver extends Thread {
    DatagramSocket serverSocket;
    DatagramPacket receivePacket;
    DatagramPacket sendPacket;
    int udpPortNum;
    String location;
    String recordCount;
    CenterServerImpl server;
    int c;
    boolean isAlive;
    private LogUtil logUtil;

    public DcmsServerUDPReceiver(boolean isAlive, int udpPort, String loc, LogUtil logUtil,
                                 CenterServerImpl serverImp) {
        location = loc;
        logUtil = logUtil;
        this.server = serverImp;
        this.isAlive = isAlive;
        c = 0;
        try {
            serverSocket = new DatagramSocket(udpPort);
            udpPortNum = udpPort;
            logUtil.log(loc + " UDP Server Started");
        } catch (IOException e) {
            logUtil.log(e.getMessage());
        }
    }

    /*
     * (non-Javadoc)
     * @see java.lang.Thread#run()
     */
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
                //new DcmsServerUDPRequestServer(receivePacket, server, logUtil).start();
                logUtil.log("2 Received in udp receiver " + inputPkt + " from " + location);
            } catch (Exception e) {
            }
        }
    }

    /*
     * Kills the current UDP receiver by assigning the isAlive flag to false
     */
    public void killUDPReceiver() {
        isAlive = false;
    }
}
