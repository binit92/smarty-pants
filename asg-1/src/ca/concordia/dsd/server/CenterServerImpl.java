package ca.concordia.dsd.server;

import ca.concordia.dsd.database.Records;
import ca.concordia.dsd.database.StudentRecord;
import ca.concordia.dsd.database.TeacherRecord;
import ca.concordia.dsd.util.Constants;
import ca.concordia.dsd.util.LogUtil;

import java.io.IOException;
import java.rmi.RemoteException;
import java.util.*;

public class CenterServerImpl implements ICenterServer {
    private LogUtil logUtil;
    public UDPThread udpThread;
    public String IPaddress;

    public HashMap<String, List<Records>> recordsMap;
    private static int studentCount = 0;
    private static int teacherCount = 0;
    private String recordsCount;
    private String serverName;

    public CenterServerImpl(String serverName) {
        this.serverName = serverName;
        logUtil = new LogUtil(serverName);
        recordsMap = new HashMap<>();
        udpThread = new UDPThread(serverName, this);
        udpThread.start();
        setIPAddress(serverName);
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

    @Override
    public String createTRecord(TeacherRecord teacherRecord) throws RemoteException {
        String teacherid = "TR" + (++teacherCount);
        teacherRecord.setTeacherId(teacherid);
        teacherRecord.setUniqueId(teacherid);

        String key = teacherRecord.getLastName().substring(0, 1);
        // adding the teacher record to HashMap
        String message = addRecordToHashMap(key, teacherRecord, null);

        //System.out.println(recordsMap);

        System.out.println("teacher is added " + teacherRecord.getFirstName() + "with this key" + key);
        logUtil.log("Teacher record created " + teacherid);
        return teacherid;
    }

    // adding the records into HashMap
    private String addRecordToHashMap(String key, TeacherRecord teacher, StudentRecord student) {

        String message = "Error";
        if (teacher != null) {
            List<Records> recordList = recordsMap.get(key);
            if (recordList != null) {
                recordList.add(teacher);
            } else {
                List<Records> records = new ArrayList<Records>();
                records.add(teacher);
                recordList = records;
            }
            recordsMap.put(key, recordList);
            message = "success";
        }

        if (student != null) {
            List<Records> recordList = recordsMap.get(key);
            if (recordList != null) {
                recordList.add(student);
            } else {
                List<Records> records = new ArrayList<Records>();
                records.add(student);
                recordList = records;
            }
            recordsMap.put(key, recordList);
            message = "success";
        }

        return message;
    }


    @Override
    public String createSRecord(StudentRecord studentRecord) throws RemoteException {
        String studentid = "SR" + (studentCount + 1);
        studentRecord.setUniqueId(studentid);
        studentRecord.setStudentID(studentid);

        String key = studentRecord.getLastName().substring(0, 1);
        // adding the student record to HashMap
        String message = addRecordToHashMap(key, null, studentRecord);

        //System.out.println(recordsMap);

        System.out.println("Student is added " + studentRecord + "with this key" + key);
        logUtil.log("Student record created " + studentid);
        return studentid;
    }

    private int getCurrServerCnt(){
        int count = 0;
        for (Map.Entry<String, List<Records>> entry : this.recordsMap.entrySet()) {
            List<Records> list = entry.getValue();
            count+=list.size();
            //System.out.println(entry.getKey()+" "+list.size());
        }
        return count;
    }

    @Override
    public String getRecordCounts() {
        String recordCount = null;
        UDPProviderThread[] req = new UDPProviderThread[2];
        int counter = 0;
        ArrayList<String> locList = new ArrayList<>();
        locList.add("MTL");
        locList.add("LVL");
        locList.add("DDO");
        for (String loc : locList) {
            if (loc== this.serverName) {
                recordCount = loc+","+getCurrServerCnt();
            } else {
                try {
                    req[counter] = new UDPProviderThread(this);
                } catch (IOException e) {
                    logUtil.log(e.getMessage());
                }
                req[counter].start();
                counter++;
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
        return recordCount;
    }

    @Override
    public String editRecord(String id, String key, String val) throws RemoteException {
        String type = id.substring(0, 2);

        if (type.equals("TR")) {
            return editTRRecord(id, key, val);
        }

        else if (type.equals("SR")) {
            return editSRRecord(id, key, val);
        }

        logUtil.log("Record edit successful");

        return "Operation not performed!";
    }

    // Editing students records
    private String editSRRecord(String recordID, String fieldname, String newvalue) {

        //System.out.println(recordsMap);

        for (Map.Entry<String, List<Records>> value : recordsMap.entrySet()) {

            List<Records> mylist = value.getValue();
            Optional<Records> record = mylist.stream().filter(x -> x.getUniqueId().equals(recordID)).findFirst();
            if (record.isPresent()) {
                if (record.isPresent() && fieldname.equals("Status")) {
                    ((StudentRecord) record.get()).setStatus(newvalue);
                    logUtil.log("Updated the records\t" + serverName);
                    return "Updated record with status :: "+newvalue;
                    // ((Student) record.get()).setStatus(null);
                } else if (record.isPresent() && fieldname.equals("StatusDate")) {
                    ((StudentRecord) record.get()).setStatusDate(newvalue);
                    logUtil.log("Updated the records\t" + serverName);
                    return "Updated record with status date :: "+newvalue;
                }
            }
        }
        return "Record with "+recordID+"not found!";
    }

    // Editing Teacher records
    private String editTRRecord(String recordID, String fieldname, String newvalue) {
        for (Map.Entry<String, List<Records>> val : recordsMap.entrySet()) {

            List<Records> mylist = val.getValue();
            Optional<Records> record = mylist.stream().filter(x -> x.getUniqueId().equals(recordID)).findFirst();

            //System.out.println(record);
            if (record.isPresent()) {
                if (record.isPresent() && fieldname.equals("Phone")) {
                    ((TeacherRecord) record.get()).setPhone(newvalue);
                    logUtil.log("Updated the records\t" + serverName);
                    return "Updated record with Phone :: "+newvalue;
                }

                else if (record.isPresent() && fieldname.equals("Address")) {
                    ((TeacherRecord) record.get()).setAddress(newvalue);
                    logUtil.log("Updated the records\t" + serverName);
                    return "Updated record with address :: "+newvalue;
                }

                else if (record.isPresent() && fieldname.equals("Location")) {
                    ((TeacherRecord) record.get()).setLocation(newvalue);
                    logUtil.log("Updated the records\t" + serverName);
                    return "Updated record with location :: "+newvalue;
                }
            }
        }
        return "Record with "+recordID+" not found";
    }

    @Override
    public String editCourses(String id, String key, ArrayList<String> values) throws RemoteException {
        for (Map.Entry<String, List<Records>> value : recordsMap.entrySet()) {

            List<Records> mylist = value.getValue();
            Optional<Records> record = mylist.stream().filter(x -> x.getUniqueId().equals(id)).findFirst();
            if (record.isPresent() && key.equals("CoursesRegistered")) {
                ((StudentRecord) record.get()).setCoursesRegistered(values);
                logUtil.log("Updated the records\t" + serverName);
            }
        }
        //System.out.println(recordsMap);
        return null;
    }

}