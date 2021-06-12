package ca.concordia.dsd.server;

import java.rmi.Remote;

public interface CenterServer extends Remote {

    public String createTRecord();
    public String createSRecord();
    public String getRecordCounts();
    public String editRecord();

}
