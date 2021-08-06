package server.impl;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.MulticastSocket;
import conf.LogManager;
import conf.Constants;

public class DcmsServerMultiCastReceiver extends Thread {
	MulticastSocket multicastsocket;
	InetAddress address;
	boolean isPrimary;
	LogManager logManager;



	public DcmsServerMultiCastReceiver(boolean isPrimary, LogManager ackManager) {
		try {
			multicastsocket = new MulticastSocket(Constants.MULTICAST_PORT_NUMBER);
			address = InetAddress.getByName(Constants.MULTICAST_IP_ADDRESS);
			multicastsocket.joinGroup(address);
			this.isPrimary = isPrimary;
			this.logManager = ackManager;
		} catch (IOException e) {
			System.out.println(e.getMessage());
		}

	}


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
							new String(packet.getData()), logManager);
					ack.start();
					DcmsServerReplicaRequestProcessor req = new DcmsServerReplicaRequestProcessor(
							new String(packet.getData()), logManager);
					req.start();
				}
			}
		} catch (IOException e) {
			System.out.println(e.getMessage());
		}
	}
}