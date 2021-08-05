package ca.concordia.dsd;

import ca.concordia.dsd.arch.corba;
import ca.concordia.dsd.arch.corbaHelper;
import ca.concordia.dsd.server.frontend.FrontEnd;
import ca.concordia.dsd.util.Constants;
import org.omg.CORBA.ORB;
import org.omg.CosNaming.NameComponent;
import org.omg.CosNaming.NamingContextExt;
import org.omg.CosNaming.NamingContextExtHelper;
import org.omg.PortableServer.POA;
import org.omg.PortableServer.POAHelper;

import java.util.Arrays;

public class RunMTL {

    // open CORBA port programmatically ..
    static{
        try{
            //String command = "orbd -ORBInitialPort " + Constants.MTL_SERVER_PORT + " -ORBInitialHost " + Constants.MTL_SERVER_HOST;
            String command = "orbd -ORBInitialPort " + Constants.ORB_INITIAL_PORT + " -ORBInitialHost " + Constants.ORB_INITIAL_HOST;
            Runtime.getRuntime().exec(command);
        }catch(Exception e){
            System.out.println(e.getMessage());
            System.out.println("Stopping the server ");
            System.exit(1);
        }
        System.out.println("Corba service started successfully ");
    }

    public static void main(String[] args) {
        try {
            // Setting the port and host programmatically here ..
            String localargs[] = new String[4];
            localargs[0] = "-ORBInitialPort";
            //localargs[1] = Integer.toString(Constants.MTL_SERVER_PORT);
            localargs[1] = Integer.toString(Constants.ORB_INITIAL_PORT);
            localargs[2] = "-ORBInitialHost ";
            //localargs[3] = Constants.MTL_SERVER_HOST;
            localargs[3] = Constants.ORB_INITIAL_HOST;

            System.out.println(Arrays.toString(localargs));

            // Initiate local ORB object
            ORB orb = ORB.init(localargs, null);

            // Get reference to RootPOA and get POAManager
            POA rootPOA = POAHelper.narrow(orb.resolve_initial_references("RootPOA"));
            rootPOA.the_POAManager().activate();

            // Create servant and register it with the ORB
            //CenterServerImpl servant = new CenterServerImpl(Constants.MTL_TAG,Constants.MTL_SERVER_PORT,Constants.MTL_UDP_PORT_LEADER);
            //servant.setORB(orb);
            FrontEnd servant = new FrontEnd(Constants.MTL_TAG);
            //servant.init();

            // Get object reference from the servant
            org.omg.CORBA.Object ref = rootPOA.servant_to_reference(servant);
            corba dcmsServer = corbaHelper.narrow(ref);

            // Get the root Naming Context
            org.omg.CORBA.Object objRef = orb.resolve_initial_references("NameService");
            NamingContextExt namingContextRef = NamingContextExtHelper.narrow(objRef);

            // Bind the object reference to the Naming Context
            NameComponent path[] = namingContextRef.to_name(Constants.MTL_TAG);
            namingContextRef.rebind(path, dcmsServer);

            // Run the server
            //dcmsServer.startUDPServer();
            System.out.println("Server " + Constants.MTL_TAG + " is running ...");
            orb.run();
            //servant.startUDPServer();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
