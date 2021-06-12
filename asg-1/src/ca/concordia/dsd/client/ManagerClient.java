package ca.concordia.dsd.client;

import ca.concordia.dsd.server.ICenterServer;
import ca.concordia.dsd.util.Constants;
import ca.concordia.dsd.util.LogUtil;

import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.util.Scanner;
import java.util.regex.Pattern;

public class ManagerClient implements Constants {

    private LogUtil logUtil;
    private static ICenterServer server;

    public ManagerClient(){
        logUtil = new LogUtil("manager");
        logUtil.log("Initiating ManagerClient ");
    }

    public void start() {
        logUtil.log("Starting MangerClient");
        try {
            String managerId = getManagerID();
        }catch (Exception e) {
            logUtil.log(e.getMessage());
        }
        logUtil.log("Ending ManagerClient");
    }

    public String getManagerID()  {
        System.out.println("getManagerID");
        while(true) {
            logUtil.log("Enter the manager id ?:");
            Scanner keyboard = new Scanner(System.in);
            String input = keyboard.nextLine();
            if (isValidManagerId(input)){
                return input;
            }else{
                logUtil.log("Not a valid manager ID of format: <MTL1111>  Try Again !!!");
            }
        }
    }

    public boolean isValidManagerId(String managerId){
        if (managerId != null && managerId.length() == 7) {
            if (Pattern.compile("[0-9]*").matcher(managerId.substring(3,6)).matches()){
                String firstThree = managerId.substring(0,3);
                if (firstThree.equalsIgnoreCase(MTL_TAG)){
                    createRmiConnection(MTL_TAG,MTL_SERVER_HOST,MTL_SERVER_PORT);
                    return true;
                }else if (firstThree.equalsIgnoreCase(LVL_TAG)){
                    createRmiConnection(LVL_TAG,LVL_SERVER_HOST,LVL_SERVER_PORT);
                    return true;
                }else if(firstThree.equalsIgnoreCase(DDO_TAG)) {
                    createRmiConnection(DDO_TAG,DDO_SERVER_HOST,DDO_SERVER_PORT);
                    return true;
                }
            }
        }
        return false;
    }

    private void createRmiConnection (String tag,String name, int port) {
        try {
            logUtil.log(" Initiating RMI connection to " + name + " : " + port);
            Registry registry = LocateRegistry.getRegistry(name,port);
            server = (ICenterServer) registry.lookup(tag);
            logUtil.log(" RMI connection established to " + name + " : " + port);
        }catch (Exception e){
            e.printStackTrace();
        }
    }
}
