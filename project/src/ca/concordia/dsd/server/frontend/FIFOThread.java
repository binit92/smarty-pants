package ca.concordia.dsd.server.frontend;

import ca.concordia.dsd.util.Constants;
import ca.concordia.dsd.util.LogUtil;

import java.io.FileWriter;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Queue;

/*
    This thread is for receiving input from front end so that
    the request can be added to the FIFO Queue to eventually
    transfer it to the current server.
 */
public class FIFOThread extends Thread{

    private DatagramSocket sSocket;
    private DatagramPacket rPacket;
    private ArrayList<TransferRequestThread> requestThreadArrayList;
    private Queue<String> fifoQueue =new LinkedList<>();
    private LogUtil logUtil;

    public FIFOThread(ArrayList<TransferRequestThread> requestThreadArrayList,LogUtil logUtil){
        this.requestThreadArrayList = requestThreadArrayList;
        this.logUtil = logUtil;
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
        System.out.println("Running FIFO Thread");
        byte[] data;
        // infinite loop
        while(true){
            try{
                data = new byte[1024];
                rPacket = new DatagramPacket(data,data.length);
                sSocket.receive(rPacket);
                byte[] receive = rPacket.getData();
                String receiveStr = new String(receive).trim();
                // TODO : use logUtil ?
                System.out.println("received string : " + receiveStr);

                fifoQueue.add(receiveStr);
                TransferRequestThread trt = new TransferRequestThread(fifoQueue.poll().getBytes(),logUtil);
                trt.start();

                requestThreadArrayList.add(trt);
            }catch (Exception e){
                System.out.println(e.getMessage());
            }
        }
    }
}
