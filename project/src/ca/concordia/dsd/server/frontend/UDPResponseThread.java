package ca.concordia.dsd.server.frontend;

import ca.concordia.dsd.util.Constants;
import ca.concordia.dsd.util.LogUtil;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.SocketException;
import java.util.HashMap;

// FrontEnd uses this thread to received UDP response from the leader/replicas server
// response is latest saved in hashmap

public class UDPResponseThread extends Thread{

    DatagramSocket sSocket;
    DatagramPacket rPacket;
    HashMap<Integer, TransferResponseThread> responseMap;
    private LogUtil logUtil;
    private final String LOG_TAG = "| " + UDPResponseThread.class.getSimpleName() + " | ";

    public UDPResponseThread(HashMap<Integer, TransferResponseThread> responseMap, LogUtil logUtil){
        this.responseMap = responseMap;
        this.logUtil = logUtil;
        init();
    }

    private void init(){
        try{
            logUtil.log(LOG_TAG + "Creating datagram socket over front end udp port " + Constants.FRONT_END_UDP_PORT);
            sSocket = new DatagramSocket(Constants.FRONT_END_UDP_PORT);
        }catch (SocketException se){
            se.printStackTrace();
            System.out.println(LOG_TAG + se.getMessage());
        }
    }

    @Override
    public void run() {
        System.out.println(LOG_TAG+ "Running UDP Response Thread");
        byte[] data;

        // infinite loop
        while(true){
            try{
                data = new byte[1024];
                rPacket = new DatagramPacket(data,data.length);
                sSocket.receive(rPacket);
                byte[] response = rPacket.getData();
                String responseStr = new String(response).trim();
                System.out.println(LOG_TAG + "response string : " + responseStr);

                String[] responseArray = responseStr.split(Constants.RESPONSE_DATA_SPLITTER);
                TransferResponseThread trt = new TransferResponseThread(responseArray[0]);
                trt.start();

                responseMap.put(Integer.parseInt(responseArray[1]),trt);

            }catch (Exception e){
                e.printStackTrace();

                System.out.println(LOG_TAG + e.getMessage());

            }
        }
    }
}
