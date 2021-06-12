package ca.concordia.dsd;

import ca.concordia.dsd.server.CenterServerImpl;
import ca.concordia.dsd.server.ICenterServer;
import ca.concordia.dsd.util.Constants;

import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;

public class RunMTL {

    private static ICenterServer stub;
    private static CenterServerImpl server;

    public static void main(String[] args) {
        try {
            // create server
            server = new CenterServerImpl(Constants.MTL_TAG);
            // create remote objects
            stub = (ICenterServer) UnicastRemoteObject.exportObject(server,Constants.MTL_SERVER_PORT);
            // registry binds the stub of remote object
            Registry registry = LocateRegistry.createRegistry(Constants.MTL_SERVER_PORT);
            registry.bind(Constants.MTL_TAG,stub);
        }catch (Exception e){
            e.printStackTrace();
        }
    }
}
