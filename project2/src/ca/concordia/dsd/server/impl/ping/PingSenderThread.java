package ca.concordia.dsd.server.impl.ping;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;


public class PingSenderThread extends Thread {

    private final String TAG = "|" + PingSenderThread.class.getSimpleName() + "| ";

    private final int port1;
    private final int port2;
    private final String name;
    private final DatagramSocket socket;

    public PingSenderThread(DatagramSocket socket, String name, int port1, int port2) {
        this.port1 = port1;
        this.port2 = port2;
        this.name = name;
        this.socket = socket;
    }

    public void run() {
        byte[] dataBytes = name.getBytes();
        DatagramPacket packet;
        try {
            packet = new DatagramPacket(dataBytes,
                    dataBytes.length,
                    InetAddress.getByName("localhost"),
                    port1);
            socket.send(packet);
            packet = new DatagramPacket(dataBytes,
                    dataBytes.length,
                    InetAddress.getByName("localhost"),
                    port2);
            socket.send(packet);
        } catch (Exception e) {

            e.printStackTrace();
        }
    }
}
