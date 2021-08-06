package server.impl;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;

import conf.Constants;
import conf.LogManager;

public class DcmsServerReplicaAcknowledgementSender extends Thread {
	String request;
	DatagramSocket ds;

	public DcmsServerReplicaAcknowledgementSender(String request, LogManager logManger) {
		request = "RECEIVED ACKNOWLEDGEMENT IN PRIMARY :: " + request;
		this.request = request;
	}


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
