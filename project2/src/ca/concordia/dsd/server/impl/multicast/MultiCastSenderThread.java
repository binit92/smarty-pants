package ca.concordia.dsd.server.impl.multicast;

import ca.concordia.dsd.util.Constants;
import ca.concordia.dsd.util.LogUtil;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.MulticastSocket;

public class MultiCastSenderThread extends Thread {
    MulticastSocket multicastsocket;
    InetAddress address;
    String data;
    LogUtil logger;


    public MultiCastSenderThread(String request, LogUtil logger) {
        try {
            multicastsocket = new MulticastSocket(Constants.MULTICAST_PORT_NUMBER);
            address = InetAddress.getByName(Constants.MULTICAST_IP_ADDRESS);
            multicastsocket.joinGroup(address);
            this.logger = logger;
            this.data = request;
        } catch (IOException e) {
            System.out.println(e.getMessage());
        }
    }


    public synchronized void run() {
        try {
            DatagramPacket packet = new DatagramPacket(data.getBytes(), data.length(), address,
                    Constants.MULTICAST_PORT_NUMBER);
            //logger.log(Level.INFO, "Sending Multicast request" + data);
            multicastsocket.send(packet);
        } catch (IOException e) {
            System.out.println(e.getMessage());
        }
    }
}
