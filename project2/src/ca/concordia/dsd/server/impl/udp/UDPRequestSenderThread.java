package ca.concordia.dsd.server.impl.udp;

import ca.concordia.dsd.database.Record;
import ca.concordia.dsd.database.Student;
import ca.concordia.dsd.database.Teacher;
import ca.concordia.dsd.server.impl.CenterServer;
import ca.concordia.dsd.util.LocationEnum;
import ca.concordia.dsd.util.LogUtil;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.SocketException;
import java.util.List;
import java.util.Map;

public class UDPRequestSenderThread extends Thread {

    private final String TAG = "|" + UDPRequestSenderThread.class.getSimpleName() + "| ";
    private final DatagramPacket receivePacket;
    private final CenterServer server;
    private final LogUtil logUtil;
    private final Object mapLock;
    private DatagramSocket serverSocket;

    public UDPRequestSenderThread(DatagramPacket pkt, CenterServer serverImp, LogUtil logUtil) {
        receivePacket = pkt;
        server = serverImp;
        mapLock = new Object();
        this.logUtil = logUtil;
        try {
            serverSocket = new DatagramSocket();
        } catch (SocketException e) {
            logUtil.log(TAG + "error : " + e.getMessage());
        }
    }


    public void run() {
        byte[] responseData;
        try {
            String inputPkt = new String(receivePacket.getData()).trim();
            String[] pktSplit = new String[2];
            if (inputPkt.contains("#")) {
                pktSplit = inputPkt.split("#");
                inputPkt = pktSplit[0];
            }
            switch (inputPkt) {
                case "TRANSFER_RECORD":
                    logUtil.log(TAG + "transferring record  " + pktSplit[1]);
                    responseData = transferRecord(pktSplit[1]).getBytes();
                    serverSocket.send(new DatagramPacket(responseData, responseData.length, receivePacket.getAddress(),
                            receivePacket.getPort()));
                    break;
                case "GET_RECORD_COUNT":
                    responseData = Integer.toString(getRecCount()).getBytes();
                    logUtil.log(TAG + "record count : " + getRecCount());
                    serverSocket.send(new DatagramPacket(responseData, responseData.length, receivePacket.getAddress(),
                            receivePacket.getPort()));
                    break;
                default:
                   logUtil.log(TAG + "request type is not identified");
            }

        } catch (Exception e) {
        }
    }

    // TODO : check for concurrency
    private synchronized String transferRecord(String recordToBeAdded) {
        String[] temp = recordToBeAdded.split(",");
        String managerID = temp[0];
        String recordID = temp[1];
        if (recordID.contains("TR")) {
            String firstName = temp[2];
            String lastName = temp[3];
            String address = temp[4];
            String phone = temp[5];
            String specialization = temp[6];
            String location = temp[7];
            String key = lastName.substring(0, 1);
            Teacher teacherObj = new Teacher(managerID, recordID, firstName, lastName, address, phone, specialization,
                    location);
            String message;
            List<Record> data;
            synchronized (mapLock) {
                message = server.addRecordToHashMap(key, teacherObj, null);
                data = server.recordsMap.get(key);
            }
            return message + " " + data;
        } else {
            String firstName = temp[2];
            String lastName = temp[3];
            String CoursesRegistered = temp[4];
            List<String> courseList = server.putCoursesinList(CoursesRegistered);
            String status = temp[3];
            String statusDate = temp[5];
            Student studentObj = new Student(managerID, recordID, firstName, lastName, courseList, status, statusDate);
            String key = lastName.substring(0, 1);

            String message;
            List<Record> data;
            synchronized (mapLock) {
                message = server.addRecordToHashMap(key, null, studentObj);
                data = server.recordsMap.get(key);
            }
            return message + " " + data;
        }
    }

    // TODO : check for concurrency
    private synchronized int getRecCount() {
        int count = 0;
        synchronized (mapLock) {
            for (Map.Entry<String, List<Record>> entry : server.recordsMap.entrySet()) {
                List<Record> list = entry.getValue();
                count += list.size();
            }
        }
        return count;
    }
}
