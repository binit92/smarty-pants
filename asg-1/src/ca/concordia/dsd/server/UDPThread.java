package ca.concordia.dsd.server;

import ca.concordia.dsd.util.Constants;
import ca.concordia.dsd.util.LogUtil;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;

public class UDPThread extends Thread implements Constants {
    public int udpPortNum;
    private DatagramSocket serverSocket;
    private DatagramPacket receivePacket;
    private DatagramPacket sendPacket;
    private LogUtil logUtil;
    private String recordCount;
    private CenterServerImpl server;
    private int c;
    private String name;

    public UDPThread(String name, CenterServerImpl server) {
        name = name;
        logUtil = new LogUtil(name);
        this.server = server;
        c = 0;
        try {
            switch (name) {
                case MTL_TAG:
                    serverSocket = new DatagramSocket(MTL_UDP_PORT);
                    udpPortNum = Constants.MTL_UDP_PORT;
                    logUtil.log("MTL UDP Server Started");
                    break;
                case LVL_TAG:
                    serverSocket = new DatagramSocket(LVL_UDP_PORT);
                    udpPortNum = LVL_UDP_PORT;
                    logUtil.log("LVL UDP Server Started");
                    break;
                case DDO_TAG:
                    serverSocket = new DatagramSocket(DDO_UDP_PORT);
                    udpPortNum = DDO_UDP_PORT;
                    logUtil.log("DDO UDP Server Started");
                    break;
            }

        } catch (IOException e) {
            logUtil.log(e.getMessage());
        }
    }

    @Override
    public void run() {
        byte[] receiveData;
        //System.out.println(c+" "+location);
        while (true) {
            try {
                receiveData = new byte[1024];
                receivePacket = new DatagramPacket(receiveData, receiveData.length);
                serverSocket.receive(receivePacket);
                System.out.println("Received pkt :: " + new String(receivePacket.getData()));
                String inputPkt = new String(receivePacket.getData()).trim();
                new UDPRequestThread(receivePacket, server,name).start();
                logUtil.log("Received " + inputPkt + " from " + server);
            } catch (Exception e) {
            }
        }
    }
}
