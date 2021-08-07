package ca.concordia.dsd;

import ca.concordia.dsd.arch.corba;
import ca.concordia.dsd.arch.corbaHelper;
import ca.concordia.dsd.server.frontend.FrontEnd;
import ca.concordia.dsd.util.Constants;
import ca.concordia.dsd.util.LocationEnum;
import org.omg.CORBA.ORB;
import org.omg.CosNaming.NameComponent;
import org.omg.CosNaming.NamingContextExt;
import org.omg.CosNaming.NamingContextExtHelper;
import org.omg.PortableServer.POA;
import org.omg.PortableServer.POAHelper;

import java.io.File;
import java.io.IOException;


public class RunAllServers implements Constants {
    private static final String TAG = "|" + RunAllServers.class.getSimpleName() + "| ";
    static corba ref;

    public static void main(String[] args) {
        try {
            createLogDirectories();
            ORB orb = ORB.init(args, null);

            POA rootpoa = POAHelper
                    .narrow(orb.resolve_initial_references("RootPOA"));
            rootpoa.the_POAManager().activate();

            FrontEnd serverFE = new FrontEnd();

            org.omg.CORBA.Object feRef = rootpoa.servant_to_reference(serverFE);

            ref = corbaHelper.narrow(feRef);

            org.omg.CORBA.Object objRef = orb
                    .resolve_initial_references("NameService");
            NamingContextExt ncRef = NamingContextExtHelper.narrow(objRef);

            NameComponent[] fePath = ncRef.to_name("FE");

            ncRef.rebind(fePath, ref);
            System.out.println(TAG + "Started CORBA front-end and all servers in all datacenters");
            orb.run();
        } catch (Exception e) {
            System.out.println(TAG + e.getMessage());
        }
    }


    static {
        try {
            Runtime.getRuntime()
                    .exec("orbd -ORBInitialPort 5555 -ORBInitialHost localhost");
        } catch (IOException e) {
            e.printStackTrace();
            System.exit(1);
        }
    }

    private static void createLogDirectories() {
        new File(Constants.LOG_DIR + File.separator + FRONTEND_TAG).mkdir();
        new File(Constants.LOG_DIR + File.separator + REPLICA_RESPONSE_TAG).mkdir();

        new File(Constants.LOG_DIR + File.separator + LEADER_TAG).mkdir();
        new File(Constants.LOG_DIR + File.separator + LEADER_TAG + File.separator + LocationEnum.MTL).mkdir();
        new File(Constants.LOG_DIR + File.separator + LEADER_TAG + File.separator + LocationEnum.LVL).mkdir();
        new File(Constants.LOG_DIR + File.separator + LEADER_TAG + File.separator + LocationEnum.DDO).mkdir();

        new File(Constants.LOG_DIR + File.separator + REPLICA1_TAG).mkdir();
        new File(Constants.LOG_DIR + File.separator + REPLICA1_TAG + File.separator + LocationEnum.MTL).mkdir();
        new File(Constants.LOG_DIR + File.separator + REPLICA1_TAG + File.separator + LocationEnum.LVL).mkdir();
        new File(Constants.LOG_DIR + File.separator + REPLICA1_TAG + "\\" + LocationEnum.DDO).mkdir();

        new File(Constants.LOG_DIR + File.separator + REPLICA2_TAG).mkdir();
        new File(Constants.LOG_DIR + File.separator + REPLICA2_TAG + File.separator + LocationEnum.MTL).mkdir();
        new File(Constants.LOG_DIR + File.separator + REPLICA2_TAG + File.separator + LocationEnum.LVL).mkdir();
        new File(Constants.LOG_DIR + File.separator + REPLICA2_TAG + File.separator + LocationEnum.DDO).mkdir();
    }

}