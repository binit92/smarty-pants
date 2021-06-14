package ca.concordia.dsd.server;

import ca.concordia.dsd.database.StudentRecord;
import ca.concordia.dsd.database.TeacherRecord;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.ArrayList;

public interface ICenterServer extends Remote {

    public String createTRecord(String manager, TeacherRecord teacherRecord) throws RemoteException;

    public String createSRecord(String manager, StudentRecord studentRecord) throws RemoteException;

    public String getRecordCounts(String manager) throws RemoteException;

    public String editRecord(String manager, String id, String key, String val) throws RemoteException;

    public String editCourses(String manager, String id, String key, ArrayList<String> values) throws RemoteException;

}
