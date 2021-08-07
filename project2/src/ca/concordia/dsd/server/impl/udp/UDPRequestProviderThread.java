package ca.concordia.dsd.server.impl.udp;

import ca.concordia.dsd.database.Record;
import ca.concordia.dsd.server.impl.CenterServer;
import ca.concordia.dsd.util.LogUtil;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;

public class UDPRequestProviderThread extends Thread {
    private final String TAG = "|" + UDPRequestProviderThread.class.getSimpleName() + "| ";

    LogUtil logUtil;
    private String recordCount = "";
    private String transferResult = "";
    private final CenterServer server;
    private final String requestType;
    private final Record recordForTransfer;


    public UDPRequestProviderThread(CenterServer server, String requestType,
                                    Record recordForTransfer, LogUtil logUtil) throws IOException {
        this.server = server;
        this.requestType = requestType;
        this.recordForTransfer = recordForTransfer;
        this.logUtil = logUtil;
    }

    public String getRemoteRecordCount() {
        return recordCount;
    }

    public String getTransferResult() {
        return transferResult;
    }


    public synchronized void run() {
        DatagramSocket socket = null;
        try {
            System.out.println("Req type :: " + requestType);
            switch (requestType) {
                case "GET_RECORD_COUNT":
                    socket = new DatagramSocket();
                    byte[] data = "GET_RECORD_COUNT".getBytes();
                    System.out.println("data in udp req provider :: " + new String(data));
                    System.out.println("port here :: " + server.locUDPPort);
                    DatagramPacket packet = new DatagramPacket(data, data.length,
                            InetAddress.getByName(InetAddress.getLocalHost().getHostAddress()),
                            server.locUDPPort);
                    socket.send(packet);
                    data = new byte[100];
                    socket.receive(new DatagramPacket(data, data.length));
                    recordCount = server.location + " " + new String(data);
                    break;
                case "TRANSFER_RECORD":
                    socket = new DatagramSocket();
                    byte[] data1 = ("TRANSFER_RECORD" + "#"
                            + recordForTransfer.toString()).getBytes();


                    DatagramPacket packet1 = new DatagramPacket(data1, data1.length,
                            InetAddress.getByName(InetAddress.getLocalHost().getHostAddress()),
                            server.locUDPPort);
                    socket.send(packet1);
                    data1 = new byte[100];
                    socket.receive(new DatagramPacket(data1, data1.length));
                    transferResult = new String(data1);
                    System.out.println("TRANSFER IN UDP PROVIDER =============" + transferResult);
                    break;
            }
        } catch (Exception e) {
            System.out.println("Exception :::::::::::::::::::::" + e.getMessage());
        } finally {
            if (socket != null) {
                socket.close();
            }
        }
    }

}