package server.impl;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.SocketException;
import java.util.HashMap;
import java.util.logging.Level;
import conf.Constants;
import conf.LogManager;
import conf.ServerCenterLocation;
import server.frontend.TransferResponseToFE;


public class DcmsServerReplicaResponseReceiver extends Thread {

	DatagramSocket serverSocket;
	DatagramPacket receivePacket;
	DatagramPacket sendPacket;
	int udpPortNum;
	ServerCenterLocation location;
	LogManager loggerInstance;
	String recordCount;
	HashMap<Integer, TransferResponseToFE> responses;
	int c;

	public DcmsServerReplicaResponseReceiver(LogManager logManager) {
		try {
			loggerInstance = logManager;
			serverSocket = new DatagramSocket(Constants.CURRENT_PRIMARY_PORT_FOR_REPLICAS);
		} catch (SocketException e) {
			System.out.println(e.getMessage());
		}
	}


	@Override
	public synchronized void run() {
		byte[] receiveData;
		while (true) {
			try {
				receiveData = new byte[1024];
				receivePacket = new DatagramPacket(receiveData, receiveData.length);
				serverSocket.receive(receivePacket);
				byte[] receivedData = receivePacket.getData();
				String inputPkt = new String(receivedData).trim();
				if (inputPkt.contains("ACKNOWLEDGEMENT")) {
					System.out.println(new String(receivedData));
					loggerInstance.logger.log(Level.INFO, inputPkt);
				} else {
					System.out.println("Received response packet in PRIMARY:: " + new String(receivedData));
					loggerInstance.logger.log(Level.INFO, "Received response in Primary " + inputPkt);
				}
			} catch (Exception e) {

			}
		}
	}
}
