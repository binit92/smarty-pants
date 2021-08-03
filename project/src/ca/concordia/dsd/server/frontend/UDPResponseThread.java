package ca.concordia.dsd.server.frontend;

import ca.concordia.dsd.util.Constants;

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

    public UDPResponseThread(HashMap<Integer, TransferResponseThread> responseMap){
        this.responseMap = responseMap;
        init();
    }

    private void init(){
        try{
            sSocket = new DatagramSocket(Constants.FRONT_END_UDP_PORT);
        }catch (SocketException se){
            System.out.println(se.getMessage());
        }
    }

    @Override
    public void run() {
        System.out.println("Running UDP Response Thread");
        byte[] data;
        // infinite loop
        while(true){
            try{
                data = new byte[1024];
                rPacket = new DatagramPacket(data,data.length);
                sSocket.receive(rPacket);
                byte[] response = rPacket.getData();
                String responseStr = new String(response).trim();
                System.out.println("response string : " + responseStr);

                String[] responseArray = responseStr.split(Constants.RESPONSE_DATA_SPLITTER);
                TransferResponseThread trt = new TransferResponseThread(responseArray[0]);
                trt.start();

                responseMap.put(Integer.parseInt(responseArray[1]),trt);

            }catch (Exception e){
                System.out.println(e.getMessage());
            }
        }
    }
}
