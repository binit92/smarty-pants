package ca.concordia.dsd.server.frontend.helper;

import ca.concordia.dsd.util.Constants;
import ca.concordia.dsd.util.LocationEnum;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.SocketException;
import java.util.HashMap;
import java.util.logging.Level;
import java.util.logging.Logger;

public class UDPResponseThread extends Thread {

    DatagramSocket serverSocket;
    DatagramPacket receivePacket;
    DatagramPacket sendPacket;
    int udpPortNum;
    LocationEnum location;
    Logger loggerInstance;
    String recordCount;
    HashMap<Integer, ResponseThread> responses;
    int c;

    public UDPResponseThread(HashMap<Integer, ResponseThread> responses) {
        try {
            this.responses = responses;
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
                System.out.println(
                        "Received response packet :: " + new String(receivedData));
                String inputPkt = new String(receivedData).trim();
                System.out.println("Returned response...." + inputPkt);
                String[] data = inputPkt.split(Constants.RESPONSE_DATA_SEPERATOR);
                ResponseThread transferResponse = new ResponseThread(data[0]);
                transferResponse.start();
                responses.put(Integer.parseInt(data[1]), transferResponse);
                loggerInstance.log(Level.INFO,
                        "Received " + inputPkt + " from " + location);
            } catch (Exception e) {

            }
        }
    }
}
