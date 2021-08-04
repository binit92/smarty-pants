package ca.concordia.dsd.server.impl;

import ca.concordia.dsd.arch.corbaPOA;
import ca.concordia.dsd.database.Records;
import ca.concordia.dsd.database.StudentRecord;
import ca.concordia.dsd.database.TeacherRecord;
import ca.concordia.dsd.server.UDPProviderThread;
import ca.concordia.dsd.server.UDPThread;
import ca.concordia.dsd.util.Constants;
import ca.concordia.dsd.util.LogUtil;
import org.omg.CORBA.ORB;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.util.*;
import java.util.stream.Collectors;

public class CenterServerImpl extends corbaPOA  {
    private static final String LOG_TAG = "| " + CenterServerImpl.class.getSimpleName() + "| ";

    private LogUtil logUtil;
    public UDPThread udpThread;
    public String IPaddress;

    int port = 0;
    int udpPort = 0;

    private ORB ddoorb;

    public HashMap<String, List<Records>> recordsMap;
    private static int studentCount = 10001;
    private static int teacherCount = 10001;
    private int recordsCount;
    private final String serverName;

    private static final Object lockID = new Object();
    private static final Object lockCount = new Object();

    public CenterServerImpl(String serverName, int port, int udpPort) {
        this.serverName = serverName;
        this.port = port;
        this.udpPort = udpPort;
        logUtil = new LogUtil(serverName);
        recordsMap = new HashMap<>();
        udpThread = new UDPThread(this.serverName, this);
        udpThread.start();
        setIPAddress(serverName);
    }

    public void setORB(ORB ddoobjectorb) {
        this.ddoorb = ddoobjectorb;
    }

    //implement shutdown
    public void shutdown(){
        this.ddoorb.shutdown(false);
    }

    private void setIPAddress(String serverName) {
        switch (serverName) {
            case Constants.MTL_TAG:
                IPaddress = Constants.MTL_SERVER_HOST;
                break;
            case Constants.LVL_TAG:
                IPaddress = Constants.LVL_SERVER_HOST;
                break;
            case Constants.DDO_TAG:
                IPaddress = Constants.DDO_SERVER_HOST;
                break;
        }
    }

    private synchronized String addToDB(String key, TeacherRecord tR, StudentRecord sR) {
        String ret = "error";
        if (tR != null) {
            List<Records> recordList = recordsMap.get(key);
            if (recordList != null) {
                recordList.add(tR);
            } else {
                List<Records> records = new ArrayList<Records>();
                records.add(tR);
                recordList = records;
            }
            recordsMap.put(key, recordList);
            ret = "success";
        }

        if (sR != null) {
            List<Records> recordList = recordsMap.get(key);
            if (recordList != null) {
                recordList.add(sR);
            } else {
                List<Records> records = new ArrayList<Records>();
                records.add(sR);
                recordList = records;
            }
            recordsMap.put(key, recordList);
            ret = "success";
        }

        return ret;
    }

    private int getServerCount(){
        int c = 0;
        for (Map.Entry<String, List<Records>> e : this.recordsMap.entrySet()) {
            List<Records> total = e.getValue();
            c+=total.size();
        }
        return c;
    }


    public String createTRecord(String id, String fName, String lName, String address, String phone, String specialization, String location) {
        logUtil.log(id,"Create record called for teacher : " + fName);
        String teacherid = "TR" + (++teacherCount);
        TeacherRecord tR = new TeacherRecord(teacherid,fName,lName,address,phone,specialization,location);
        //tR.setTeacherId(teacherid);
        //tR.setUniqueId(teacherid);

        String key = tR.getLastName().substring(0, 1);
        String ret = addToDB(key, tR, null);
        //TODO : fix return
        logUtil.log(id,"new teacher " + tR.getFirstName() + " with this key " + key);
        logUtil.log(id,"teacher id " + teacherid);

        return teacherid;
        //return true;
    }

    public String createSRecord(String id, String fName, String lName, String courses, boolean status, String statusDate) {
        logUtil.log(id,"Create record called for student : " + fName);
        String studentid = "SR" + (studentCount + 1);
        // TODO: fix this : courses, status
        StudentRecord sR = new StudentRecord(studentid,fName,lName,null, "True",statusDate);
        //sR.setUniqueId(studentid);
        //sR.setStudentID(studentid);

        String key = sR.getLastName().substring(0, 1);
        String ret = addToDB(key, null, sR);

        // TODO: return ret
        logUtil.log(id," new student is added " + sR + " with this key " + key);
        logUtil.log(id, "student record created " + studentid);
        return studentid;
        //return true;
    }

    public synchronized String getRecordCounts(String manager) {
        logUtil.log(manager,"get record counts ");
        String recordCount = null;
        UDPProviderThread[] req = new UDPProviderThread[2];
        int i = 0;
        ArrayList<String> locList = new ArrayList<>();
        locList.add(Constants.MTL_TAG);
        locList.add(Constants.LVL_TAG);
        locList.add(Constants.DDO_TAG);
        for (String loc : locList) {
            if (loc== this.serverName) {
                recordCount = loc+","+ getServerCount();
            } else {
                try {
                    String ipAdd = Constants.MTL_SERVER_HOST;
                    // todo : how to contact replica when the leader is down ?
                    int udpPort = Constants.MTL_UDP_PORT_LEADER;
                    if(loc.equalsIgnoreCase(Constants.LVL_TAG)){
                        ipAdd = Constants.LVL_SERVER_HOST;
                        udpPort = Constants.LVL_UDP_PORT_LEADER;
                    }else if(loc.equalsIgnoreCase(Constants.DDO_TAG)){
                        ipAdd = Constants.DDO_SERVER_HOST;
                        udpPort = Constants.DDO_UDP_PORT_LEADER;
                    }
                    req[i] = new UDPProviderThread(loc, ipAdd,udpPort);
                } catch (IOException e) {
                    logUtil.log(manager,e.getMessage());
                }
                req[i].start();
                i++;
            }
        }
        for (UDPProviderThread request : req) {
            try {
                request.join();
                recordCount += " , " + request.getRemoteRecordCount().trim();

            } catch (InterruptedException e) {
                //e.printStackTrace();
                logUtil.log(e.getMessage());
            }catch (NullPointerException npe){
                logUtil.log("Not all servers are reachable");
            }
        }
        logUtil.log(manager,"record count " + recordCount);
        return recordCount;
    }

    public String editRecord(String manager, String id, String key, String val)  {
        String type = id.substring(0, 2);

        if (type.equalsIgnoreCase("TR")) {
            return editTRRecord(manager,id, key, val);
        }

        else if (type.equalsIgnoreCase("SR")) {
            return editSRRecord(manager,id, key, val);
        }
        logUtil.log(manager, "Operation invalid");
        return "Operation invalid";
    }

    public String transferRecord(String id, String recordId, String remoteCenterServerName) {
        String serverTOConnect = "";
        int serverTOConnectUDPPort = 0;
        if(serverName.equalsIgnoreCase(Constants.DDO_TAG)){
            serverTOConnect = Constants.DDO_SERVER_HOST;
            serverTOConnectUDPPort = Constants.DDO_UDP_PORT_LEADER;
        }else if (serverName.equalsIgnoreCase(Constants.MTL_TAG)){
            serverTOConnect = Constants.MTL_SERVER_HOST;
            serverTOConnectUDPPort = Constants.MTL_UDP_PORT_LEADER;
        }else if (serverName.equalsIgnoreCase(Constants.LVL_TAG)){
            serverTOConnect = Constants.LVL_SERVER_HOST;
            serverTOConnectUDPPort = Constants.LVL_UDP_PORT_LEADER;
        }else{
            logUtil.log(id,"Invalid server name ");
            return "fail";
        }
        String result = "";
        if(remoteCenterServerName.compareTo(serverName)!= 0){

            synchronized ((recordsMap)){
                for(List<Records> recordList : recordsMap.values()){
                    Iterator<Records> iter = recordList.iterator();
                    while(iter.hasNext()){
                        Records found = iter.next();
                        if(found.getUniqueId().compareTo(recordId) == 0){
                            // not found so copy this record to another server
                            // Using UDP to copy the record
                            DatagramSocket socket = null;
                            try{
                                socket = new DatagramSocket();
                                // timeout in 5 seconds
                                socket.setSoTimeout(5 * 1000);
                                String req = "";
                                InetAddress addr = InetAddress.getByName(serverTOConnect);
                                if(found.getType() == Records.RecordType.TEACHER){
                                    TeacherRecord tr = (TeacherRecord) found;
                                    StringBuilder br = new StringBuilder();
                                    br.append("TR");br.append("|");
                                    br.append(id);br.append("|");
                                    br.append(recordId);br.append("|");
                                    br.append(tr.getFirstName());br.append("|");
                                    br.append(tr.getLastName());br.append("|");
                                    br.append(tr.getAddress());br.append("|");
                                    br.append(tr.getPhone());br.append("|");
                                    br.append(tr.getSpecialization());br.append("|");
                                    br.append(tr.getLocation());br.append("|");
                                    req = br.toString();

                                }else{
                                    StudentRecord sr = (StudentRecord) found;
                                    StringBuilder br = new StringBuilder();
                                    br.append("SR");br.append("|");
                                    br.append(id);br.append("|");
                                    br.append(recordId);br.append("|");
                                    br.append(sr.getFirstName());br.append("|");
                                    br.append(sr.getLastName());br.append("|");
                                    String coursesStr = sr.getCoursesRegistered().stream()
                                            .map(n -> String.valueOf(n))
                                            .collect(Collectors.joining("-", "{", "}"));
                                    br.append(coursesStr);br.append("|");
                                    br.append(sr.getStatus());br.append("|");
                                    br.append(sr.getStatusDate());br.append("|");

                                }
                                byte[] request = req.getBytes();
                                DatagramPacket pckt = new DatagramPacket(request,req.length(),addr,serverTOConnectUDPPort);
                                socket.send(pckt);

                                byte[] response = new byte[1024];
                                DatagramPacket receivedpckt = new DatagramPacket(response, response.length);
                                socket.receive(receivedpckt);
                                result = new String(receivedpckt.getData()).trim();

                            }catch(Exception e){
                                logUtil.log(e.getMessage());
                            }finally {
                                if(socket != null){
                                    socket.close();
                                }
                            }

                            // when data is copied through UDP, delete the record
                            if(result.compareTo(recordId) == 0){
                                synchronized (lockCount){
                                    recordList.remove(found);
                                    recordsCount--;
                                    logUtil.log(id, recordId + " transferred to " + remoteCenterServerName);
                                }
                                return "success";
                            }else{
                                logUtil.log(id, recordId + " failed to transfer to "+ remoteCenterServerName);
                                return "fail";
                            }

                        }

                    }
                }
            }
        }
        return result;
    }

    private synchronized String editSRRecord(String manager,String recordID, String key, String val) {

        for (Map.Entry<String, List<Records>> value : recordsMap.entrySet()) {
            List<Records> mylist = value.getValue();
            Optional<Records> record = mylist.stream().filter(x -> x.getUniqueId().equals(recordID)).findFirst();
            if (record.isPresent()) {
                if (record.isPresent() && key.equals("Status")) {
                    ((StudentRecord) record.get()).setStatus(val);
                    logUtil.log("Records update for : " + serverName);
                    return "Records updated with status : "+val;
                } else if (record.isPresent() && key.equals("StatusDate")) {
                    ((StudentRecord) record.get()).setStatusDate(val);
                    logUtil.log("Records update for : " + serverName);
                    return "Records updated with status : "+val;
                }
            }
        }
        return "Record : " + recordID+ " is not found";
    }

    private String editTRRecord(String manager,String recordID, String key, String val) {
       for (Map.Entry<String, List<Records>> value : recordsMap.entrySet()) {
           List<Records> mylist = value.getValue();
           Optional<Records> record = mylist.stream().filter(x -> x.getUniqueId().equals(recordID)).findFirst();

           if (record.isPresent()) {
               if (record.isPresent() && key.equalsIgnoreCase("Phone")) {
                   ((TeacherRecord) record.get()).setPhone(val);
                   logUtil.log(manager,"Records update for : " + serverName);
                    return "Records updated with status : "+val;
                }

                else if (record.isPresent() && key.equalsIgnoreCase("Address")) {
                    ((TeacherRecord) record.get()).setAddress(val);
                    logUtil.log(manager,"Records update for : " + serverName);
                    return "Records updated with status : "+val;
                }

                else if (record.isPresent() && key.equalsIgnoreCase("Location")) {
                    ((TeacherRecord) record.get()).setLocation(val);
                    logUtil.log(manager,"Records update for : " + serverName);
                    return "Records updated with status : "+val;
                }
            }
        }
       return "Record : " + recordID+ " is not found";
   }


    public String editCourses(String manager, String id, String key, ArrayList<String> values) {
        for (Map.Entry<String, List<Records>> value : recordsMap.entrySet()) {

            List<Records> list = value.getValue();
            Optional<Records> record = list.stream().filter(x -> x.getUniqueId().equals(id)).findFirst();
            if (record.isPresent() && key.equalsIgnoreCase("courses")) {
                ((StudentRecord) record.get()).setCoursesRegistered(values);
                logUtil.log("Records update for : " + serverName);
            }
        }
        return null;
    }

    public void startUDPServer(){
        DatagramSocket socket = null;
        try{
            socket = new DatagramSocket(this.udpPort);
            while(true){
                //Get the request
                byte[] buffer =new byte[1024];
                DatagramPacket request = new DatagramPacket(buffer, buffer.length);
                socket.receive(request);

                // Each request will be handled by a separate thread making sure no request will miss..
                DatagramSocket threadSocket = socket;
                new Thread(() -> {
                    String reply = "-1";
                    String reqStr = new String(request.getData()).trim();
                    String[] reqArr= reqStr.split("|");
                    switch(reqArr[0]){
                        case "TR":
                            reply = transferTR(reqArr[1],reqArr[2],reqArr[3],reqArr[4],reqArr[5],reqArr[6],reqArr[7],reqArr[8]);
                            break;
                        case "SR":
                            reply = transferSR(reqArr[1],reqArr[2],reqArr[3],reqArr[4],reqArr[5],reqArr[6],reqArr[7]);
                    }
                    // reply back
                    DatagramPacket response = new DatagramPacket(reply.getBytes(),reply.length(),request.getAddress(),request.getPort());
                    try{
                        threadSocket.send(response);
                    }catch (IOException io){
                        logUtil.log(io.getMessage());
                    }
                }).start();
            }
        }catch(Exception e){
            logUtil.log(e.getMessage());
        }finally{
            if(socket != null){
                socket.close();
            }
        }
    }

    private String transferTR(String id, String recordID, String fName, String lName, String addr, String phone, String spec, String loc){
        //Creating new teacher record
        TeacherRecord newTR = new TeacherRecord(recordID, fName,lName,addr, phone,spec, loc);
        //using synchronized here so that mututal exclusion can happend with multiple threads calling concurrently ..
        synchronized (recordsMap){
            List<Records> recordsList;
            String key = lName.substring(0, 1);
            if(recordsMap.containsKey(lName)){
                recordsList = recordsMap.get(key);
            }else{
                recordsList = new ArrayList<>();
                recordsMap.put(key,recordsList);
            }
            // Adding new record here
            recordsList.add(newTR);
            recordsCount++;
            logUtil.log(id, "record tranferred, record ID : " + recordID + " teacher name : " + fName );
        }
        return recordID;
    }

    private String transferSR(String id, String recordId, String fName,String lName, String courses, String status, String date){
        ArrayList<String> coursesRegistered = new ArrayList<>();
        try {
            coursesRegistered = (ArrayList<String>) Arrays.asList(courses.split(","));
        }catch (Exception e){
            logUtil.log(e.getMessage());
        }
        StudentRecord newSR = new StudentRecord(recordId,fName,lName,coursesRegistered,status,date);
        //using synchronized here so that mututal exclusion can happend with multiple threads calling concurrently ..
        synchronized (recordsMap){
            List<Records> recordsList;
            String key = lName.substring(0,1);
            if(recordsMap.containsKey(lName)){
                recordsList = recordsMap.get(key);
            }else{
                recordsList = new ArrayList<>();
                recordsMap.put(key,recordsList);
            }
            // Adding new record here
            recordsList.add(newSR);
            recordsCount++;
            logUtil.log(id,"record trasferred, record ID: " + recordId + " student name: " + fName);
        }
        return recordId;
    }

    private String udpClient(int port) {
        System.out.println("--> udpclient port: " + port);
        DatagramSocket socket = null;
        String response= null;
        try {
            socket=new DatagramSocket();
            // 10 second timeout
            socket.setSoTimeout(1000 * 5);
            InetAddress aHost= InetAddress.getByName("localhost");
            String data= "requesting data";
            DatagramPacket request= new DatagramPacket(data.getBytes(), data.getBytes().length, aHost, port);
            socket.send(request);
            byte[] buffer= new byte[1000];
            DatagramPacket reply= new DatagramPacket(buffer, buffer.length);
            socket.receive(reply);
            response=  new String(reply.getData());

            System.out.println("reply is "+ new String(reply.getData()));
        }
        catch (Exception e) {
            System.out.println("udpclient: "+e.getMessage());
        }
        finally {
            if(socket!=null) {
                socket.close();
            }
        }
        System.out.println("-->response ");
        return response;
    }

    public String getServerName(){
        return serverName;
    }
}