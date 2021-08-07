package ca.concordia.dsd.server.frontend;

import ca.concordia.dsd.arch.corbaPOA;
import ca.concordia.dsd.database.Record;
import ca.concordia.dsd.server.frontend.helper.FIFOThread;
import ca.concordia.dsd.server.frontend.helper.RequestThread;
import ca.concordia.dsd.server.frontend.helper.ResponseThread;
import ca.concordia.dsd.server.frontend.helper.UDPResponseThread;
import ca.concordia.dsd.server.impl.CenterServer;
import ca.concordia.dsd.server.impl.multicast.MultiCastReceiverThread;
import ca.concordia.dsd.server.impl.replica.ReplicaResponseThread;
import ca.concordia.dsd.util.Constants;
import ca.concordia.dsd.util.LocationEnum;
import ca.concordia.dsd.util.LogUtil;
import ca.concordia.dsd.util.OperationsEnum;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class FrontEnd extends corbaPOA implements Constants {
    private static final String TAG = "|" + FrontEnd.class.getSimpleName() + "| ";
    public static HashMap<Integer, ResponseThread> responsesMap;
    public static ArrayList<String> receivedResponsesArraylist;
    public static HashMap<String, CenterServer> primaryMap, replicaOneMap, replicaTwoMap;
    public static HashMap<Integer, HashMap<String, CenterServer>> repo;
    public static HashMap<String, Boolean> statusMap = new HashMap<>();
    public static HashMap<String, Long> reportingMap = new HashMap<>();

    private static boolean isMTLOneAlive = true;
    private static final boolean isMTLTwoAlive = true;
    private static final boolean isMTLThreeAlive = true;
    private static boolean isLVLOneAlive = true;
    private static final boolean isLVLTwoAlive = true;
    private static final boolean isLVLThreeAlive = true;
    private static boolean isDDOOneAlive = true;
    private static final boolean isDDOTwoAlive = true;
    private static final boolean isDDOThreeAlive = true;
    private static final int ONE_SECOND_TIMEOUT = 1000;
    private static final int LEADER_ID = 100;
    private static final int S1_ID = 1;
    private static final int S2_ID = 2;
    private static final int S3_ID = 3;
    private static final Object lock = new Object();
    private static final HashMap<String, Integer> ServerIDMap = new HashMap<>();
    private static LogUtil logUtil;
    public HashMap<String, List<Record>> recordsMap;
    private LogUtil ackManager;
    private Integer requestId;
    private final HashMap<Integer, String> requestBuffer;
    private final ArrayList<RequestThread> requests;
    private MultiCastReceiverThread primaryReceiver, replica1Receiver, replica2Receiver;
    private ReplicaResponseThread replicaResponseReceiver;
    private final ArrayList<Integer> replicas = new ArrayList<>();

    private CenterServer primaryMtlServer, primaryLvlServer, primaryDdoServer;
    private CenterServer replica1MtlServer, replica1LvlServer, replica1DdoServer;
    private CenterServer replica2MtlServer, replica2LvlServer, replica2DdoServer;


    public FrontEnd() {
        logUtil = new LogUtil("FrontEnd");

        recordsMap = new HashMap<>();
        requests = new ArrayList<>();
        responsesMap = new HashMap<>();
        requestBuffer = new HashMap<>();
        receivedResponsesArraylist = new ArrayList<>();

        repo = new HashMap<>();
        primaryMap = new HashMap<>();
        replicaOneMap = new HashMap<>();
        replicaTwoMap = new HashMap<>();
        requestId = 0;

        init();
    }

    private static LogUtil getLogInstance(String serverName, LocationEnum loc) {
        return new LogUtil(serverName);
    }

    private static synchronized void checkServerStatus(String serverName) {
        synchronized (lock) {
            try {
                long currentTime = System.currentTimeMillis();
                if (reportingMap.containsKey(serverName)) {
                    if (currentTime - reportingMap.get(serverName) > ONE_SECOND_TIMEOUT) {
                        if (statusMap.containsKey(serverName)) {
                            if (statusMap.get(serverName)) {
                                logUtil.log(TAG + "<FAIL> Primary Server : " + serverName + " has not reported in last one second");
                                bullyAlgorithm(serverName);
                            }
                        }
                    }
                }
            }catch (Exception e){

            }
        }
    }

    private static String bullyAlgorithm(String oldLeader) {
        statusMap.remove(oldLeader);
        reportingMap.remove(oldLeader);
        ServerIDMap.remove(oldLeader);
        String loc = oldLeader.substring(0, 3);

        Map.Entry<String, Integer> maxEntry = null;
        for (Map.Entry<String, Integer> entry : ServerIDMap.entrySet()) {
            if (entry.getKey().contains(loc)) {
                if (maxEntry == null || entry.getValue().compareTo(maxEntry.getValue()) > 0) {
                    maxEntry = entry;
                }
            }
        }
        statusMap.put(maxEntry.getKey(), true);
        ServerIDMap.put(maxEntry.getKey(), LEADER_ID);
        logUtil.log(TAG + "New elected leader is : " + maxEntry.getKey() + " at datacenter " + loc);
        HashMap<String, CenterServer> replaceserver = new HashMap<String, CenterServer>();
        synchronized (repo) {
            replaceserver = repo.get(Constants.PRIMARY_SERVER_ID);
        }
        if (maxEntry.getKey().contains("2")) {
            try{
                ArrayList<Integer> replicas = new ArrayList<>();
                replicas.add(Constants.REPLICA2_SERVER_ID);
                HashMap<String, CenterServer> getnewserver = new HashMap<String, CenterServer>();
                synchronized (repo) {
                    getnewserver = repo.get(Constants.REPLICA1_SERVER_ID);
                }
                CenterServer newPrimary = getnewserver.get(loc);
                newPrimary.setPrimary(true);
                newPrimary.setReplicas(replicas);
                newPrimary.setServerID(Constants.PRIMARY_SERVER_ID);

                replaceserver.remove(loc);

                replaceserver.put(loc, newPrimary);
                synchronized (repo) {
                    repo.put(Constants.PRIMARY_SERVER_ID, replaceserver);
                }
                getnewserver.remove(loc);
                synchronized (repo) {
                    repo.put(Constants.REPLICA1_SERVER_ID, getnewserver);
                }
                HashMap<String, CenterServer> replicamap = repo.get(Constants.REPLICA2_SERVER_ID);
                CenterServer replica = replicamap.get(loc);
                replica.setReplicas(replicas);
                replicamap.put(loc, replica);
                logUtil.log(TAG + "No replica found at location " + loc);
                synchronized (repo) {
                    repo.put(Constants.REPLICA2_SERVER_ID, replicamap);
                }
            }catch (Exception e){}

        } else if (maxEntry.getKey().contains("3")) {
            try{
                ArrayList<Integer> replicas = new ArrayList<>();
                replicas.add(Constants.REPLICA1_SERVER_ID);
                HashMap<String, CenterServer> getnewserver = repo.get(Constants.REPLICA2_SERVER_ID);
                CenterServer newPrimary = getnewserver.get(loc);
                newPrimary.setPrimary(true);
                newPrimary.setReplicas(replicas);
                newPrimary.setServerID(Constants.PRIMARY_SERVER_ID);

                replaceserver.put(loc, newPrimary);
                synchronized (repo) {
                    repo.put(Constants.PRIMARY_SERVER_ID, replaceserver);
                }
                getnewserver.remove(loc);
                synchronized (repo) {
                    repo.put(Constants.REPLICA2_SERVER_ID, getnewserver);
                }
                HashMap<String, CenterServer> replicamap = repo.get(Constants.REPLICA1_SERVER_ID);
                CenterServer replica = replicamap.get(loc);
                replica.setReplicas(replicas);
                replicamap.put(loc, replica);
                synchronized (repo) {
                    repo.put(Constants.REPLICA1_SERVER_ID, replicamap);
                }
            }catch (Exception e){}

        }
        return "and elected new leader " + maxEntry.getKey() + " in the location" + loc;
    }

    private static boolean getStatus(String name) {
        if (name.equals("MTL1")) {
            return isMTLOneAlive;
        } else if (name.equals("MTL2")) {
            return isMTLTwoAlive;
        } else if (name.equals("MTL3")) {
            return isMTLThreeAlive;
        } else if (name.equals("LVL1")) {
            return isLVLOneAlive;
        } else if (name.equals("LVL2")) {
            return isLVLTwoAlive;
        } else if (name.equals("LVL3")) {
            return isLVLThreeAlive;
        } else if (name.equals("DDO1")) {
            return isDDOOneAlive;
        } else if (name.equals("DDO2")) {
            return isDDOTwoAlive;
        } else if (name.equals("DDO3")) {
            return isDDOThreeAlive;
        }
        return false;
    }


    private String getServerLoc(String managerID) {
        return managerID.substring(0, 3);
    }

    @Override
    public String createTRecord(String id, String fName, String lName, String address, String phone, String specialization, String location) {
        String teacherStr = fName + "," + lName + ","
                + address + "," + phone + "," + specialization + "," + location;
        String teacher = OperationsEnum.CREATE_TR_RECORD + Constants.RECEIVED_SPLITTER + getServerLoc(id)
                + Constants.RECEIVED_SPLITTER + id + Constants.RECEIVED_SPLITTER + teacherStr;
        logUtil.log(TAG + "Forwarding createTRecord operation from frontend to server" + teacher);
        return dispatchRequestToCurrentServer(teacher);
    }

    @Override
    public String createSRecord(String id, String fName, String lName, String courses, boolean status, String statusDate) {
        String studentStr = fName + "," + lName + "," + courses + "," + status + "," + statusDate;
        String student = OperationsEnum.CREATE_SR_RECORD + Constants.RECEIVED_SPLITTER + getServerLoc(id)
                + Constants.RECEIVED_SPLITTER + id + Constants.RECEIVED_SPLITTER + studentStr;
        logUtil.log(TAG + "Forwarding createSRecord operation from frontend to server " + student);
        return dispatchRequestToCurrentServer(student);
    }

    @Override
    public String getRecordCounts(String id) {
        String req = OperationsEnum.GET_RECORD_COUNT + Constants.RECEIVED_SPLITTER + getServerLoc(id)
                + Constants.RECEIVED_SPLITTER + id;
        logUtil.log(TAG + "Forwarding getRecordCounts operation from frontend to server" + req);
        return dispatchRequestToCurrentServer(req);
    }

    @Override
    public String editRecord(String id, String recordID, String fieldName, String newValue) {
        String editData = OperationsEnum.EDIT_RECORD + Constants.RECEIVED_SPLITTER + getServerLoc(id)
                + Constants.RECEIVED_SPLITTER + id + Constants.RECEIVED_SPLITTER + recordID
                + Constants.RECEIVED_SPLITTER + fieldName + Constants.RECEIVED_SPLITTER + newValue;
        logUtil.log(TAG + "Forwarding editRecord operation from frontend to server " + editData);
        return dispatchRequestToCurrentServer(editData);
    }

    @Override
    public String transferRecord(String id, String recordId, String remoteCenterServerName) {
        String req = OperationsEnum.TRANSFER_RECORD + Constants.RECEIVED_SPLITTER + getServerLoc(id)
                + Constants.RECEIVED_SPLITTER + id + Constants.RECEIVED_SPLITTER + recordId
                + Constants.RECEIVED_SPLITTER + remoteCenterServerName;
        logUtil.log(TAG + "Forwarding transferRecord operation from frontend to server" + req);
        return dispatchRequestToCurrentServer(req);
    }

    @Override
    public String killPrimaryServer(String id) {
        String msg = "";
        if (id.equals("MTL")) {
            if (isMTLOneAlive && isMTLTwoAlive && isMTLThreeAlive) {
                isMTLOneAlive = false;
                primaryMtlServer.pingReceiverThread.setStatus(false);
                msg = "killing MTL1 Server " + bullyAlgorithm("MTL1");
            } else {
                msg = "primary server is found killed";
            }
        } else if (id.equals("LVL")) {
            if (isLVLOneAlive && isLVLTwoAlive && isLVLThreeAlive) {
                isLVLOneAlive = false;
                primaryLvlServer.pingReceiverThread.setStatus(false);
                msg = "killing LVL1 Server " + bullyAlgorithm("LVL1");
            } else {
                msg = "primary server is found killed";
            }
        } else if (id.equals("DDO")) {
            if (isDDOOneAlive && isDDOTwoAlive && isDDOThreeAlive) {
                isDDOOneAlive = false;
                primaryDdoServer.pingReceiverThread.setStatus(false);
                msg = "killing DDO1 server" + bullyAlgorithm("DDO1");
            } else {
                msg = "primary server is found killed";
            }
        }
        return msg;
    }

    public String dispatchRequestToCurrentServer(String data) {
        try {
            requestId += 1;
            DatagramSocket ds = new DatagramSocket();
            data = data + Constants.RECEIVED_SPLITTER + requestId;
            byte[] dataBytes = data.getBytes();
            DatagramPacket dp = new DatagramPacket(dataBytes, dataBytes.length,
                    InetAddress.getByName(Constants.CURRENT_SERVER_IP), Constants.CURRENT_SERVER_UDP_PORT);
            ds.send(dp);

            logUtil.log(TAG + "New request in buffer with request id : " + requestId);
            requestBuffer.put(requestId, data);
            logUtil.log(TAG + "Waiting for ACK reply from server: " + CURRENT_SERVER_IP + " : " + CURRENT_SERVER_UDP_PORT);
            Thread.sleep(Constants.RETRY_TIME);
            return getResponse(requestId);
        } catch (Exception e) {
            logUtil.log(TAG + "error: " + e.getMessage());
            return e.getMessage();
        }
    }

    public String getResponse(Integer requestId) {
        try {
            responsesMap.get(requestId).join();
        } catch (InterruptedException e) {
            logUtil.log(TAG + "error: " + e.getMessage());
        }
        requestBuffer.remove(requestId);
        return responsesMap.get(requestId).getResponse();
    }

    public void init() {
        try {

            statusMap.put(MTL1, true);
            statusMap.put(LVL1, true);
            statusMap.put(DDO1, true);
            statusMap.put(MTL2, false);
            statusMap.put(LVL2, false);
            statusMap.put(DDO2, false);
            statusMap.put(MTL3, false);
            statusMap.put(LVL3, false);
            statusMap.put(DDO3, false);

            ServerIDMap.put(MTL1, LEADER_ID);
            ServerIDMap.put(MTL2, S2_ID);
            ServerIDMap.put(MTL3, S3_ID);
            ServerIDMap.put(LVL1, LEADER_ID);
            ServerIDMap.put(LVL2, S2_ID);
            ServerIDMap.put(LVL3, S3_ID);
            ServerIDMap.put(DDO1, LEADER_ID);
            ServerIDMap.put(DDO2, S2_ID);
            ServerIDMap.put(DDO3, S3_ID);

            reportingMap.put(MTL1, System.currentTimeMillis());
            reportingMap.put(MTL2, System.currentTimeMillis());
            reportingMap.put(MTL3, System.currentTimeMillis());
            reportingMap.put(LVL1, System.currentTimeMillis());
            reportingMap.put(LVL2, System.currentTimeMillis());
            reportingMap.put(LVL3, System.currentTimeMillis());
            reportingMap.put(DDO1, System.currentTimeMillis());
            reportingMap.put(DDO2, System.currentTimeMillis());
            reportingMap.put(DDO3, System.currentTimeMillis());

            replicas.add(Constants.REPLICA1_SERVER_ID);
            replicas.add(Constants.REPLICA2_SERVER_ID);

            startFIFOThread();
            startUDPResponseThread();
            startMultiCastReceiverThread();
            startReplicaResponseThread();
            startReplicaOneMulticastReceiverThread();

            createPrimaryServers();
            createReplicaOneServers();
            createReplicaTwoServers();

            synchronized (repo) {
                repo.put(Constants.PRIMARY_SERVER_ID, primaryMap);
                repo.put(Constants.REPLICA1_SERVER_ID, replicaOneMap);
                repo.put(Constants.REPLICA2_SERVER_ID, replicaTwoMap);
            }

            startPingFromAllServers();
            startWatcherThread();

        } catch (Exception e) {

        }
    }

    private void startFIFOThread() {
        FIFOThread udpReceiverFromFE = new FIFOThread(requests, logUtil);
        udpReceiverFromFE.start();
    }

    private void startUDPResponseThread() {
        UDPResponseThread udpResponse = new UDPResponseThread(responsesMap, logUtil);
        udpResponse.start();
    }

    private void startMultiCastReceiverThread() {
        primaryReceiver = new MultiCastReceiverThread(true, ackManager);
        primaryReceiver.start();
    }

    private void startReplicaResponseThread() {
        replicaResponseReceiver = new ReplicaResponseThread(new LogUtil("ReplicasResponse"));
        replicaResponseReceiver.start();
    }

    private void startReplicaOneMulticastReceiverThread() {
        replica1Receiver = new MultiCastReceiverThread(false, ackManager);
        replica1Receiver.start();
    }

    private void createPrimaryServers() throws SocketException {
        DatagramSocket socket1 = new DatagramSocket();
        primaryMtlServer = new CenterServer(Constants.PRIMARY_SERVER_ID, true, LocationEnum.MTL,
                9999, socket1, isMTLOneAlive, MTL1, MTL1_PORT, MTL2_PORT,
                MTL3_PORT, replicas, getLogInstance("PRIMARY_SERVER", LocationEnum.MTL));

        primaryLvlServer = new CenterServer(Constants.PRIMARY_SERVER_ID, true, LocationEnum.LVL,
                7777, socket1, isLVLOneAlive, LVL1, LVL1_PORT, LVL2_PORT,
                LVL3_PORT, replicas, getLogInstance("PRIMARY_SERVER", LocationEnum.LVL));

        primaryDdoServer = new CenterServer(Constants.PRIMARY_SERVER_ID, true, LocationEnum.DDO,
                6666, socket1, isDDOOneAlive, DDO1, DDO1_PORT, DDO2_PORT,
                DDO3_PORT, replicas, getLogInstance("PRIMARY_SERVER", LocationEnum.DDO));

        primaryMap.put("MTL", primaryMtlServer);
        primaryMap.put("LVL", primaryLvlServer);
        primaryMap.put("DDO", primaryDdoServer);
    }

    private void createReplicaOneServers() throws SocketException {
        DatagramSocket socket2 = new DatagramSocket();
        replica1MtlServer = new CenterServer(Constants.REPLICA1_SERVER_ID, false,
                LocationEnum.MTL, 5555, socket2, isMTLTwoAlive, MTL2, MTL2_PORT,
                MTL1_PORT, MTL3_PORT, replicas,
                getLogInstance("REPLICA1_SERVER", LocationEnum.MTL));

        replica1LvlServer = new CenterServer(Constants.REPLICA1_SERVER_ID, false,
                LocationEnum.LVL, 4444, socket2, isLVLTwoAlive, LVL2, LVL2_PORT,
                LVL1_PORT, LVL3_PORT, replicas,
                getLogInstance("REPLICA1_SERVER", LocationEnum.LVL));

        replica1DdoServer = new CenterServer(Constants.REPLICA1_SERVER_ID, false,
                LocationEnum.DDO, 2222, socket2, isDDOTwoAlive, DDO2, DDO2_PORT,
                DDO1_PORT, DDO3_PORT, replicas,
                getLogInstance("REPLICA1_SERVER", LocationEnum.DDO));

        replicaOneMap.put("MTL", replica1MtlServer);
        replicaOneMap.put("LVL", replica1LvlServer);
        replicaOneMap.put("DDO", replica1DdoServer);
    }

    private void createReplicaTwoServers() throws SocketException {
        DatagramSocket socket3 = new DatagramSocket();
        replica2MtlServer = new CenterServer(Constants.REPLICA2_SERVER_ID, false,
                LocationEnum.MTL, 9878, socket3, isMTLThreeAlive, MTL3, MTL3_PORT,
                MTL1_PORT, MTL2_PORT, replicas,
                getLogInstance("REPLICA2_SERVER", LocationEnum.MTL));

        replica2LvlServer = new CenterServer(Constants.REPLICA2_SERVER_ID, false,
                LocationEnum.LVL, 9701, socket3, isLVLThreeAlive, LVL3, LVL3_PORT,
                LVL1_PORT, LVL2_PORT, replicas,
                getLogInstance("REPLICA2_SERVER", LocationEnum.LVL));

        replica2DdoServer = new CenterServer(Constants.REPLICA2_SERVER_ID, false,
                LocationEnum.DDO, 5655, socket3, isDDOThreeAlive, DDO3, DDO3_PORT,
                DDO1_PORT, DDO2_PORT, replicas,
                getLogInstance("REPLICA2_SERVER", LocationEnum.DDO));

        replicaTwoMap.put("MTL", replica2MtlServer);
        replicaTwoMap.put("LVL", replica2LvlServer);
        replicaTwoMap.put("DDO", replica2DdoServer);
    }

    private void startPingFromAllServers() {
        Thread thread1 = new Thread() {
            public void run() {
                while (getStatus(MTL1)) {
                    primaryMtlServer.ping();
                }
            }
        };
        Thread thread2 = new Thread() {
            public void run() {
                while (getStatus(MTL2)) {
                    replica1MtlServer.ping();
                }
            }
        };
        Thread thread3 = new Thread() {
            public void run() {
                while (getStatus(MTL3)) {
                    replica2MtlServer.ping();
                }
            }
        };
        Thread thread4 = new Thread() {
            public void run() {
                while (getStatus(LVL1)) {
                    primaryLvlServer.ping();
                }
            }
        };
        Thread thread5 = new Thread() {
            public void run() {
                while (getStatus(LVL2)) {
                    replica1LvlServer.ping();
                }
            }
        };
        Thread thread6 = new Thread() {
            public void run() {
                while (getStatus(LVL3)) {
                    replica2LvlServer.ping();
                }
            }
        };
        Thread thread7 = new Thread() {
            public void run() {
                while (getStatus(DDO1)) {
                    primaryDdoServer.ping();
                }
            }
        };
        Thread thread8 = new Thread() {
            public void run() {
                while (getStatus(DDO2)) {
                    replica1DdoServer.ping();
                }
            }
        };
        Thread thread9 = new Thread() {
            public void run() {
                while (getStatus(DDO3)) {
                    replica2DdoServer.ping();
                }
            }
        };
        thread1.start();
        thread2.start();
        thread3.start();
        thread4.start();
        thread5.start();
        thread6.start();
        thread7.start();
        thread8.start();
        thread9.start();
    }

    private void startWatcherThread() {
        Thread statusChecker = new Thread() {
            public void run() {
                while (true) {
                    checkServerStatus("MTL1");
                    checkServerStatus("MTL2");
                    checkServerStatus("MTL3");
                    checkServerStatus("LVL1");
                    checkServerStatus("LVL2");
                    checkServerStatus("LVL3");
                    checkServerStatus("DDO1");
                    checkServerStatus("DDO2");
                    checkServerStatus("DDO3");
                }
            }
        };
        statusChecker.start();
    }

}