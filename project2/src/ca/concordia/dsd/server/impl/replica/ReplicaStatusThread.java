package ca.concordia.dsd.server.impl.replica;

import ca.concordia.dsd.util.Constants;
import ca.concordia.dsd.util.LogUtil;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;

public class ReplicaStatusThread extends Thread {
    private final String TAG = "|" + ReplicaStatusThread.class.getSimpleName() + "| ";

    private String request;
    private DatagramSocket ds;
    private LogUtil logUtil;

    public ReplicaStatusThread(String request, LogUtil logManger) {
        request = "RECEIVED ACKNOWLEDGEMENT IN PRIMARY :: " + request;
        this.request = request;
        this.logUtil = logManger;
    }


    public synchronized void run() {
        try {
            ds = new DatagramSocket();
            byte[] dataBytes = request.getBytes();
            DatagramPacket dp = new DatagramPacket(dataBytes, dataBytes.length,
                    InetAddress.getByName(InetAddress.getLocalHost().getHostAddress()),
                    Constants.CURRENT_PRIMARY_PORT_FOR_REPLICAS);
            ds.send(dp);
        } catch (SocketException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }
}
