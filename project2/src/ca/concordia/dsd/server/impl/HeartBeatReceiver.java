package ca.concordia.dsd.server.impl;

import ca.concordia.dsd.server.frontend.DcmsServerFE;
import ca.concordia.dsd.util.LogUtil;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.SocketException;

public class HeartBeatReceiver extends Thread {

    private final String TAG = "|" + HeartBeatReceiver.class.getSimpleName() + "| ";
    DatagramSocket ds = null;
    String name;
    boolean isAlive;
    Object mapAccessor;


    public HeartBeatReceiver(boolean isAlive, String name, int port, LogUtil logUtil) {
        try {
            this.isAlive = isAlive;
            this.name = name;
            System.out.println(name + "listening in :: " + port);
            ds = new DatagramSocket(port);
            mapAccessor = new Object();
        } catch (SocketException e) {
            e.printStackTrace();
        }
    }

    public void run() {
        byte[] data = new byte[100];
        while (getStatus()) {
            try {
                DatagramPacket dp = new DatagramPacket(data, data.length);
                ds.receive(dp);
                synchronized (mapAccessor) {
                    DcmsServerFE.server_last_updated_time.put(name, System.nanoTime() / 1000000);
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
