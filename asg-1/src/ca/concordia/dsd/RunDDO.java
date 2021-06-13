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

public class RunDDO {

    private static ICenterServer stub;
    private static CenterServerImpl server;

    public static void main(String[] args) {
        try {
            // create server
            server = new CenterServerImpl(Constants.DDO_TAG);

            try{
                //add dummy teachers
                TeacherRecord tR1 = new TeacherRecord(null,"dino","moria","Cartier","0123456789","maths","DDO");
                TeacherRecord tR2 = new TeacherRecord(null,"fued","taker","Dorion","0123456789","algo","DDO");
                System.out.println("> "+server.createTRecord(tR1));
                System.out.println("> "+server.createTRecord(tR2));
                //add dummy students
                ArrayList<String> courses = new ArrayList<>();
                courses.add("maths");
                courses.add("algo");
                StudentRecord sR1 = new StudentRecord(null,"tina","frank",courses,"active","01062021");
                //StudentRecord sR2 = new StudentRecord(null,"mina","trank",courses,"inactive","02062021");
                System.out.println("> "+server.createSRecord(sR1));
                //System.out.println("> "+server.createSRecord(sR2));
                //get count
                System.out.println(server.getRecordCounts());

            }catch (Throwable t){
                System.out.println(t.getMessage());
            }



            // create remote objects
            stub = (ICenterServer) UnicastRemoteObject.exportObject(server, Constants.DDO_SERVER_PORT);
            // registry binds the stub of remote object
            Registry registry = LocateRegistry.createRegistry(Constants.DDO_SERVER_PORT);
            registry.bind(Constants.DDO_TAG, stub);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
