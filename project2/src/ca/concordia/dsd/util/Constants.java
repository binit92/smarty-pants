package ca.concordia.dsd.util;

import java.io.File;

public interface Constants {

    String PROJ_DIR = System.getProperty("user.dir");
    String LOG_DIR = PROJ_DIR + File.separator + "logs";

    int MULTICAST_PORT_NUMBER = 6779;
    String MULTICAST_IP_ADDRESS = "224.0.0.1";
    int CURRENT_PRIMARY_PORT_FOR_REPLICAS = 2323;

    // use this to run all ca.concordia.dsd.server (DDO, LVL and MTL)
    int ORB_INITIAL_PORT = 5555;
    String ORB_INITIAL_HOST = "localhost";

    String FRONT_END_UDP_HOST = "localhost";
    int FRONT_END_UDP_PORT = 7717;

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
    int PRIMARY_SERVER_ID = 0;
    int REPLICA1_ID = 1;
    int REPLICA2_ID = 2;

    int LEADER_DOWN_TIME_LIMIT = 1000;

}
