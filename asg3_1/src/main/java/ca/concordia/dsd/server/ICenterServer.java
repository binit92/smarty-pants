package ca.concordia.dsd.server;

import javax.jws.WebMethod;
import javax.jws.WebService;

@WebService
public interface ICenterServer {

	@WebMethod
    public String createTRecord(String id, String fName, String lName, String address, String phone, String specialization, String location);

	@WebMethod
    public String createSRecord(String id, String fName, String lName, String courses, boolean status, String statusDate);

	@WebMethod
    public String getRecordCounts(String manager) ;

	@WebMethod
    public String editRecord(String manager, String id, String key, String val)  ;

	@WebMethod
    public String transferRecord(String id, String recordId, String remoteCenterServerName) ;


}
