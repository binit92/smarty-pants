package ca.concordia.dsd;

import ca.concordia.dsd.database.StudentRecord;
import ca.concordia.dsd.database.TeacherRecord;
import ca.concordia.dsd.server.CenterServerImpl;
import ca.concordia.dsd.server.ICenterServer;
import ca.concordia.dsd.util.Constants;

import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.util.ArrayList;

public class RunMTL {

    private static ICenterServer stub;
    private static CenterServerImpl server;

    public static void main(String[] args) {
        try {

            server = new CenterServerImpl(Constants.MTL_TAG);
            try{
                // "MTL0000" is a test manager id
                //add dummy teachers
                TeacherRecord tR1 = new TeacherRecord(null,"ched","faker","Cartier","0123456789","maths","MTL");
                TeacherRecord tR2 = new TeacherRecord(null,"fed","taker","Dorion","0123456789","algo","MTL");
                System.out.println("> "+server.createTRecord("MTL0000",tR1));
                System.out.println("> "+server.createTRecord("MTL0000",tR2));
                //add dummy students
                ArrayList<String> courses = new ArrayList<>();
                courses.add("maths");
                courses.add("algo");
                StudentRecord sR1 = new StudentRecord(null,"anna","frank",courses,"active","01062021");
                StudentRecord sR2 = new StudentRecord(null,"anne","trank",courses,"inactive","02062021");
                System.out.println("> "+server.createSRecord("MTL0000",sR1));
                System.out.println("> "+server.createSRecord("MTL0000",sR2));
                //get count
                System.out.println(server.getRecordCounts("MTL0000"));

            }catch (Throwable t){
                System.out.println(t.getMessage());
            }

            // export remote object
            stub = (ICenterServer) UnicastRemoteObject.exportObject(server, Constants.MTL_SERVER_PORT);

            Registry registry = LocateRegistry.createRegistry(Constants.MTL_SERVER_PORT);
            registry.bind(Constants.MTL_TAG, stub);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
