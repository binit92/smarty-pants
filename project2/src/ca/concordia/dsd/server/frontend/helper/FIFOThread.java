package ca.concordia.dsd.server.frontend.helper;

import ca.concordia.dsd.util.Constants;
import ca.concordia.dsd.util.LocationEnum;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Queue;
import java.util.logging.Level;
import java.util.logging.Logger;

public class FIFOThread extends Thread {

    DatagramSocket serverSocket;
    DatagramPacket receivePacket;
    DatagramPacket sendPacket;
    int udpPortNum;
    LocationEnum location;
    Logger loggerInstance;
    String recordCount;
    ArrayList<RequestThread> requests;
    int c;
    Queue<String> FIFORequest = new LinkedList<String>();


    public FIFOThread(ArrayList<RequestThread> requests) {
        try {
            this.requests = requests;
            serverSocket = new DatagramSocket(Constants.CURRENT_SERVER_UDP_PORT);
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
                System.out.println("Received pkt :: " + new String(receivedData));
                FIFORequest.add(new String(receivedData));
                RequestThread transferReq = new RequestThread(FIFORequest.poll().getBytes(),
                        loggerInstance);
                transferReq.start();
                requests.add(transferReq);
                String inputPkt = new String(receivePacket.getData()).trim();
                loggerInstance.log(Level.INFO, "Received " + inputPkt + " from " + location);
            } catch (Exception e) {

            }
        }
    }
}
