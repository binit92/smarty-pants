package ca.concordia.dsd.server.frontend.helper;

import ca.concordia.dsd.util.Constants;
import ca.concordia.dsd.util.LocationEnum;
import ca.concordia.dsd.util.LogUtil;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.SocketException;
import java.util.HashMap;

public class UDPResponseThread extends Thread {

    private static final String TAG = "|" + UDPResponseThread.class.getSimpleName() + "| ";
    private DatagramSocket serverSocket;
    private DatagramPacket receivePacket;
    private HashMap<Integer, ResponseThread> responses;
    private LogUtil logUtil;

    public UDPResponseThread(HashMap<Integer, ResponseThread> responses, LogUtil logUtil) {
        try {
            this.responses = responses;
            this.logUtil = logUtil;
            serverSocket = new DatagramSocket(Constants.FRONT_END_UDP_PORT);
        } catch (SocketException e) {
            System.out.println(e.getMessage());
        }
    }

    @Override
    public void run() {
        byte[] receiveData;
        while (true) {
            try {
                receiveData = new byte[1024];
                receivePacket = new DatagramPacket(receiveData, receiveData.length);
                serverSocket.receive(receivePacket);
                byte[] receivedData = receivePacket.getData();
                String inputPkt = new String(receivedData).trim();
                String[] data = inputPkt.split(Constants.RESPONSE_SPLITTER);
                ResponseThread transferResponse = new ResponseThread(data[0], logUtil);
                transferResponse.start();
                responses.put(Integer.parseInt(data[1]), transferResponse);
                logUtil.log(TAG + "Received " + inputPkt );
            } catch (Exception e) {

            }
        }
    }
}
