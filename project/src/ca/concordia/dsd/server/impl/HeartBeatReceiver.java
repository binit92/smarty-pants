package ca.concordia.dsd.server.impl;

import ca.concordia.dsd.server.frontend.FrontEnd;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.SocketException;


public class HeartBeatReceiver extends Thread {

	DatagramSocket ds = null;
	String name;
	boolean isAlive;
	Object mapAccessor;

	/*
	 * Heart beat receiver to keep checking the other servers' status
	 */
	public HeartBeatReceiver(boolean isAlive, String name, int port) {
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

	/*
	 * (non-Javadoc)
	 * @see java.lang.Thread#run()
	 */
	public void run() {
		byte[] data = new byte[100];
		while (getStatus()) {
			try {
				DatagramPacket dp = new DatagramPacket(data, data.length);
				ds.receive(dp);
				synchronized (mapAccessor) {
					// System.out.println("with time
					// "+System.nanoTime()/1000000+"In "+this.name+" Received
					// data "+new String(dp.getData()));
					FrontEnd.server_reporting_time.put(name, System.currentTimeMillis());
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	/*
	 * Sets the status of the receiver to false
	 * to kill the present server and its communication with
	 * other servers
	 */
	public void setStatus(boolean value) {
		isAlive = value;
	}

	private boolean getStatus() {
		return isAlive;
	}
}
