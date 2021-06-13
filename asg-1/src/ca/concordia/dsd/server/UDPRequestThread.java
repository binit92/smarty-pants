package ca.concordia.dsd.server;

import ca.concordia.dsd.database.Records;
import ca.concordia.dsd.util.LogUtil;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.SocketException;
import java.util.List;
import java.util.Map;

public class UDPRequestThread extends Thread {
    private DatagramSocket serverSocket;
    private DatagramPacket receivePacket;
    private CenterServerImpl server;
    private LogUtil logUtil;
    private String serverName;


    public UDPRequestThread(DatagramPacket pkt, CenterServerImpl serverImp, String name) {
        this.receivePacket = pkt;
        this.server = serverImp;
        this.serverName = name;
        this.logUtil = new LogUtil(serverName);
        try {
            serverSocket = new DatagramSocket();
        } catch (SocketException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void run() {
        byte[] responseData;
        try {
            String inputPkt = new String(receivePacket.getData()).trim();
            if (inputPkt.equals("GET_RECORD_COUNT")) {
                //System.out.println("Got record count pkt");
                responseData = Integer.toString(getRecCount()).getBytes();
                serverSocket.send(new DatagramPacket(responseData, responseData.length, receivePacket.getAddress(),
                        receivePacket.getPort()));
            }
            logUtil.log("Received " + inputPkt + " from " + server);
        } catch (Exception e) {
			logUtil.log(e.getMessage());
        }
    }

    private int getRecCount() {
        int count = 0;
        for (Map.Entry<String, List<Records>> entry : server.recordsMap.entrySet()) {
            List<Records> list = entry.getValue();
            count += list.size();
            System.out.println(entry.getKey() + " " + list.size());
        }
        return count;
    }
}
