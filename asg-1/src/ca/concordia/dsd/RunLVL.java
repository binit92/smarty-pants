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

public class RunLVL {

    private static ICenterServer stub;
    private static CenterServerImpl server;

    public static void main(String[] args) {
        try {
            // create server
            server = new CenterServerImpl(Constants.LVL_TAG);

            try{
                //"LVL0000" is a test manager id
                //add dummy teachers
                TeacherRecord tR1 = new TeacherRecord(null,"ted","min","Cartier","0123456789","maths","LVL");
                TeacherRecord tR2 = new TeacherRecord(null,"ned","stark","Dorion","0123456789","algo","LVL");
                System.out.println("> "+server.createTRecord("LVL0000",tR1));
                System.out.println("> "+server.createTRecord("LVL0000",tR2));
                //add dummy students
                ArrayList<String> courses = new ArrayList<>();
                courses.add("maths");
                courses.add("algo");
                StudentRecord sR1 = new StudentRecord(null,"bnna","lrank",courses,"active","01062021");
                StudentRecord sR2 = new StudentRecord(null,"bnne","srank",courses,"inactive","02062021");
                System.out.println("> "+server.createSRecord("LVL0000",sR1));
                System.out.println("> "+server.createSRecord("LVL0000",sR2));
                //get count
                System.out.println(server.getRecordCounts("LVL0000"));

            }catch (Throwable t){
                System.out.println(t.getMessage());
            }


            // create remote objects
            stub = (ICenterServer) UnicastRemoteObject.exportObject(server, Constants.LVL_SERVER_PORT);
            // registry binds the stub of remote object
            Registry registry = LocateRegistry.createRegistry(Constants.LVL_SERVER_PORT);
            registry.bind(Constants.LVL_TAG, stub);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
