package ca.concordia.dsd.server;

import ca.concordia.dsd.database.StudentRecord;
import ca.concordia.dsd.database.TeacherRecord;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.ArrayList;

public interface ICenterServer extends Remote {

    public String createTRecord(TeacherRecord teacherRecord) throws RemoteException;
    public String createSRecord(StudentRecord studentRecord) throws RemoteException;
    public String getRecordCounts() throws RemoteException;
    public String editRecord(String id, String key, String val) throws RemoteException;
    public String editCourses(String id, String key, ArrayList<String> values) throws RemoteException;

}
