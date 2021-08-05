package ca.concordia.dsd.server;

import ca.concordia.dsd.server.impl.CenterServerImpl;
import ca.concordia.dsd.util.Constants;
import ca.concordia.dsd.util.LogUtil;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;

public class UDPThread extends Thread implements Constants {
    private final String LOG_TAG = "| " + UDPThread.class.getSimpleName() + " | ";
    public int udpPortNum;
    private DatagramSocket serverSocket;
    private DatagramPacket receivePacket;
    private DatagramPacket sendPacket;
    private final LogUtil logUtil;
    private String recordCount;
    private final CenterServerImpl server;
    private final int c;
    private final String name;

    public UDPThread(String name, CenterServerImpl server) {
        this.name = name;
        logUtil = new LogUtil(name);
        this.server = server;
        c = 0;
        try {
            switch (name) {
                case MTL_TAG:
                    serverSocket = new DatagramSocket(MTL_UDP_PORT_LEADER);
                    udpPortNum = Constants.MTL_UDP_PORT_LEADER;
                    logUtil.log("MTL UDP Server Started");
                    break;
                case LVL_TAG:
                    serverSocket = new DatagramSocket(LVL_UDP_PORT_LEADER);
                    udpPortNum = LVL_UDP_PORT_LEADER;
                    logUtil.log("LVL UDP Server Started");
                    break;
                case DDO_TAG:
                    serverSocket = new DatagramSocket(DDO_UDP_PORT_LEADER);
                    udpPortNum = DDO_UDP_PORT_LEADER;
                    logUtil.log("DDO UDP Server Started");
                    break;
            }

        } catch (IOException e) {
            System.out.println(LOG_TAG + e.getMessage());
            logUtil.log(e.getMessage());
        }
    }

    @Override
    public void run() {
        byte[] receiveData;
        while (true) {
            try {
                receiveData = new byte[1024];
                receivePacket = new DatagramPacket(receiveData, receiveData.length);
                serverSocket.setSoTimeout(1000 * 5);
                serverSocket.receive(receivePacket);
                logUtil.log(name, "UDPThread, received pkt :: " + new String(receivePacket.getData()));
                String inputPkt = new String(receivePacket.getData()).trim();
                new UDPRequestThread(receivePacket, server, name).start();
                logUtil.log(name, "UDPThread, Received " + inputPkt + " from " + server);
            } catch (Exception e) {
            }
        }
    }
}
