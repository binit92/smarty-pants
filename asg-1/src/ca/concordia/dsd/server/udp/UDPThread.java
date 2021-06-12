package ca.concordia.dsd.server.udp;

import ca.concordia.dsd.server.CenterServerImpl;
import ca.concordia.dsd.util.LogUtil;

import java.net.DatagramPacket;
import java.net.DatagramSocket;

public class UDPThread implements Runnable {
    private int port;
    private String serverName;
    private DatagramSocket socket;
    private DatagramPacket receive;
    private DatagramPacket send;
    private LogUtil logUtil;
    private CenterServerImpl serverTasks;

    public UDPThread(String name, int no, LogUtil instance, CenterServerImpl tasks){
        this.serverName = name;
        this.port = no;
        this.logUtil = instance;
        this.serverTasks = tasks;

        try{
            socket = new DatagramSocket(port);
            logUtil.log(serverName + " has started on port : " + port);
        }catch (Exception e){
            logUtil.log(e.getMessage());
        }

    }

    @Override
    public void run() {
        byte[] data;
        while(true){
            try{
                data = new byte[1024];
                receive = new DatagramPacket(data,data.length);
                socket.receive(receive);
                logUtil.log("received: " + new String(data));

            }catch (Exception e){

            }
        }
    }
}
