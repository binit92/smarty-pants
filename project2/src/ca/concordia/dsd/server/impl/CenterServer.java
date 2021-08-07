package ca.concordia.dsd.server.impl;

import ca.concordia.dsd.database.Record;
import ca.concordia.dsd.database.Student;
import ca.concordia.dsd.database.Teacher;
import ca.concordia.dsd.server.frontend.FrontEnd;
import ca.concordia.dsd.server.impl.ping.PingReceiverThread;
import ca.concordia.dsd.server.impl.ping.PingSenderThread;
import ca.concordia.dsd.server.impl.replica.ReplicaHandler;
import ca.concordia.dsd.server.impl.udp.UDPRequestProviderThread;
import ca.concordia.dsd.server.impl.udp.UDPRequestReceiverThread;
import ca.concordia.dsd.util.Constants;
import ca.concordia.dsd.util.LocationEnum;
import ca.concordia.dsd.util.LogUtil;

import java.io.IOException;
import java.net.DatagramSocket;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map.Entry;
import java.util.Optional;


//public class DcmsServerImpl extends corbaPOA{
public class CenterServer {
    private final String TAG = "|" + CenterServer.class.getSimpleName() + "| ";
    public HashMap<String, List<Record>> recordsMap;
    public PingReceiverThread pingReceiverThread;
    public ArrayList<Integer> replicas;
    public String location;
    public int locUDPPort = 0;
    ca.concordia.dsd.server.impl.udp.UDPRequestReceiverThread UDPRequestReceiverThread;
    String IPaddress;
    Object recordsMapAccessorLock = new Object();
    int studentCount = 0;
    int teacherCount = 0;
    String recordsCount;
    boolean isPrimary;
    Integer serverID = 0;
    PingSenderThread pingSenderThread;
    String name;
    int port1, port2;
    boolean isAlive;
    DatagramSocket ds = null;
    private LogUtil logManager;

    public CenterServer(int serverID, boolean isPrimary, LocationEnum loc, int locUDPPort, DatagramSocket ds,
                        boolean isAlive, String name, int receivePort, int port1, int port2, ArrayList<Integer> replicas,
                        LogUtil logUtil) {
        logManager = logUtil;
        synchronized (recordsMapAccessorLock) {
            recordsMap = new HashMap<>();
        }
        this.locUDPPort = locUDPPort;
        UDPRequestReceiverThread = new UDPRequestReceiverThread(true, locUDPPort, loc, logManager, this);
        UDPRequestReceiverThread.start();
        location = loc.toString();
        this.isPrimary = isPrimary;
        this.serverID = serverID;
        this.name = name;
        this.port1 = port1;
        this.port2 = port2;
        this.isAlive = isAlive;
        pingReceiverThread = new PingReceiverThread(isAlive, name, receivePort, logManager);
        pingReceiverThread.start();
        this.ds = ds;
        this.replicas = replicas;
    }

    public int getlocUDPPort() {
        return this.locUDPPort;
    }

    //@Override
    public synchronized String createTRecord(String managerID, String teacher) {
        if (isPrimary) {
            for (Integer replicaId : replicas) {
                ReplicaHandler req = new ReplicaHandler(replicaId,
                        logManager);
                req.createTRecord(managerID, teacher);
            }
        }
        String temp[] = teacher.split(",");
        String teacherID = "TR" + (++teacherCount);
        String firstName = temp[0];
        String lastname = temp[1];
        String address = temp[2];
        String phone = temp[3];
        String specialization = temp[4];
        String location = temp[5];
        String requestID = temp[6];
        Teacher teacherObj = new Teacher(managerID, teacherID, firstName, lastname, address, phone, specialization,
                location);
        String key = lastname.substring(0, 1);
        String message = addRecordToHashMap(key, teacherObj, null);
        if (message.equals("success")) {
            System.out.println("teacher is added " + teacherObj + " with this key " + key + " by Manager " + managerID
                    + " for the request ID: " + requestID);
            logManager.log(TAG + "Teacher record created " + teacherID + " by Manager : " + managerID
                    + " for the request ID: " + requestID);
        } else {
            logManager.log(TAG + "Error in creating T record" + requestID);
            return "Error in creating T record";
        }

        return teacherID;

    }


    //@Override
    public synchronized String createSRecord(String managerID, String student) {
        if (isPrimary) {
            for (Integer replicaId : replicas) {
                ReplicaHandler req = new ReplicaHandler(replicaId,
                        logManager);
                req.createSRecord(managerID, student);
            }
        }
        String temp[] = student.split(",");
        String firstName = temp[0];
        String lastName = temp[1];
        String CoursesRegistered = temp[2];
        List<String> courseList = putCoursesinList(CoursesRegistered);
        String status = temp[3];
        String statusDate = temp[4];
        String requestID = temp[5];
        String studentID = "SR" + (++studentCount);
        Student studentObj = new Student(managerID, studentID, firstName, lastName, courseList, status, statusDate);
        String key = lastName.substring(0, 1);
        String message = addRecordToHashMap(key, null, studentObj);
        if (message.equals("success")) {
            System.out.println(" Student is added " + studentObj + " with this key " + key + " by Manager " + managerID
                    + " for the requestID " + requestID);
            logManager.log(TAG + "Student record created " + studentID + " by manager : " + managerID
                    + " for the requestID " + requestID);
        } else {
            return "Error in creating S record";
        }
        return studentID;
    }


    private synchronized int getCurrServerCnt() {
        int count = 0;
        synchronized (recordsMapAccessorLock) {
            for (Entry<String, List<Record>> entry : this.recordsMap.entrySet()) {
                List<Record> list = entry.getValue();
                count += list.size();
            }
        }
        return count;
    }


    //@Override
    public synchronized String getRecordCount(String manager) {
        if (isPrimary) {
            for (Integer replicaId : replicas) {
                ReplicaHandler req = new ReplicaHandler(replicaId,
                        logManager);
                req.getRecordCount(manager);
            }
        }
        String data[] = manager.split(Constants.RECEIVED_DATA_SEPERATOR);
        String managerID = data[0];
        String requestID = data[1];
        String recordCount = null;
        UDPRequestProviderThread[] req = new UDPRequestProviderThread[2];
        int counter = 0;
        ArrayList<String> locList = new ArrayList<>();
        locList.add("MTL");
        locList.add("LVL");
        locList.add("DDO");
        for (String loc : locList) {
            // System.out.println("11>>>>>>>>>>>>>>>>>>>>>>>>>>>Now serving
            // location :: " + loc);
            if (loc == this.location) {
                recordCount = loc + " " + getCurrServerCnt();
            } else {
                try {
                    // System.out.println("22>>>>>>>>>>>>>>>>>>>>>>>>>>>Now
                    // serving location :: " + loc);
                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    System.out.println("Server id :: " + serverID);
                    req[counter] = new UDPRequestProviderThread(
                            FrontEnd.repo.get(serverID).get(loc), "GET_RECORD_COUNT", null,
                            logManager);
                } catch (IOException e) {
                    System.out.println("Exception in get rec count :: " + e.getMessage());
                    logManager.log(TAG + e.getMessage());
                }
                req[counter].start();
                counter++;
            }
        }
        for (UDPRequestProviderThread request : req) {
            try {
                request.join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            recordCount += " , " + request.getRemoteRecordCount().trim();
        }
        System.out.println(
                recordCount + " for the request ID " + requestID + " as requested by the managerID " + managerID);
        logManager.log(TAG +
                recordCount + " for the request ID " + requestID + " as requested by the managerID " + managerID);
        return recordCount;
    }

    //@Override
    public String editRecord(String id, String recordID, String fieldName, String newValue) {
        if (isPrimary) {
            for (Integer replicaId : replicas) {
                ReplicaHandler req = new ReplicaHandler(replicaId,
                        logManager);
                req.editRecord(id, recordID, fieldName, newValue);
            }
        }
        String data[] = newValue.split(Constants.RECEIVED_DATA_SEPERATOR);
        String requestID = data[1];
        String type = recordID.substring(0, 2);
        if (type.equals("TR")) {
            return editTRRecord(id, recordID, fieldName, newValue);
        } else if (type.equals("SR")) {
            return editSRRecord(id, recordID, fieldName, newValue);
        }
        logManager.log(TAG + "Record edit successful for the request ID " + requestID);
        return "Operation not performed!";
    }

    public synchronized String transferRecord(String managerID, String recordID, String data) {
        if (isPrimary) {
            for (Integer replicaId : replicas) {
                ReplicaHandler req = new ReplicaHandler(replicaId,
                        logManager);
                req.transferRecord(managerID, recordID, data);
            }
        }
        String parsedata[] = data.split(Constants.RECEIVED_DATA_SEPERATOR);
        String remoteCenterServerName = parsedata[0];
        String requestID = parsedata[1];
        String type = recordID.substring(0, 2);
        UDPRequestProviderThread req = null;
        UDPRequestProviderThread req1 = null;
        try {
            Record record = getRecordForTransfer(recordID);
            if (record == null) {
                return "RecordID unavailable!";
            } else if (remoteCenterServerName.equals(this.location)) {
                return "Please enter a valid location to transfer. The record is already present in " + location;
            }
            req = new UDPRequestProviderThread(
                    FrontEnd.repo.get(serverID).get(remoteCenterServerName.trim()), "TRANSFER_RECORD",
                    record, logManager);

            if (isPrimary && this.replicas.size() == Constants.TOTAL_REPLICAS_COUNT - 1) {
                System.out.println("Replicas size is ::::::::::: 1" + remoteCenterServerName);
                req1 = new UDPRequestProviderThread(FrontEnd.repo.get(Constants.REPLICA2_SERVER_ID)
                        .get(remoteCenterServerName.trim()), "TRANSFER_RECORD", record, logManager);
                req1.start();
                try {
                    req1.join();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        } catch (IOException e) {
            logManager.log(TAG + e.getMessage());
        }
        if (req != null) {
            req.start();
        }
        try {
            if (req != null) {
                req.join();
            }
            if (removeRecordAfterTransfer(recordID) == "success") {
                logManager.log(TAG + "Record created in  " + remoteCenterServerName + "  and removed from "
                        + location + " with requestID " + requestID);
                System.out.println("Record created in " + remoteCenterServerName + "and removed from " + location
                        + " with requestID " + requestID);
                return "Record created in " + remoteCenterServerName + "and removed from " + location;
            }
        } catch (Exception e) {
            System.out.println("Exception in transfer record :: " + e.getMessage());
        }

        return "Transfer record operation unsuccessful!";
    }

    private synchronized String removeRecordAfterTransfer(String recordID) {
        synchronized (recordsMapAccessorLock) {
            for (Entry<String, List<Record>> element : recordsMap.entrySet()) {
                List<Record> mylist = element.getValue();
                for (int i = 0; i < mylist.size(); i++) {
                    if (mylist.get(i).getRecordID().equals(recordID)) {
                        mylist.remove(i);
                    }
                }
                recordsMap.put(element.getKey(), mylist);
            }
            System.out.println("Removed record from " + this.location);
        }
        return "success";
    }

    private synchronized Record getRecordForTransfer(String recordID) {
        synchronized (recordsMapAccessorLock) {
            for (Entry<String, List<Record>> value : recordsMap.entrySet()) {
                List<Record> mylist = value.getValue();
                Optional<Record> record = mylist.stream().filter(x -> x.getRecordID().equals(recordID)).findFirst();
                if (recordID.contains("TR")) {
                    if (record.isPresent())
                        return (Teacher) record.get();
                } else {
                    if (record.isPresent())
                        return (Student) record.get();
                }
            }
        }
        return null;
    }

    private synchronized String editSRRecord(String maangerID, String recordID, String fieldname, String data) {
        String newdata[] = data.split(Constants.RECEIVED_DATA_SEPERATOR);
        String newvalue = newdata[0];
        String requestID = newdata[1];
        for (Entry<String, List<Record>> value : recordsMap.entrySet()) {
            List<Record> mylist = value.getValue();
            Optional<Record> record = mylist.stream().filter(x -> x.getRecordID().equals(recordID)).findFirst();
            if (record.isPresent()) {
                if (record.isPresent() && fieldname.equals("Status")) {
                    ((Student) record.get()).setStatus(newvalue);
                    logManager.log(TAG + maangerID + " performed the operation with the requestID "
                            + requestID + " and Updated the records\t" + location);
                    System.out.println("Record with recordID " + recordID + "update with new " + fieldname + " as "
                            + newvalue + " with requestID " + requestID);
                    return "Updated record with status :: " + newvalue;
                } else if (record.isPresent() && fieldname.equals("StatusDate")) {
                    ((Student) record.get()).setStatusDate(newvalue);
                    logManager.log(TAG + maangerID + " performed the operation with the requestID "
                            + requestID + "Updated the records\t" + location);
                    System.out.println("Record with recordID " + recordID + "update with new " + fieldname + " as "
                            + newvalue + " with requestID " + requestID);
                    return "Updated record with status date :: " + newvalue;
                } else if (record.isPresent() && fieldname.equals("CoursesRegistered")) {
                    List<String> courseList = putCoursesinList(newvalue);
                    ((Student) record.get()).setCoursesRegistered(courseList);
                    logManager.log(TAG + maangerID + " performed the operation with the requestID "
                            + requestID + "Updated the courses registered\t" + location);
                    System.out.println("Record with recordID " + recordID + "update with new " + fieldname + " as "
                            + newvalue + " with requestID " + requestID);
                    return "Updated record with courses :: " + courseList;
                } else {
                    System.out.println("Record with " + recordID + " not found");
                    logManager.log(TAG + "Record with " + recordID + "not found!" + location);
                    return "Record with " + recordID + " not found";
                }
            }
        }
        return "Record with " + recordID + "not found!";
    }


    private synchronized String editTRRecord(String managerID, String recordID, String fieldname, String data) {
        String newdata[] = data.split(Constants.RECEIVED_DATA_SEPERATOR);
        String newvalue = newdata[0];
        String requestID = newdata[1];
        for (Entry<String, List<Record>> val : recordsMap.entrySet()) {
            List<Record> mylist = val.getValue();
            Optional<Record> record = mylist.stream().filter(x -> x.getRecordID().equals(recordID)).findFirst();

            if (record.isPresent()) {
                if (record.isPresent() && fieldname.equals("Phone")) {
                    ((Teacher) record.get()).setPhone(newvalue);
                    logManager.log(TAG + managerID + " performed the operation with the requestID "
                            + requestID + "Updated the records\t" + location);
                    System.out.println("Record with recordID " + recordID + "update with new " + fieldname + " as "
                            + newvalue + " with requestID " + requestID);
                    return "Updated record with Phone :: " + newvalue;
                } else if (record.isPresent() && fieldname.equals("Address")) {
                    ((Teacher) record.get()).setAddress(newvalue);
                    logManager.log(TAG + managerID + " performed the operation with the requestID "
                            + requestID + "Updated the records\t" + location);
                    System.out.println("Record with recordID " + recordID + "update with new " + fieldname + " as "
                            + newvalue + " with requestID " + requestID);
                    return "Updated record with address :: " + newvalue;
                } else if (record.isPresent() && fieldname.equals("Location")) {
                    ((Teacher) record.get()).setLocation(newvalue);
                    logManager.log(TAG + managerID + " performed the operation with the requestID "
                            + requestID + "Updated the records\t" + location);
                    System.out.println("Record with recordID " + recordID + "update with new " + fieldname + " as "
                            + newvalue + " with requestID " + requestID);
                    return "Updated record with location :: " + newvalue;
                } else {
                    System.out.println("Record with " + recordID + " not found");
                    logManager.log(TAG + "Record with " + recordID + "not found!" + location);
                    return "Record with " + recordID + " not found";
                }
            }
        }
        return "Record with " + recordID + " not found";
    }


    public void ping() {
        pingSenderThread = new PingSenderThread(ds, name, port1, port2);
        pingSenderThread.start();
    }

    public boolean isPrimary() {
        return isPrimary;
    }

    public void setPrimary(boolean isPrimary) {
        this.isPrimary = isPrimary;
    }

    public ArrayList<Integer> getReplicas() {
        return replicas;
    }

    public void setReplicas(ArrayList<Integer> replicas) {
        this.replicas = replicas;
    }

    //@Override
    public String killPrimaryServer(String id) {
        return null;
    }

    public Integer getServerID() {
        return serverID;
    }

    public void setServerID(Integer serverID) {
        this.serverID = serverID;
    }


    public synchronized String addRecordToHashMap(String key, Teacher teacher, Student student) {
        String message = "Error";
        if (teacher != null) {
            List<Record> recordList = null;
            synchronized (recordsMapAccessorLock) {
                recordList = recordsMap.get(key);
            }
            if (recordList != null) {
                recordList.add(teacher);
            } else {
                List<Record> records = null;
                synchronized (recordsMapAccessorLock) {
                    records = new ArrayList<Record>();
                    records.add(teacher);
                }
                recordList = records;
            }
            synchronized (recordsMapAccessorLock) {
                recordsMap.put(key, recordList);
            }
            message = "success";
        }

        if (student != null) {
            List<Record> recordList = null;
            synchronized (recordsMapAccessorLock) {
                recordList = recordsMap.get(key);
            }
            if (recordList != null) {
                recordList.add(student);
            } else {
                List<Record> records = null;
                synchronized (recordsMapAccessorLock) {
                    records = new ArrayList<Record>();
                    records.add(student);
                }
                recordList = records;
            }
            synchronized (recordsMapAccessorLock) {
                recordsMap.put(key, recordList);
            }
            message = "success";
        }
        return message;
    }

    public synchronized List<String> putCoursesinList(String newvalue) {
        String[] courses = newvalue.split("//");
        ArrayList<String> courseList = new ArrayList<>();
        for (String course : courses)
            courseList.add(course);
        return courseList;
    }
}