package ca.concordia.dsd.server.frontend;

import ca.concordia.dsd.arch.corbaPOA;
import ca.concordia.dsd.database.Records;
import ca.concordia.dsd.server.OperationsType;
import ca.concordia.dsd.server.impl.CenterServerImpl;
import ca.concordia.dsd.util.Constants;
import ca.concordia.dsd.util.LogUtil;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 *  What this class does:
 *  1. Spawn separate threads for all the center servers
 *  2. Detect if the thread is running or need to be restarted
 *  3. ...
 */

public class FrontEnd extends corbaPOA {

    private final String LOG_TAG = "| " + FrontEnd.class.getSimpleName() + " | ";
    private CenterServerImpl leader;
    private CenterServerImpl replica1;
    private CenterServerImpl replica2;

    private boolean isLeaderAlive = true;
    private boolean isReplica1Alive = true;
    private boolean isReplica2Alive = true;

    private int requestCount;
    private String serverTag;
    private LogUtil logUtil;

    static boolean leaderIsAlive = true;
    static boolean replica1IsAlive = true;
    static boolean replica2IsAlive = true;

    // why ?
    private HashMap<String, List<Records>> recordListMap;
    private ArrayList<TransferRequestToCurrentServerThread> transferRequestToCurrentServerThreadArrayList;
    private HashMap<Integer,String> requestBufferMap;
    private static HashMap<String,Integer> currentIds;

    private static Object lock = new Object();
    public static HashMap<String, Boolean> server_leader = new HashMap<>();
    public static HashMap<String, Long> server_reporting_time = new HashMap<>();

    public static String CURRENT_LEADER_SERVER_NAME;
    public static String CURRENT_SERVER_HOST;
    public static int CURRENT_SERVER_UDP_PORT;

    public static ArrayList<String> listOfResponse;
    public static HashMap<Integer, CenterServerImpl> repo;
    public static HashMap<Integer, TransferResponseThread> transferResponseThreadHashMap;
    public static ArrayList<String> responseArrayList;

    public FrontEnd(String serverTag){
        this.serverTag = serverTag;
        this.logUtil = new LogUtil("frontend");

        //ArrayList of threads that transfer requests to current server
        transferRequestToCurrentServerThreadArrayList = new ArrayList<>();
        // hashmap of transfer response threads
        transferResponseThreadHashMap = new HashMap<>();
        // request buffer map
        requestBufferMap = new HashMap<>();
        //received response list
        responseArrayList = new ArrayList<>();

        // save the list of response from TransferResponseThread
        listOfResponse = new ArrayList<>();
        //repo - map of leader and replicas
        repo = new HashMap<>();
        // save the servername with ids
        currentIds = new HashMap<>();

        findCurrentServerSettings(serverTag);

        //variable to save the number of request that is coming to this server starting from 1
        requestCount = 0;
    }

    private void findCurrentServerSettings(String serverTag){
        if (serverTag.equalsIgnoreCase(Constants.DDO_TAG)){
            CURRENT_SERVER_HOST = Constants.DDO_SERVER_HOST;
            CURRENT_SERVER_UDP_PORT = Constants.DDO_UDP_PORT_LEADER;
            CURRENT_LEADER_SERVER_NAME = Constants.DDO_1;

            server_leader.put(Constants.DDO_1,true);
            server_leader.put(Constants.DDO_2,false);
            server_leader.put(Constants.DDO_3,false);

            currentIds.put(Constants.DDO_1,Constants.LEADER_ID);
            currentIds.put(Constants.DDO_2,Constants.REPLICA1_ID);
            currentIds.put(Constants.DDO_3,Constants.REPLICA2_ID);

            server_reporting_time.put(Constants.DDO_1,System.currentTimeMillis());
            server_reporting_time.put(Constants.DDO_2,System.currentTimeMillis());
            server_reporting_time.put(Constants.DDO_3,System.currentTimeMillis());

        }else if (serverTag.equalsIgnoreCase(Constants.LVL_TAG)){
            CURRENT_SERVER_HOST = Constants.LVL_SERVER_HOST;
            CURRENT_SERVER_UDP_PORT = Constants.LVL_UDP_PORT_LEADER;
            CURRENT_LEADER_SERVER_NAME = Constants.LVL_1;

            server_leader.put(Constants.LVL_1,true);
            server_leader.put(Constants.LVL_2,false);
            server_leader.put(Constants.LVL_3,false);

            currentIds.put(Constants.LVL_1,Constants.LEADER_ID);
            currentIds.put(Constants.LVL_2,Constants.REPLICA1_ID);
            currentIds.put(Constants.LVL_3,Constants.REPLICA2_ID);

            server_reporting_time.put(Constants.LVL_1,System.currentTimeMillis());
            server_reporting_time.put(Constants.LVL_2,System.currentTimeMillis());
            server_reporting_time.put(Constants.LVL_3,System.currentTimeMillis());

        }else if (serverTag.equalsIgnoreCase(Constants.MTL_TAG)){
            CURRENT_SERVER_HOST = Constants.MTL_SERVER_HOST;
            CURRENT_SERVER_UDP_PORT = Constants.MTL_UDP_PORT_LEADER;
            CURRENT_LEADER_SERVER_NAME = Constants.MTL_1;

            server_leader.put(Constants.MTL_1,true);
            server_leader.put(Constants.MTL_2,false);
            server_leader.put(Constants.MTL_3,false);

            currentIds.put(Constants.MTL_1,Constants.LEADER_ID);
            currentIds.put(Constants.MTL_2,Constants.REPLICA1_ID);
            currentIds.put(Constants.MTL_3,Constants.REPLICA2_ID);

            server_reporting_time.put(Constants.MTL_1,System.currentTimeMillis());
            server_reporting_time.put(Constants.MTL_2,System.currentTimeMillis());
            server_reporting_time.put(Constants.MTL_3,System.currentTimeMillis());
        }
    }

    public void init(){
        try{
            //starting FIFO Thread
            FIFOThread fifoThread = new FIFOThread(transferRequestToCurrentServerThreadArrayList,logUtil);
            fifoThread.start();

            //starting UDP Response Thread
            UDPResponseReceiverThread udpResponseReceiverThread = new UDPResponseReceiverThread(transferResponseThreadHashMap,logUtil);
            udpResponseReceiverThread.start();

            // Central Server, leader and its two replica
            ArrayList<Integer> replicas = new ArrayList<>();
            replicas.add(Constants.REPLICA1_ID);
            replicas.add(Constants.REPLICA2_ID);
            boolean isPrimary = true;

            DatagramSocket socket1 = new DatagramSocket();
            DatagramSocket socket2 = new DatagramSocket();
            DatagramSocket socket3 = new DatagramSocket();
            leader = new CenterServerImpl(Constants.DDO_1,
                    Constants.DDO_SERVER_PORT,
                    Constants.LVL_SERVER_PORT,
                    Constants.MTL_SERVER_PORT,
                    Constants.DDO_UDP_PORT_LEADER,
                    socket1,
                    replicas,
                    Constants.PRIMARY_SERVER_ID,
                    isPrimary,
                    leaderIsAlive);

            //TODO : why servername is hardcoded to DDO
            replica1 = new CenterServerImpl(Constants.DDO_2,
                    Constants.DDO_SERVER_PORT,
                    Constants.LVL_SERVER_PORT,
                    Constants.MTL_SERVER_PORT,
                    Constants.DDO_UDP_PORT_REPLICA1,
                    socket1,
                    replicas,
                    Constants.REPLICA1_ID,
                    false,
                    replica1IsAlive);
            replica2 = new CenterServerImpl(Constants.DDO_3,
                    Constants.DDO_SERVER_PORT,
                    Constants.LVL_SERVER_PORT,
                    Constants.MTL_SERVER_PORT,
                    Constants.DDO_UDP_PORT_REPLICA2,
                    socket1,
                    replicas,
                    Constants.REPLICA1_ID,
                    false,
                    replica2IsAlive);

            // Save details in hashmap here .. or some datastructure
            repo.put(Constants.LEADER_ID,leader);
            repo.put(Constants.REPLICA1_ID,replica1);
            repo.put(Constants.REPLICA2_ID,replica2);

            Thread leaderThread = new Thread(){
                public void run(){
                    while(isLeaderAlive){
                        //TODO
                        leader.sendHeartBeat();
                    }
                }
            };

            Thread replica1Thread = new Thread(){
                public void run(){
                    while(isReplica1Alive){
                        //TODO
                        replica1.sendHeartBeat();
                    }
                }
            };


            Thread replica2Thread = new Thread(){
                public void run(){
                    while(isReplica2Alive){
                        //TODO
                        replica2.sendHeartBeat();
                    }
                }
            };

            //>start all three threads
            //leaderThread.start();
            //replica1Thread.start();
            //replica2Thread.start();

            Thread watcher = new Thread(){
                public void run(){
                    while(true){
                        if(serverTag.startsWith("MTL")){
                            checkServerStatus(Constants.MTL_1);
                            checkServerStatus(Constants.MTL_2);
                            checkServerStatus(Constants.MTL_3);
                        }else if (serverTag.startsWith("DDO")){
                            checkServerStatus(Constants.DDO_1);
                            checkServerStatus(Constants.DDO_2);
                            checkServerStatus(Constants.DDO_3);
                        }else if (serverTag.startsWith("LVL")){
                            checkServerStatus(Constants.LVL_1);
                            checkServerStatus(Constants.LVL_2);
                            checkServerStatus(Constants.LVL_3);
                        }
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
        System.out.println(LOG_TAG + "createTRecord: " +  id + " " +fName + " " + lName + " " + address);
        StringBuilder builder = new StringBuilder();
        builder.append(OperationsType.CREATE_TR_RECORD);builder.append(Constants.RESPONSE_DATA_SPLITTER);
        builder.append(id);builder.append("|");
        builder.append(fName);builder.append("|");
        builder.append(lName);builder.append("|");
        builder.append(address);builder.append("|");
        builder.append(phone);builder.append("|");
        builder.append(specialization);builder.append("|");
        builder.append(location);builder.append("|");
        logUtil.log(LOG_TAG + " dispatching createTRRecord to server : " + builder);
        return dispatchToCurrentServer(builder.toString());
    }

    @Override
    public String createSRecord(String id, String fName, String lName, String courses, boolean status, String statusDate) {
        System.out.println(LOG_TAG + "createSRecord: " +   id + " " +fName + " " + lName  );
        StringBuilder builder = new StringBuilder();
        builder.append(OperationsType.CREATE_SR_RECORD);builder.append(Constants.RESPONSE_DATA_SPLITTER);
        builder.append(id);builder.append("|");
        builder.append(fName);builder.append("|");
        builder.append(lName);builder.append("|");
        builder.append(courses);builder.append("|");
        builder.append(status);builder.append("|");
        builder.append(statusDate);builder.append("|");
        logUtil.log(LOG_TAG + " dispatching createSRRecord to server : " + builder);
        return dispatchToCurrentServer(builder.toString());
    }

    @Override
    public String getRecordCounts(String id) {
        System.out.println(LOG_TAG + "getRecordCounts: " + id );
        StringBuilder builder = new StringBuilder();
        builder.append(OperationsType.GET_RECORD_COUNT);builder.append(Constants.RESPONSE_DATA_SPLITTER);
        builder.append(id);builder.append("|");
        logUtil.log(LOG_TAG + " dispatching getRecordCounts to server : " + builder);
        return dispatchToCurrentServer(builder.toString());
    }

    @Override
    public String editRecord(String id, String recordID, String fieldName, String newValue) {
        System.out.println(id + " " +recordID + " " + fieldName + " " + newValue);
        StringBuilder builder = new StringBuilder();
        builder.append(OperationsType.EDIT_RECORD);builder.append(Constants.RESPONSE_DATA_SPLITTER);
        builder.append(id);builder.append("|");
        builder.append(recordID);builder.append("|");
        builder.append(fieldName);builder.append("|");
        builder.append(newValue);builder.append("|");
        logUtil.log(LOG_TAG + " dispatching editRecord to server : " + builder);
        return dispatchToCurrentServer(builder.toString());
    }

    @Override
    public String transferRecord(String id, String recordId, String remoteCenterServerName) {
        System.out.println(LOG_TAG + "transferRecord : " + id + " " + recordId + " " + remoteCenterServerName);
        StringBuilder builder = new StringBuilder();
        builder.append(OperationsType.TRANSFER_RECORD);builder.append(Constants.RESPONSE_DATA_SPLITTER);
        builder.append(id);builder.append("|");
        builder.append(recordId);builder.append("|");
        builder.append(remoteCenterServerName);builder.append("|");
        logUtil.log(LOG_TAG + " dispatching transferRecord to server : " + builder);
        return dispatchToCurrentServer(builder.toString());
    }

    @Override
    public String killPrimaryServer(String id){
        System.out.printf(LOG_TAG + "killPrimaryServer : " + id + " " );;
        String ret = "";

        if (leaderIsAlive && replica1IsAlive && replica2IsAlive) {
            leaderIsAlive = false;
            leader.heartBeatReceiver.setStatus(false);
            ret = "MTL1 Server is killed " + electNewLeader(CURRENT_LEADER_SERVER_NAME);
        } else {
            ret = "Primary is already killed!!";
        }
        return ret;
    }

    public String dispatchToCurrentServer(String data){
        try{
            requestCount+=1;
            DatagramSocket sSocket = new DatagramSocket();
            data += Constants.RESPONSE_DATA_SPLITTER + Integer.toString(requestCount);
            byte[] arr = data.getBytes();
            DatagramPacket pkt = new DatagramPacket(arr, arr.length, InetAddress.getByName(CURRENT_SERVER_HOST),CURRENT_SERVER_UDP_PORT);
            sSocket.send(pkt);

            logUtil.log(LOG_TAG + "Saving request with id " + requestCount + " in request buffer");
            requestBufferMap.put(requestCount,data);
            logUtil.log(LOG_TAG + "waiting for reply from server " + CURRENT_SERVER_HOST + " : " + CURRENT_SERVER_UDP_PORT);

            Thread.sleep(5 * 1000);

            return getResponse(requestCount);
        }catch (Exception e){
            e.printStackTrace();
        }
        return null;
    }

    public String getResponse(Integer requestId) {
        try {
            transferResponseThreadHashMap.get(requestId).join();
        } catch (InterruptedException e) {
            System.out.println(e.getMessage());
        }
        requestBufferMap.remove(requestId);
        String response = transferResponseThreadHashMap.get(requestId).getResponse();
        logUtil.log(LOG_TAG + "response : " + response);
        return response;
    }

    // TODO : add some comment and details here
    private void checkServerStatus(String name_of_server){
        synchronized (lock){
            long cur = System.currentTimeMillis();
            if (server_reporting_time.containsKey(name_of_server)) {
                if (cur - server_reporting_time.get(name_of_server) > Constants.LEADER_DOWN_TIME_LIMIT) {
                    if (server_leader.containsKey(name_of_server)) {
                        if (server_leader.get(name_of_server)) {
                            logUtil.log(LOG_TAG + name_of_server + " Leader Server has failed!!!");
                            electNewLeader(name_of_server);
                        }
                    }
                }
            }
        }
    }

    private String electNewLeader(String oldLeader) {
        System.out.println(LOG_TAG +  "electNewLeader , oldleader is: " + oldLeader);
        server_leader.remove(oldLeader);
        server_reporting_time.remove(oldLeader);
        currentIds.remove(oldLeader);

        String loc = oldLeader.substring(0, 3);
        Map.Entry<String, Integer> maxEntry = null;
        for (Map.Entry<String, Integer> entry : currentIds.entrySet()) {
            if (entry.getKey().contains(loc)) {
                if (maxEntry == null || entry.getValue().compareTo(maxEntry.getValue()) > 0) {
                    maxEntry = entry;
                }
            }
        }
        System.out.println(LOG_TAG + "loc found is : " + loc);
        System.out.println(LOG_TAG + "max entry is : " + maxEntry.getKey() + " : " + maxEntry.getValue());
        server_leader.put(maxEntry.getKey(), true);
        currentIds.put(maxEntry.getKey(), Constants.LEADER_ID);
        logUtil.log(LOG_TAG +  "++++Elected new leader :: " + maxEntry.getKey() + " in the location" + loc);

        CenterServerImpl replaceserver;
        synchronized (repo) {
            replaceserver = repo.get(Constants.PRIMARY_SERVER_ID);
        }
        if (maxEntry.getKey().contains(Integer.toString(Constants.REPLICA1_ID))) {
            ArrayList<Integer> replicas = new ArrayList<>();
            replicas.add(Constants.REPLICA2_ID);
            CenterServerImpl newServer;

            synchronized (repo) {
                newServer = repo.get(Constants.REPLICA1_ID);
            }

            newServer.setPrimary(true);
            newServer.setReplicas(replicas);
            newServer.setServerID(Constants.PRIMARY_SERVER_ID);

            // setting replica1(new server) primary server here
            synchronized (repo) {
                repo.put(Constants.PRIMARY_SERVER_ID, newServer);
            }
            // setting replaceserver as replica1 here
            synchronized (repo) {
                repo.put(Constants.REPLICA1_ID, replaceserver);
            }

            CenterServerImpl replica2 = repo.get(Constants.REPLICA2_ID);
            replica2.setReplicas(replicas);

            // keeping replica2 as replica 2 here
            synchronized (repo) {
                repo.put(Constants.REPLICA2_ID, replica2);
            }
        } else if (maxEntry.getKey().contains(Integer.toString(Constants.REPLICA2_ID))) {
            ArrayList<Integer> replicas = new ArrayList<>();
            replicas.add(Constants.REPLICA1_ID);
            CenterServerImpl newServer = repo.get(Constants.REPLICA2_ID);
            newServer.setPrimary(true);
            newServer.setReplicas(replicas);
            newServer.setServerID(Constants.PRIMARY_SERVER_ID);

            synchronized (repo) {
                repo.put(Constants.PRIMARY_SERVER_ID, newServer);
            }

            synchronized (repo) {
                repo.put(Constants.REPLICA2_ID, replaceserver);
            }
            CenterServerImpl replica1 = repo.get(Constants.REPLICA1_ID);
            replica1.setReplicas(replicas);
            synchronized (repo) {
                repo.put(Constants.REPLICA1_ID, replica1);
            }
        }

        System.out.println(LOG_TAG + "Elected new leader :: " + maxEntry.getKey() + " in the location" + loc);
        return "and elected new leader " + maxEntry.getKey() + " in the location" + loc;
    }
}
