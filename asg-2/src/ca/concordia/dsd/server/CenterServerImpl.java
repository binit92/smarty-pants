package ca.concordia.dsd.server;

import ca.concordia.dsd.arch.corbaPOA;
import ca.concordia.dsd.database.Records;
import ca.concordia.dsd.database.StudentRecord;
import ca.concordia.dsd.database.TeacherRecord;
import ca.concordia.dsd.util.Constants;
import ca.concordia.dsd.util.LogUtil;
import org.omg.CORBA.ORB;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.util.*;

public class CenterServerImpl extends corbaPOA  {
    private LogUtil logUtil;
    public UDPThread udpThread;
    public String IPaddress;

    int port = 0;
    int udpPort = 0;

    private ORB ddoorb;

    public HashMap<String, List<Records>> recordsMap;
    private static int studentCount = 10001;
    private static int teacherCount = 10001;
    private String recordsCount;
    private final String serverName;

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
                    int udpPort = Constants.MTL_UDP_PORT;
                    if(loc.equalsIgnoreCase(Constants.LVL_TAG)){
                        ipAdd = Constants.LVL_SERVER_HOST;
                        udpPort = Constants.LVL_UDP_PORT;
                    }else if(loc.equalsIgnoreCase(Constants.DDO_TAG)){
                        ipAdd = Constants.DDO_SERVER_HOST;
                        udpPort = Constants.DDO_UDP_PORT;
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
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            recordCount += " , " + request.getRemoteRecordCount().trim();
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
        return "";
        //return false;
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

    /* TODO
    public String editCourses(String manager, String id, String key, ArrayList<String> values) throws RemoteException {
        for (Map.Entry<String, List<Records>> value : recordsMap.entrySet()) {

            List<Records> list = value.getValue();
            Optional<Records> record = list.stream().filter(x -> x.getUniqueId().equals(id)).findFirst();
            if (record.isPresent() && key.equalsIgnoreCase("courses")) {
                ((StudentRecord) record.get()).setCoursesRegistered(values);
                logUtil.log("Records update for : " + serverName);
            }
        }
        return null;
    }*/

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