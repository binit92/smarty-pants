package ca.concordia.dsd;

import ca.concordia.dsd.server.CenterServerImpl;
import ca.concordia.dsd.server.ICenterServer;
import ca.concordia.dsd.util.Constants;

import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;

public class RunLVL {

    private static ICenterServer stub;
    private static CenterServerImpl server;

    public static void main(String[] args) {
        try {
            // create server
            server = new CenterServerImpl(Constants.DDO_TAG);
            // create remote objects
            stub = (ICenterServer) UnicastRemoteObject.exportObject(server,Constants.DDO_SERVER_PORT);
            // registry binds the stub of remote object
            Registry registry = LocateRegistry.createRegistry(Constants.DDO_SERVER_PORT);
            registry.bind(Constants.DDO_TAG,stub);
        }catch (Exception e){
            e.printStackTrace();
        }
    }
}
