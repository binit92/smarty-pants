package ca.concordia.dsd.server.main;

import ca.concordia.dsd.arch.corba;
import ca.concordia.dsd.arch.corbaHelper;
import ca.concordia.dsd.util.Constants;
import ca.concordia.dsd.conf.ServerCenterLocation;
import ca.concordia.dsd.server.frontend.DcmsServerFE;
import org.omg.CORBA.ORB;
import org.omg.CosNaming.NameComponent;
import org.omg.CosNaming.NamingContextExt;
import org.omg.CosNaming.NamingContextExtHelper;
import org.omg.PortableServer.POA;
import org.omg.PortableServer.POAHelper;

import java.io.File;
import java.io.IOException;


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
        new File(Constants.LOG_DIR + File.separator + "ServerFE").mkdir();
        new File(Constants.LOG_DIR + File.separator + "ReplicasResponse").mkdir();

        new File(Constants.LOG_DIR + File.separator + "PRIMARY_SERVER").mkdir();
        new File(Constants.LOG_DIR + File.separator + "PRIMARY_SERVER" + "\\" + ServerCenterLocation.MTL).mkdir();
        new File(Constants.LOG_DIR + File.separator + "PRIMARY_SERVER" + "\\" + ServerCenterLocation.LVL).mkdir();
        new File(Constants.LOG_DIR + File.separator + "PRIMARY_SERVER" + "\\" + ServerCenterLocation.DDO).mkdir();

        new File(Constants.LOG_DIR + File.separator + "REPLICA1_SERVER").mkdir();
        new File(Constants.LOG_DIR + File.separator + "REPLICA1_SERVER" + "\\" + ServerCenterLocation.MTL).mkdir();
        new File(Constants.LOG_DIR + File.separator + "REPLICA1_SERVER" + "\\" + ServerCenterLocation.LVL).mkdir();
        new File(Constants.LOG_DIR + File.separator + "REPLICA1_SERVER" + "\\" + ServerCenterLocation.DDO).mkdir();

        new File(Constants.LOG_DIR + File.separator + "REPLICA2_SERVER").mkdir();
        new File(Constants.LOG_DIR + File.separator + "REPLICA2_SERVER" + "\\" + ServerCenterLocation.MTL).mkdir();
        new File(Constants.LOG_DIR + File.separator + "REPLICA2_SERVER" + "\\" + ServerCenterLocation.LVL).mkdir();
        new File(Constants.LOG_DIR + File.separator + "REPLICA2_SERVER" + "\\" + ServerCenterLocation.DDO).mkdir();

    }


    public static void main(String[] args) {
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


            NameComponent[] fePath = ncRef.to_name("FE");


            ncRef.rebind(fePath, fehref);
            System.out.println("DCMS Servers ready and waiting ...");
            orb.run();
        } catch (Exception e) {
            System.err.println("Exception in Server Main:: " + e);
            e.printStackTrace(System.out);
        }

    }
}