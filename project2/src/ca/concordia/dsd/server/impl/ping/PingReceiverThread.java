package ca.concordia.dsd.server.impl.ping;

import ca.concordia.dsd.server.frontend.FrontEnd;
import ca.concordia.dsd.util.LogUtil;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.SocketException;

public class PingReceiverThread extends Thread {

    private final String TAG = "|" + PingReceiverThread.class.getSimpleName() + "| ";
    private DatagramSocket socket = null;
    private String name;
    private boolean isAlive;
    private Object lock;


    public PingReceiverThread(boolean isAlive, String name, int port, LogUtil logUtil) {
        try {
            this.isAlive = isAlive;
            this.name = name;
            logUtil.log(TAG + "Server " + name + " is receiving ping message using port : " + port);
            socket = new DatagramSocket(port);
            lock = new Object();
        } catch (SocketException e) {
            e.printStackTrace();
        }
    }

    public void run() {
        byte[] data = new byte[100];
        while (getStatus()) {
            try {
                DatagramPacket dp = new DatagramPacket(data, data.length);
                socket.receive(dp);
                synchronized (lock) {
                    FrontEnd.reportingMap.put(name, System.currentTimeMillis());
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private boolean getStatus() {
        return isAlive;
    }

    public void setStatus(boolean value) {
        isAlive = value;
    }
}
