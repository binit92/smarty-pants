package server.main;

import idlmodule.*;
import org.omg.CosNaming.*;
import java.io.File;
import java.io.IOException;
import conf.Constants;
import org.omg.CORBA.*;
import org.omg.PortableServer.*;
import org.omg.PortableServer.POA;
import server.frontend.DcmsServerFE;
import conf.ServerCenterLocation;


public class DcmsServerMain {
	static corba fehref;

	static {
		try {
			Runtime.getRuntime()
					.exec("orbd -ORBInitialPort 1050 -ORBInitialHost localhost");
		} catch (IOException e) {
			e.printStackTrace();
			System.exit(1);
		}
		System.out.println("CORBA Service Started!");
	}

	private static void init() {
		new File(Constants.LOG_DIR + "ServerFE").mkdir();
		new File(Constants.LOG_DIR + "ReplicasResponse").mkdir();
		
		new File(Constants.LOG_DIR + "PRIMARY_SERVER").mkdir();
		new File(Constants.LOG_DIR + "PRIMARY_SERVER"+ "\\"+ServerCenterLocation.MTL.toString()).mkdir();
		new File(Constants.LOG_DIR + "PRIMARY_SERVER"+ "\\"+ServerCenterLocation.LVL.toString()).mkdir();
		new File(Constants.LOG_DIR + "PRIMARY_SERVER"+ "\\"+ServerCenterLocation.DDO.toString()).mkdir();

		new File(Constants.LOG_DIR + "REPLICA1_SERVER").mkdir();
		new File(Constants.LOG_DIR + "REPLICA1_SERVER"+ "\\"+ServerCenterLocation.MTL.toString()).mkdir();
		new File(Constants.LOG_DIR + "REPLICA1_SERVER"+ "\\"+ServerCenterLocation.LVL.toString()).mkdir();
		new File(Constants.LOG_DIR + "REPLICA1_SERVER"+ "\\"+ServerCenterLocation.DDO.toString()).mkdir();
		
		new File(Constants.LOG_DIR + "REPLICA2_SERVER").mkdir();
		new File(Constants.LOG_DIR + "REPLICA2_SERVER"+ "\\"+ServerCenterLocation.MTL.toString()).mkdir();
		new File(Constants.LOG_DIR + "REPLICA2_SERVER"+ "\\"+ServerCenterLocation.LVL.toString()).mkdir();
		new File(Constants.LOG_DIR + "REPLICA2_SERVER"+ "\\"+ServerCenterLocation.DDO.toString()).mkdir();
		
	}


	public static void main(String args[]) {
		try {

			init();

			ORB orb = ORB.init(args, null);
			

			POA rootpoa = POAHelper
					.narrow(orb.resolve_initial_references("RootPOA"));
			rootpoa.the_POAManager().activate();
			

			DcmsServerFE serverFE = new DcmsServerFE();


			org.omg.CORBA.Object feRef = rootpoa.servant_to_reference(serverFE);
			

			fehref = corbaHelper.narrow(feRef);


			org.omg.CORBA.Object objRef = orb
					.resolve_initial_references("NameService");
			NamingContextExt ncRef = NamingContextExtHelper.narrow(objRef);


			NameComponent fePath[] = ncRef.to_name("FE");


			ncRef.rebind(fePath, fehref);
			System.out.println("DCMS Servers ready and waiting ...");
			orb.run();
		}

		catch (Exception e) {
			System.err.println("Exception in Server Main:: " + e);
			e.printStackTrace(System.out);
		}

	}
}