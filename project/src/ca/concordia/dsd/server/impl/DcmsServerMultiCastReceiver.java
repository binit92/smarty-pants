package ca.concordia.dsd.server.impl;

import ca.concordia.dsd.util.Constants;
import ca.concordia.dsd.util.LogUtil;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.MulticastSocket;

public class DcmsServerMultiCastReceiver extends Thread {
    MulticastSocket multicastsocket;
    InetAddress address;
    boolean isPrimary;
    LogUtil logUtil;

    /**
     * This constructor sets the Multicast socket port number and the instance
     * address and stores a flag for PrimaryServer
     *
     * @param isPrimary  Boolean Variable for primary server
     * @param ackManager Object for the LogManager
     */

    public DcmsServerMultiCastReceiver(boolean isPrimary, LogUtil ackManager) {
        try {
            multicastsocket = new MulticastSocket(Constants.MULTICAST_PORT_NUMBER);
            address = InetAddress.getByName(Constants.MULTICAST_IP_ADDRESS);
            multicastsocket.joinGroup(address);
            this.isPrimary = isPrimary;
            this.logUtil = ackManager;
        } catch (IOException e) {
            System.out.println(e.getMessage());
        }

    }

    /**
     * This thread receives the multicasted data on the port and performs the
     * respective operation if it is a replica.
     */
    /*
     * (non-Javadoc)
     * @see java.lang.Thread#run()
     */
    public synchronized void run() {
        try {
            while (true) {
                byte[] mydata = new byte[100];
                DatagramPacket packet = new DatagramPacket(mydata, mydata.length);
                multicastsocket.receive(packet);
                if (!isPrimary) {
                    System.out.println("Received data in multicast heartBeatReceiver " + new String(packet.getData()));
                    System.out.println("Sent the acknowledgement for the data recevied in replica to primary server "
                            + new String(packet.getData()));

                    DcmsServerReplicaAcknowledgementSender ack = new DcmsServerReplicaAcknowledgementSender(
                            new String(packet.getData()), logUtil);
                    ack.start();
                    DcmsServerReplicaRequestProcessor req = new DcmsServerReplicaRequestProcessor(
                            new String(packet.getData()), logUtil);
                    req.start();
                }
            }
        } catch (IOException e) {
            System.out.println(e.getMessage());
        }
    }
}
