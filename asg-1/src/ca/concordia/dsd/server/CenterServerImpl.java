package ca.concordia.dsd.server;

import ca.concordia.dsd.database.Database;
import ca.concordia.dsd.database.StudentRecord;
import ca.concordia.dsd.database.TeacherRecord;
import ca.concordia.dsd.util.LogUtil;

import java.rmi.RemoteException;
import java.util.ArrayList;

public class CenterServerImpl implements ICenterServer {

    private LogUtil logUtil;
    private Database database;

    public CenterServerImpl(String serverName) {
        logUtil = new LogUtil(serverName);
        database = new Database();
        // set udp etc

    }

    @Override
    public String createTRecord(TeacherRecord teacherRecord) throws RemoteException {
        return null;
    }

    @Override
    public String createSRecord(StudentRecord studentRecord) throws RemoteException {
        return null;
    }

    @Override
    public String getRecordCounts() {
        return null;
    }

    @Override
    public String editRecord(String id, String key, String val) throws RemoteException {
        return null;
    }

    @Override
    public String editCourses(String id, String key, ArrayList<String> values) throws RemoteException {
        return null;
    }

}