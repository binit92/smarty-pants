package ca.concordia.dsd.server;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.util.logging.Level;
import java.util.logging.Logger;


public class UDPProviderThread extends Thread {
    private String recordCount;
    private Logger logger;
    private CenterServerImpl server;


    public UDPProviderThread(CenterServerImpl server) throws IOException {
        this.server = server;
    }


    public String getRemoteRecordCount() {
        return recordCount;
    }


    @Override
    public void run() {
        DatagramSocket socket = null;
        try {
            socket = new DatagramSocket();
            byte[] data = "GET_RECORD_COUNT".getBytes();
            //System.out.println(server.location);
            DatagramPacket packet = new DatagramPacket(data, data.length, InetAddress.getByName(server.IPaddress), server.udpThread.udpPortNum);
            socket.send(packet);
            data = new byte[100];
            socket.receive(new DatagramPacket(data, data.length));
            recordCount = server + "," + new String(data);
            //System.out.println(recordCount);
        } catch (Exception e) {
            logger.log(Level.SEVERE, e.getMessage());
        } finally {
            if (socket != null) {
                socket.close();
            }
        }
    }
}