package ca.concordia.dsd.server.impl.multicast;

import ca.concordia.dsd.server.impl.replica.ReplicaRequestThread;
import ca.concordia.dsd.server.impl.replica.ReplicaStatusThread;
import ca.concordia.dsd.util.Constants;
import ca.concordia.dsd.util.LogUtil;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.MulticastSocket;

public class MultiCastReceiverThread extends Thread {
    private final String TAG = "|" + MultiCastReceiverThread.class.getSimpleName() + "| ";

    private MulticastSocket socket;
    private InetAddress addr;
    private boolean isPrimary;
    private LogUtil logUtil;


    public MultiCastReceiverThread(boolean isPrimary, LogUtil ackManager) {
        try {
            socket = new MulticastSocket(Constants.MULTICAST_PORT_NUMBER);
            addr = InetAddress.getByName(Constants.MULTICAST_IP_ADDRESS);
            socket.joinGroup(addr);
            this.isPrimary = isPrimary;
            this.logUtil = ackManager;
        } catch (IOException e) {
            logUtil.log(TAG + "error : " + e.getMessage());
        }
    }

    public synchronized void run() {
        try {
            while (true) {
                byte[] mydata = new byte[100];
                DatagramPacket packet = new DatagramPacket(mydata, mydata.length);
                socket.receive(packet);
                if (!isPrimary) {

                    logUtil.log(TAG + " received data in multicast received");
                    logUtil.log(TAG + " sending ACK back to primary server");

                    startACKThread(packet);
                    startReplicaRequestThread(packet);
                }
            }
        } catch (IOException e) {
            logUtil.log(TAG + "error " + e.getMessage());
            System.out.println(e.getMessage());
        }
    }

    private void startACKThread(DatagramPacket packet) {
        ReplicaStatusThread ack = new ReplicaStatusThread(
                new String(packet.getData()), logUtil);
        ack.start();
    }

    private void startReplicaRequestThread(DatagramPacket packet) {
        ReplicaRequestThread req = new ReplicaRequestThread(
                new String(packet.getData()), logUtil);
        req.start();
    }
}
