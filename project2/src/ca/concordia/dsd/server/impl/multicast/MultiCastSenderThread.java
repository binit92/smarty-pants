package ca.concordia.dsd.server.impl.multicast;

import ca.concordia.dsd.util.Constants;
import ca.concordia.dsd.util.LogUtil;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.MulticastSocket;

public class MultiCastSenderThread extends Thread {
    private final String TAG = "|" + MultiCastSenderThread.class.getSimpleName() + "| ";

    private MulticastSocket socket;
    private InetAddress addr;
    private String data;
    private LogUtil logUtil;

    public MultiCastSenderThread(String request, LogUtil logUtil) {
        try {
            socket = new MulticastSocket(Constants.MULTICAST_PORT_NUMBER);
            addr = InetAddress.getByName(Constants.MULTICAST_IP_ADDRESS);
            socket.joinGroup(addr);
            this.logUtil = logUtil;
            this.data = request;
        } catch (IOException e) {
            logUtil.log(TAG + "error: " + e.getMessage());
        }
    }

    public synchronized void run() {
        try {
            DatagramPacket packet = new DatagramPacket(data.getBytes(),
                    data.length(),
                    addr,
                    Constants.MULTICAST_PORT_NUMBER);
            logUtil.log(TAG + " sending multicast, data : " + data);
            socket.send(packet);
        } catch (IOException e) {
            logUtil.log(TAG + "error: " + e.getMessage());
        }
    }
}
