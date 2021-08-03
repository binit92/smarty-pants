package ca.concordia.dsd.server.frontend;

import ca.concordia.dsd.arch.corbaPOA;
import ca.concordia.dsd.server.impl.CenterServerImpl;
import ca.concordia.dsd.util.Constants;
import org.omg.CORBA.ORB;

import java.util.ArrayList;
import java.util.HashMap;

/**
 *  What this class does:
 *  1. Spawn separate threads for all the center servers
 *  2. Detect if the thread is running or need to be restarted
 *  3. ...
 */

public class FrontEnd extends corbaPOA {

    private CenterServerImpl leader;
    private CenterServerImpl replica1;
    private CenterServerImpl replica2;

    private boolean isLeaderAlive = true;
    private boolean isReplica1Alive = true;
    private boolean isReplica2Alive = true;

    public static ArrayList<String> listOfResponse;
    public static HashMap<Integer, HashMap<String,CenterServerImpl>> repo;

    public FrontEnd(){

    }

    public void init(){
        try{
            leader = new CenterServerImpl(Constants.DDO_TAG,Constants.DDO_SERVER_PORT,Constants.DDO_UDP_PORT_LEADER);
            replica1 = new CenterServerImpl(Constants.DDO_TAG,Constants.DDO_SERVER_PORT,Constants.DDO_UDP_PORT_REPLICA1);
            replica2 = new CenterServerImpl(Constants.DDO_TAG,Constants.DDO_SERVER_PORT, Constants.DDO_UDP_PORT_REPLICA2);

            // Save details in hashmap here .. or some datastructure

            Thread leaderThread = new Thread(){
                public void run(){
                    while(isLeaderAlive){
                        //TODO
                        //leader.sendHeartBeat();
                    }
                }
            };

            Thread replica1Thread = new Thread(){
                public void run(){
                    while(isReplica1Alive){
                        //TODO
                        //replica1.sendHeartBeat();
                    }
                }
            };


            Thread replica2Thread = new Thread(){
                public void run(){
                    while(isReplica2Alive){
                        //TODO
                        //replica2.sendHeartBeat();
                    }
                }
            };

            // start all three threads
            leaderThread.start();
            replica1Thread.start();
            replica2Thread.start();

            Thread watcher = new Thread(){
                public void run(){
                    while(true){
                        checkHeartBeats();
                    }
                }
            };
            watcher.start();

        }catch (Exception e){
            e.printStackTrace();
        }
    }


    @Override
    public String createTRecord(String id, String fName, String lName, String address, String phone, String specialization, String location) {
        return null;
    }

    @Override
    public String createSRecord(String id, String fName, String lName, String courses, boolean status, String statusDate) {
        return null;
    }

    @Override
    public String getRecordCounts(String id) {
        return null;
    }

    @Override
    public String editRecord(String id, String recordID, String fieldName, String newValue) {
        return null;
    }

    @Override
    public String transferRecord(String id, String recordId, String remoteCenterServerName) {
        return null;
    }

    public void connectToServer(String data){

    }

    // TODO
    private void checkHeartBeats(){

    }


    private void electNewLeader(){

    }


}
