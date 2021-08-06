package server.impl;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.SocketException;
import java.util.logging.Logger;

import server.frontend.DcmsServerFE;

public class HeartBeatReceiver extends Thread {

	DatagramSocket ds = null;
	String name;
	boolean isAlive;
	Object mapAccessor;


	public HeartBeatReceiver(boolean isAlive, String name, int port, Logger logger) {
		try {
			this.isAlive = isAlive;
			this.name = name;
			System.out.println(name + "listening in :: " + port);
			ds = new DatagramSocket(port);
			mapAccessor = new Object();
		} catch (SocketException e) {
			e.printStackTrace();
		}
	}

	public void run() {
		byte[] data = new byte[100];
		while (getStatus()) {
			try {
				DatagramPacket dp = new DatagramPacket(data, data.length);
				ds.receive(dp);
				synchronized (mapAccessor) {
					DcmsServerFE.server_last_updated_time.put(name, System.nanoTime() / 1000000);
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}


	public void setStatus(boolean value) {
		isAlive = value;
	}

	private boolean getStatus() {
		return isAlive;
	}
}
