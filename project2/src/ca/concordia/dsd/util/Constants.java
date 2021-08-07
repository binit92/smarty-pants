package ca.concordia.dsd.util;

import java.io.File;

public interface Constants {

    String PROJ_DIR = System.getProperty("user.dir");
    String LOG_DIR = PROJ_DIR + File.separator + "logs";

    // use this to run all ca.concordia.dsd.server (DDO, LVL and MTL)
    int ORB_INITIAL_PORT = 5555;
    String ORB_INITIAL_HOST = "localhost";

    String FRONTEND_UDP_HOST = "localhost";
    int FRONTEND_UDP_PORT = 7717;

    String MTL_TAG = "MTL";
    String MTL_SERVER_HOST = "localhost";
    int MTL_SERVER_PORT = 7717;
    int MTL_UDP_PORT_LEADER = 9881;
    int MTL_UDP_PORT_REPLICA1 = 9882;
    int MTL_UDP_PORT_REPLICA2 = 9883;
    String MTL_1 = "MTL-1";
    String MTL_2 = "MTL-2";
    String MTL_3 = "MTL-3";

    String LVL_TAG = "LVL";
    String LVL_SERVER_HOST = "localhost";
    int LVL_SERVER_PORT = 7718;
    int LVL_UDP_PORT_LEADER = 9884;
    int LVL_UDP_PORT_REPLICA1 = 9885;
    int LVL_UDP_PORT_REPLICA2 = 9886;
    String LVL_1 = "LVL-1";
    String LVL_2 = "LVL-2";
    String LVL_3 = "LVL-3";

    String DDO_TAG = "DDO";
    String DDO_SERVER_HOST = "localhost";
    int DDO_SERVER_PORT = 7719;
    int DDO_UDP_PORT_LEADER = 9887;
    int DDO_UDP_PORT_REPLICA1 = 9888;
    int DDO_UDP_PORT_REPLICA2 = 9999;
    String DDO_1 = "DDO-1";
    String DDO_2 = "DDO-2";
    String DDO_3 = "DDO-3";

    String RESPONSE_DATA_SPLITTER = "#";
    int LEADER_ID = 100;
    int PRIMARY_ID = 0;
    int REPLICA1_ID = 1;
    int REPLICA2_ID = 2;

    int LEADER_DOWN_TIME_LIMIT = 1000;


    public static int TOTAL_SERVERS_COUNT = 3;
    public static int TOTAL_REPLICAS_COUNT = TOTAL_SERVERS_COUNT - 1;
    public static int PRIMARY_SERVER_ID = 1;
    public static int REPLICA1_SERVER_ID = 2;
    public static int REPLICA2_SERVER_ID = 3;
    public static String CURRENT_SERVER_IP = "localhost";
    public static int CURRENT_SERVER_UDP_PORT = 3333;
    public static String FRONT_END_IP = "localhost";
    public static int FRONT_END_UDP_PORT = 3232;
    public static int MULTICAST_PORT_NUMBER = 6779;
    public static String MULTICAST_IP_ADDRESS = "224.0.0.1";
    public static int CURRENT_PRIMARY_PORT_FOR_REPLICAS = 2323;
    public static int RETRY_TIME = 5000;
    public static String RECEIVED_DATA_SEPERATOR = ",";
    public static String RESPONSE_DATA_SEPERATOR = "_";
    public static String PROJECT_DIR = System.getProperty("user.dir");

    public static int MTL1_PORT = 8001;
    public static int MTL2_PORT = 8002;
    public static int MTL3_PORT = 8003;
    public static int LVL1_PORT = 8004;
    public static int LVL2_PORT = 8005;
    public static int LVL3_PORT = 8006;
    public static int DDO1_PORT = 8007;
    public static int DDO2_PORT = 8008;
    public static int DDO3_PORT = 8009;
    public static String MTL1 = "MTL1";
    public static String MTL2 = "MTL2";
    public static String MTL3 = "MTL3";
    public static String LVL1 = "LVL1";
    public static String LVL2 = "LVL2";
    public static String LVL3 = "LVL3";
    public static String DDO1 = "DDO1";
    public static String DDO2 = "DDO2";
    public static String DDO3 = "DDO3";

    public static String FRONTEND_TAG = "FRONTEND";
    public static String REPLICA_RESPONSE_TAG = "RESPONSE";
    public static String LEADER_TAG = "LEADER";
    public static String REPLICA1_TAG = "REPLICA1";
    public static String REPLICA2_TAG = "REPLICA2";
}
