package ca.concordia.dsd.server;

import java.rmi.Remote;

public interface ITasks extends Remote {

    public String createTRecord();
    public String createSRecord();
    public String getRecordCounts();
    public String editRecord();

}
