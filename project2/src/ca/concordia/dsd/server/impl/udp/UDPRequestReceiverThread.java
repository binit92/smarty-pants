package ca.concordia.dsd.server.impl.udp;

import ca.concordia.dsd.server.impl.CenterServer;
import ca.concordia.dsd.util.LocationEnum;
import ca.concordia.dsd.util.LogUtil;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;


public class UDPRequestReceiverThread extends Thread {

    private final String TAG = "|" + UDPRequestReceiverThread.class.getSimpleName() + "| ";
    private DatagramSocket serverSocket;
    private DatagramPacket receivePacket;
    private int udpPortNum;
    private LocationEnum location;
    private CenterServer server;
    private int c;
    private boolean isAlive;
    private LogUtil logUtil;

    public UDPRequestReceiverThread(boolean isAlive, int udpPort, LocationEnum loc, LogUtil logUtil,
                                    CenterServer serverImp) {
        location = loc;
        this.logUtil = logUtil;
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
                String inputPkt = new String(receivePacket.getData()).trim();
                new UDPRequestSenderThread(receivePacket, server, logUtil).start();
                logUtil.log(TAG + "received packet :  " + inputPkt + " from datacenter : " + location);
            } catch (Exception e) {
            }
        }
    }


    public void killUDPReceiver() {
        isAlive = false;
    }
}
