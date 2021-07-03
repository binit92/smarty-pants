package ca.concordia.dsd;

import ca.concordia.dsd.arch.corba;
import ca.concordia.dsd.arch.corbaHelper;

import ca.concordia.dsd.server.CenterServerImpl;
import ca.concordia.dsd.util.Constants;
import org.omg.CORBA.ORB;
import org.omg.CosNaming.NameComponent;
import org.omg.CosNaming.NamingContextExt;
import org.omg.CosNaming.NamingContextExtHelper;
import org.omg.PortableServer.POA;
import org.omg.PortableServer.POAHelper;

public class RunLVL {


    public static void main(String[] args) {
        try {

            String localargs[] = new String[4];
            localargs[0] = "-ORBInitialPort";
            localargs[1] = Integer.toString(Constants.LVL_SERVER_PORT);
            localargs[2] = "-ORBInitialHost ";
            localargs[3] = Constants.LVL_SERVER_HOST;

            // Initiate local ORB object
            ORB orb = ORB.init(localargs, null);

            // Get reference to RootPOA and get POAManager
            POA rootPOA = POAHelper.narrow(orb.resolve_initial_references("RootPOA"));
            rootPOA.the_POAManager().activate();

            // Create servant and register it with the ORB
            CenterServerImpl servant = new CenterServerImpl(Constants.LVL_TAG,Constants.LVL_SERVER_PORT,Constants.LVL_UDP_PORT);
            servant.setORB(orb);

            // Get object reference from the servant
            org.omg.CORBA.Object ref = rootPOA.servant_to_reference(servant);
            corba dcmsServer = corbaHelper.narrow(ref);

            // Get the root Naming Context
            org.omg.CORBA.Object objRef = orb.resolve_initial_references("NameService");
            NamingContextExt namingContextRef = NamingContextExtHelper.narrow(objRef);

            // Bind the object reference to the Naming Context
            NameComponent path[] = namingContextRef.to_name(Constants.LVL_TAG);
            namingContextRef.rebind(path, dcmsServer);

            // Run the server
            //dcmsServer.startUDPServer();
            System.out.println("Server " + Constants.LVL_TAG + " is running ...");
            orb.run();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
