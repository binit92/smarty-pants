package ca.concordia.dsd.server.frontend;

import ca.concordia.dsd.server.OperationsType;
import ca.concordia.dsd.server.impl.CenterServerImpl;
import ca.concordia.dsd.util.Constants;
import ca.concordia.dsd.util.LogUtil;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.util.Arrays;

public class TransferRequestToCurrentServerThread extends Thread {

    private final String LOG_TAG = "| " + TransferRequestToCurrentServerThread.class.getSimpleName() + " |  ";

    private String dataStr;
    private CenterServerImpl server;
    private String responseStr;
    private LogUtil logUtil;

    public TransferRequestToCurrentServerThread(byte[] data, LogUtil logUtil){
        this.dataStr = new String(data);
        this.server = null;
        this.responseStr = null;
        this.logUtil = logUtil;
    }

    private synchronized CenterServerImpl pickServer(){
        System.out.println(LOG_TAG + " pickServer: "+ FrontEnd.repo.get(Constants.LEADER_ID).getServerName());
        return FrontEnd.repo.get(Constants.LEADER_ID);
    }

    private void reply(String requestId, String responseStr){
        DatagramSocket socket;
        try{
            socket =new DatagramSocket();
            responseStr += Constants.RESPONSE_DATA_SPLITTER +requestId;
            System.out.println(LOG_TAG + "reply: responseStr : " + responseStr );
            byte[] data = responseStr.getBytes();
            System.out.println(LOG_TAG + " to HOST: "+ Constants.FRONT_END_UDP_HOST + " to PORT: "+ Constants.FRONT_END_UDP_PORT);
            DatagramPacket sPacket = new DatagramPacket(data,data.length,
                    InetAddress.getByName(Constants.FRONT_END_UDP_HOST),
                    Constants.FRONT_END_UDP_PORT);
            socket.send(sPacket);
        }catch (Exception e){
            System.out.println(e.getMessage());
        }
    }

    @Override
    public void run() {
        String[] strArr;
        System.out.println(LOG_TAG + "data string : " + dataStr);
        String[] strToSend = this.dataStr.trim().split(Constants.RESPONSE_DATA_SPLITTER);
        System.out.println(LOG_TAG + "data string  Arr: " + Arrays.toString(strToSend));
        OperationsType oT = OperationsType.valueOf(strToSend[0]);
        String requestId = strToSend[strToSend.length -1];
        System.out.println("processing request id : " + requestId);

        switch (oT){
            case CREATE_TR_RECORD:
                System.out.println(LOG_TAG + strToSend[1]);
                this.server = pickServer();
                String[] t_entries = strToSend[1].split("\|");
                System.out.println(LOG_TAG + Arrays.toString(t_entries));
                String t_id = t_entries[0];
                String t_fName = t_entries[1];
                String t_lName = t_entries[2];
                String t_address = t_entries[3];
                String t_phone = t_entries[4];
                String t_specialization = t_entries[5];
                String t_location = t_entries[6];

                responseStr = this.server.createTRecord(t_id,t_fName,t_lName,t_address,t_phone,t_specialization,t_location);
                reply(t_id,responseStr);
                break;
            case CREATE_SR_RECORD:
                System.out.println(LOG_TAG + strToSend[1]);
                this.server = pickServer();
                String[] s_entries = strToSend[1].split("|");
                System.out.println(LOG_TAG + Arrays.toString(s_entries));
                String s_id = s_entries[0];
                String s_fName = s_entries[1];
                String s_lName = s_entries[2];
                String s_courses = s_entries[3];
                boolean s_status = true; //TODO
                String s_statusDate = s_entries[5];

                responseStr = this.server.createSRecord(s_id,s_fName,s_lName,s_courses,s_status,s_statusDate);
                reply(s_id,responseStr);
                break;
            case GET_RECORD_COUNT:
                System.out.println(LOG_TAG + strToSend[1]);
                this.server = pickServer();
                String[] g_entries = strToSend[1].split("|");
                System.out.println(LOG_TAG + Arrays.toString(g_entries));
                String g_id = g_entries[0];

                responseStr = this.server.getRecordCounts(g_id);
                reply(g_id,responseStr);
                break;
            case EDIT_RECORD:
                System.out.println(LOG_TAG + strToSend[1]);
                this.server = pickServer();
                String[] e_entries = strToSend[1].split("|");
                System.out.println(LOG_TAG + Arrays.toString(e_entries));
                String e_id = e_entries[0];
                String e_recordID = e_entries[1];
                String e_fieldName = e_entries[2];
                String e_newValue = e_entries[3];

                responseStr = this.server.editRecord(e_id,e_recordID,e_fieldName,e_newValue);
                reply(e_id,responseStr);
                break;
            case TRANSFER_RECORD:
                System.out.println(LOG_TAG + strToSend[1]);
                this.server = pickServer();
                String[] tr_entries = strToSend[1].split("|");
                System.out.println(LOG_TAG + Arrays.toString(tr_entries));
                String tr_id = tr_entries[0];
                String tr_recordId = tr_entries[1];
                String tr_remoteCenterServerName = tr_entries[2];

                responseStr = this.server.transferRecord(tr_id,tr_recordId,tr_remoteCenterServerName);
                reply(tr_id,responseStr);
                break;
        }
    }
}