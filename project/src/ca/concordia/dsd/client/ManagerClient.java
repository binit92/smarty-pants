package ca.concordia.dsd.client;

import ca.concordia.dsd.arch.corba;
import ca.concordia.dsd.arch.corbaHelper;
import ca.concordia.dsd.database.StudentRecord;
import ca.concordia.dsd.database.TeacherRecord;
import ca.concordia.dsd.util.Constants;
import ca.concordia.dsd.util.LogUtil;
import org.omg.CORBA.ORB;
import org.omg.CosNaming.NamingContextExt;
import org.omg.CosNaming.NamingContextExtHelper;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Scanner;
import java.util.regex.Pattern;

public class ManagerClient implements Constants {

    //private static ICenterServer server;
    private static corba server;
    private LogUtil logUtil;

    public ManagerClient() {
        System.out.println("Initiating ManagerClient ");
    }

    public void start() {
        System.out.println("Starting MangerClient");
        try {
            String managerId = getManagerID();
             logUtil.log("Using managerId : " + managerId);

            takeInputForOperation(managerId);
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }

    public String getManagerID() {
        System.out.println("getManagerID");
        while (true) {
            System.out.println("Enter the manager id ? of format: <MTL1111>:");
            Scanner keyboard = new Scanner(System.in);
            String input = keyboard.nextLine();
            if (isValidManagerId(input)) {
                return input;
            } else {
                System.out.println("Not a valid manager ID of format: <MTL1111>  Try Again !!!");
            }
        }
    }

    public boolean isValidManagerId(String managerId) {
        if (managerId != null && managerId.length() == 7) {
            if (Pattern.compile("[0-9]*").matcher(managerId.substring(3, 6)).matches()) {
                String firstThree = managerId.substring(0, 3);
                // creating logUtil instance here so that different folder can be created for different managers
                logUtil = new LogUtil(managerId);
                if (firstThree.equalsIgnoreCase(MTL_TAG)) {
                    //return createCorbaConnection(MTL_TAG, MTL_SERVER_HOST, MTL_SERVER_PORT);
                    return createCorbaConnection(MTL_TAG, ORB_INITIAL_HOST, ORB_INITIAL_PORT);
                } else if (firstThree.equalsIgnoreCase(LVL_TAG)) {
                    //return createCorbaConnection(LVL_TAG, LVL_SERVER_HOST, LVL_SERVER_PORT);
                    return createCorbaConnection(LVL_TAG, ORB_INITIAL_HOST, ORB_INITIAL_PORT);
                } else if (firstThree.equalsIgnoreCase(DDO_TAG)) {
                    //return createCorbaConnection(DDO_TAG, DDO_SERVER_HOST, DDO_SERVER_PORT);
                    return createCorbaConnection(DDO_TAG, ORB_INITIAL_HOST, ORB_INITIAL_PORT);
                }
            }
        }
        return false;
    }

    // Putting commands arguments programmtically here:
    // java HelloClient -ORBInitialPort 1050 -ORBInitialHost localhost
    private boolean createCorbaConnection(String tag, String name, int port) {
        try {
            logUtil.log("createcorba connection tag: " + tag + " name: " + name + " port: " + port);
            // Setting the host and port programmtically here
            String args[] = new String[4];
            args[0] = "-ORBInitialPort";
            args[1] = Integer.toString(port);
            args[2] = "-ORBInitialHost ";
            args[3] = "localhost";

            // create and initialize the ORB
            ORB orb = ORB.init(args, null);

            // get the root naming context
            org.omg.CORBA.Object objRef = orb.resolve_initial_references("NameService");

            // Use NamingContextExt instead of NamingContext. This is
            // part of the Interoperable naming Service.
            NamingContextExt ncRef = NamingContextExtHelper.narrow(objRef);

            // resolve the Object Reference in Naming

            server = corbaHelper.narrow(ncRef.resolve_str(tag));


        }catch (Exception e){
            e.printStackTrace();
        }
        return true;
    }

    private void takeInputForOperation(String managerId) {
        int i = -1;
        while (i != 0) {
            System.out.println("Enter digit for operation : ");
            System.out.println("1. Create Teacher record ");
            System.out.println("2. Create Student record ");
            System.out.println("3. Get record count ");
            System.out.println("4. Edit record ");
            System.out.println("5. Transfer record to server ");
            System.out.println("6. Quit operations");
            Scanner keyboard = new Scanner(System.in);
            int input = Integer.parseInt(keyboard.nextLine());
            switch (input) {
                case 1:
                    // creating teacher record
                    System.out.println("first name of teacher : ");
                    String tFirstName = keyboard.nextLine();
                    System.out.println("last name of teacher : ");
                    String tLastName = keyboard.nextLine();
                    System.out.println("Address of the teacher : ");
                    String tAddress = keyboard.nextLine();
                    System.out.println("Phone Number of the teacher(XXX-XXX-XXXX) : ");
                    String tPhoneNumber = keyboard.nextLine();
                    System.out.println("Specialization of the teacher :");
                    String tSpecialization = keyboard.nextLine();
                    System.out.println("Location of the teacher (MTL/LVL/DDO) : ");
                    String tLocation = keyboard.nextLine();

                    TeacherRecord tR = new TeacherRecord("unknown", tFirstName,
                            tLastName, tAddress, tPhoneNumber, tSpecialization, tLocation);
                    logUtil.log(tR.toString());
                    String tID = null;
                    try {
                       // tID = server.createTRecord(managerId,tR);
                        tID = server.createTRecord(managerId,tFirstName,tLastName,tAddress,tPhoneNumber,tSpecialization,tLocation);
                    } catch (Exception e) {
                        logUtil.log(e.getMessage());
                    }
                    if (tID != null) {
                        logUtil.log("teacher id : " + tID + " is recorded.");
                    } else {
                        logUtil.log("teacher with id : " + tID + " is NOT recorded. Try Again !!");
                    }
                    break;
                case 2:
                    System.out.println("first name of student : ");
                    String sFirstName = keyboard.nextLine();
                    System.out.println("last name of student : ");
                    String sLastName = keyboard.nextLine();
                    System.out.println("Courses registered by student: e.g. <french,maths,dsd>");
                    ArrayList<String> sCourseList = new ArrayList<>();
                    String sCourses = keyboard.nextLine();
                    logUtil.log("Entered Student's courses to be added " + sCourses);
                    sCourseList = (ArrayList<String>) Arrays.asList(sCourses.split(","));
                    System.out.println("Status of student (Active/Inactive)");
                    String sStatus = keyboard.nextLine();
                    System.out.println("Status change date of student");
                    String sStatusChange = keyboard.nextLine();

                    StudentRecord sR = new StudentRecord("unknown", sFirstName,
                            sLastName, sCourseList, sStatus, sStatusChange);
                    logUtil.log(sR.toString());
                    String sID = null;
                    try {
                        //TODO : change sStatus to true or false, may be
                        //sID = server.createSRecord(managerId,sR);
                        sID = server.createSRecord(managerId,sFirstName,sLastName,sCourses,true,sStatusChange);
                    } catch (Exception re) {
                        logUtil.log(re.getMessage());
                    }
                    if (sID != null) {
                        logUtil.log("student id : " + sID + " is recorded.");
                    } else {
                        logUtil.log("student with id : " + sID + " is NOT recorded.");
                    }
                    break;
                case 3:
                    logUtil.log("Getting record counts from server");
                    try {
                        //TODO: add manager id here .. review and fix
                        //String numberOfRecords = server.getRecordCounts(managerId);
                        String numberOfRecords = server.getRecordCounts(managerId);
                        logUtil.log("total number of record : " + numberOfRecords);
                    } catch (Exception re) {
                        logUtil.log(re.getMessage());
                    }
                    logUtil.log("record count returned");
                    break;
                case 4:
                    System.out.println("Enter the recordID that you want to edit ?");
                    String recordID = keyboard.nextLine();
                    String recordType = recordID.substring(0, 2);
                    if (!recordType.equalsIgnoreCase("SR")
                            && !recordType.equalsIgnoreCase("TR")) {
                        logUtil.log("Invalid record ID format ");
                        break;
                    }

                    System.out.println("Enter the field name to edit: ");
                    System.out.println("TeacherRecord - <address, phone, location>");
                    System.out.println("StudentRecord - <courses, status, statusdate >");
                    String fieldName = keyboard.nextLine();
                    logUtil.log("Field name to be edited is : " + fieldName);

                    if (recordType.equalsIgnoreCase("TR")) {
                        if (!fieldName.equalsIgnoreCase("address")
                                && !fieldName.equalsIgnoreCase("phone")
                                && !fieldName.equalsIgnoreCase("location")) {
                            logUtil.log("Invalid field name " + fieldName);
                            break;
                        }
                    } else if (recordType.equalsIgnoreCase("SR")) {
                        if (!fieldName.equalsIgnoreCase("courses")
                                && !fieldName.equalsIgnoreCase("status")
                                && !fieldName.equalsIgnoreCase("statusdate")) {
                            logUtil.log("Invalid field name " + fieldName);
                            break;
                        }
                    }

                    System.out.println("Enter the new value for the field  " + fieldName + " :");
                    String newValue = keyboard.nextLine();
                    logUtil.log("new value for " + fieldName + " is : " + newValue);
                    try {
                        // TODO: review and fix stuffs
                        //String ret = server.editRecord(managerId,recordID, fieldName, newValue);
                        String ret = server .editRecord(managerId,recordID,fieldName,newValue);
                        //String ret = "";
                        if (!ret.contains("not found")) {
                            logUtil.log("Record updated successfully ");
                        } else {
                            logUtil.log("Record doesn't updated successfully, Try Again !!");
                        }
                    } catch (Exception re) {
                        logUtil.log(re.getMessage());
                    }
                    break;
                case 5:
                    System.out.println("Enter the record ID : ");
                    String rID = keyboard.nextLine();
                    System.out.println("Enter the server name : ");
                    String rServerName = keyboard.nextLine();
                    String isSuccess = server.transferRecord(managerId,rID,rServerName);
                    if("success".equalsIgnoreCase(isSuccess)){
                        logUtil.log(rID  + " transferred to " + rServerName);
                    }else{
                        logUtil.log(rID + " failed to tranfer to " + rServerName);
                    }
                    break;
                case 6:
                    i = 0;
                    break;
                default:
                    System.out.println("Wrong input, select between 1 to 5");
            }
        }
    }
}
