package ca.concordia.dsd.server.impl;


import ca.concordia.dsd.util.Constants;
import ca.concordia.dsd.util.LogUtil;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;

public class DcmsServerReplicaAcknowledgementSender extends Thread {
    String request;
    DatagramSocket ds;

    public DcmsServerReplicaAcknowledgementSender(String request, LogUtil logUtil) {
        request = "RECEIVED ACKNOWLEDGEMENT IN PRIMARY :: " + request;
        this.request = request;
    }

    /**
     * This thread is called to send the acknowledgement from the respective
     * replicas to the primary server
     */

    /*
     * (non-Javadoc)
     * @see java.lang.Thread#run()
     */
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
