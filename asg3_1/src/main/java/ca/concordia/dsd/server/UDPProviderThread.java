package ca.concordia.dsd.server;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;


public class UDPProviderThread extends Thread {
    private String recordCount;
    private String serverName;
    private String IPaddress;
    private int port;

    public UDPProviderThread(String serverName, String IPAddress, int port) throws IOException {
        this.serverName = serverName;
        this.IPaddress = IPAddress;
        this.port = port;

    }

    public String getRemoteRecordCount() {
        return recordCount;
    }

    @Override
    public void run() {
        DatagramSocket socket = null;
        try {
            socket = new DatagramSocket();
            socket.setSoTimeout(1000 * 5);
            byte[] data = "GET_RECORD_COUNT".getBytes();
            //System.out.println(server.location);
            DatagramPacket packet = new DatagramPacket(data, data.length, InetAddress.getByName(this.IPaddress), this.port);
            socket.send(packet);
            data = new byte[100];
            socket.receive(new DatagramPacket(data, data.length));
            recordCount = this.serverName + "," + new String(data);
            //System.out.println(recordCount);
        } catch (Exception e) {
            System.out.println(e.getMessage());
        } finally {
            if (socket != null) {
                socket.close();
            }
        }
    }
}