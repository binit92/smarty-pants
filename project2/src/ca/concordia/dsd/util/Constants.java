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


    int TOTAL_SERVERS_COUNT = 3;
    int TOTAL_REPLICAS_COUNT = TOTAL_SERVERS_COUNT - 1;
    int PRIMARY_SERVER_ID = 1;
    int REPLICA1_SERVER_ID = 2;
    int REPLICA2_SERVER_ID = 3;
    String CURRENT_SERVER_IP = "localhost";
    int CURRENT_SERVER_UDP_PORT = 3333;
    String FRONT_END_IP = "localhost";
    int FRONT_END_UDP_PORT = 3232;
    int MULTICAST_PORT_NUMBER = 6779;
    String MULTICAST_IP_ADDRESS = "224.0.0.1";
    int CURRENT_PRIMARY_PORT_FOR_REPLICAS = 2323;
    int RETRY_TIME = 5000;
    String RECEIVED_DATA_SEPERATOR = ",";
    String RESPONSE_DATA_SEPERATOR = "_";
    String PROJECT_DIR = System.getProperty("user.dir");

    int MTL1_PORT = 8001;
    int MTL2_PORT = 8002;
    int MTL3_PORT = 8003;
    int LVL1_PORT = 8004;
    int LVL2_PORT = 8005;
    int LVL3_PORT = 8006;
    int DDO1_PORT = 8007;
    int DDO2_PORT = 8008;
    int DDO3_PORT = 8009;
    String MTL1 = "MTL1";
    String MTL2 = "MTL2";
    String MTL3 = "MTL3";
    String LVL1 = "LVL1";
    String LVL2 = "LVL2";
    String LVL3 = "LVL3";
    String DDO1 = "DDO1";
    String DDO2 = "DDO2";
    String DDO3 = "DDO3";

    String FRONTEND_TAG = "FRONTEND";
    String REPLICA_RESPONSE_TAG = "RESPONSE";
    String LEADER_TAG = "LEADER";
    String REPLICA1_TAG = "REPLICA1";
    String REPLICA2_TAG = "REPLICA2";
}
