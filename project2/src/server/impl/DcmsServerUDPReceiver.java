package server.impl;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.util.logging.Level;
import java.util.logging.Logger;

import conf.ServerCenterLocation;



public class DcmsServerUDPReceiver extends Thread {
	DatagramSocket serverSocket;
	DatagramPacket receivePacket;
	DatagramPacket sendPacket;
	int udpPortNum;
	ServerCenterLocation location;
	private Logger loggerInstance;
	String recordCount;
	DcmsServerImpl server;
	int c;
	boolean isAlive;


	public DcmsServerUDPReceiver(boolean isAlive, int udpPort, ServerCenterLocation loc, Logger logger,
			DcmsServerImpl serverImp) {
		location = loc;
		loggerInstance = logger;
		this.server = serverImp;
		this.isAlive = isAlive;
		c = 0;
		try {
			serverSocket = new DatagramSocket(udpPort);
			udpPortNum = udpPort;
			logger.log(Level.INFO, loc.toString() + " UDP Server Started");
		} catch (IOException e) {
			logger.log(Level.SEVERE, e.getMessage());
		}
	}


	public synchronized void run() {
		byte[] receiveData;
		while (isAlive) {
			try {
				receiveData = new byte[1024];
				receivePacket = new DatagramPacket(receiveData, receiveData.length);
				serverSocket.receive(receivePacket);
				System.out.println("LOc :: " + location + "1 Received pkt in udp Receiver :: "
						+ new String(receivePacket.getData()));
				String inputPkt = new String(receivePacket.getData()).trim();
				new DcmsServerUDPRequestServer(receivePacket, server, loggerInstance).start();
				loggerInstance.log(Level.INFO, "2 Received in udp receiver " + inputPkt + " from " + location);
			} catch (Exception e) {
			}
		}
	}


	public void killUDPReceiver() {
		isAlive = false;
	}
}