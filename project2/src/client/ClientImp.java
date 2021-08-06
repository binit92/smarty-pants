package client;

import java.util.logging.Level;

import org.omg.CORBA.ORB;
import org.omg.CosNaming.NamingContextExt;
import org.omg.CosNaming.NamingContextExtHelper;

import idlmodule.*;
import conf.LogManager;
import conf.*;

public class ClientImp {
	corba serverLoc = null;
	static NamingContextExt ncRef = null;

	LogManager logManager;
	ClientImp(String[] args, ServerCenterLocation location, String ManagerID) {
		try {
			this.logManager = DcmsClient.logManager;
			ORB orb = ORB.init(args, null);
			org.omg.CORBA.Object objRef = orb.resolve_initial_references("NameService");
			ncRef = NamingContextExtHelper.narrow(objRef);
			if ((location == ServerCenterLocation.MTL)|| (location == ServerCenterLocation.LVL)
					|| (location == ServerCenterLocation.DDO)){
				serverLoc = corbaHelper.narrow(ncRef.resolve_str("FE"));
			}
			
		} catch (Exception e) {
			System.out.println("ERROR : " + e);
			e.printStackTrace(System.out);
		}
	}
	public String createTRecord(String managerID, String teacherField) {
		DcmsClient.logManager.logger.log(Level.INFO, "Initiating T record object creation request");
		String result = "";
		String teacherID = "";
		teacherID = serverLoc.createTRecord(managerID, teacherField);
		if (teacherID != null)
			result = "Teacher record is created and assigned with " + teacherID;
		else
			result = "Teacher record is not created";
		logManager.logger.log(Level.INFO, result);
		return result;
	}

	public String createSRecord(String managerID, String studentFields) {
		logManager.logger.log(Level.INFO, "Initiating S record object creation request");
		String result = "";
		String studentID = "";
		studentID = serverLoc.createSRecord(managerID, studentFields);
		if (studentID != null)
			result = "student record is created and assigned with " + studentID;
		else
			result = "student record is not created";
		logManager.logger.log(Level.INFO, result);
		return result;
	}

	public String getRecordCounts(String managerID) {
		String count = "";
		logManager.logger.log(Level.INFO, "Initiating record count request");
		count = serverLoc.getRecordCount(managerID);
		logManager.logger.log(Level.INFO, "received....count as follows");
		logManager.logger.log(Level.INFO, count);
		return count;
	}


	public String transferRecord(String ManagerID, String recordID, String location) {
		String message = "";
		logManager.logger.log(Level.INFO, "Initiating the record transfer request");
		message = serverLoc.transferRecord(ManagerID, recordID, location);
		System.out.println(message);
		logManager.logger.log(Level.INFO, message);
		return message;
	}

	public String editRecord(String managerID, String recordID, String fieldname, String newvalue) {
		String message = "";
		logManager.logger.log(Level.INFO, managerID + "has Initiated the record edit request for " + recordID);
		message = serverLoc.editRecord(managerID, recordID, fieldname, newvalue);
		logManager.logger.log(Level.INFO, message);
		return message;
	}

	public String killServer(String location) {
		String message = "";
		logManager.logger.log(Level.INFO, "Initiating Server Kill Request at location "+location);
		message = serverLoc.killPrimaryServer(location);
		//System.out.println(message);
		logManager.logger.log(Level.INFO, message);
		return message;
	}
}