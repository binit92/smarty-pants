package ca.concordia.dsd.util;

import java.io.File;

public interface Constants {

    public static final String PROJ_DIR = System.getProperty("user.dir");
    public static final String LOG_DIR = PROJ_DIR + File.separator + "logs";

    // use this to run all server (DDO, LVL and MTL)
    public static final int ORB_INITIAL_PORT = 5555;
    public static final String ORB_INITIAL_HOST = "localhost";

    public static final String FRONT_END_UDP_HOST = "localhost";
    public static final int FRONT_END_UDP_PORT = 7717;

    public static final String MTL_TAG = "MTL";
    public static final String MTL_SERVER_HOST = "localhost";
    public static final int MTL_SERVER_PORT = 7717;
    public static final int MTL_UDP_PORT_LEADER = 9881;
    public static final int MTL_UDP_PORT_REPLICA1 = 9882;
    public static final int MTL_UDP_PORT_REPLICA2 = 9883;
    public static final String MTL_1 = "MTL-1";
    public static final String MTL_2 = "MTL-2";
    public static final String MTL_3 = "MTL-3";

    public static final String LVL_TAG = "LVL";
    public static final String LVL_SERVER_HOST = "localhost";
    public static final int LVL_SERVER_PORT = 7718;
    public static final int LVL_UDP_PORT_LEADER = 9884;
    public static final int LVL_UDP_PORT_REPLICA1 = 9885;
    public static final int LVL_UDP_PORT_REPLICA2 = 9886;
    public static final String LVL_1 = "LVL-1";
    public static final String LVL_2 = "LVL-2";
    public static final String LVL_3 = "LVL-3";

    public static final String DDO_TAG = "DDO";
    public static final String DDO_SERVER_HOST = "localhost";
    public static final int DDO_SERVER_PORT = 7719;
    public static final int DDO_UDP_PORT_LEADER = 9887;
    public static final int DDO_UDP_PORT_REPLICA1 = 9888;
    public static final int DDO_UDP_PORT_REPLICA2 = 9999;
    public static final String DDO_1 = "DDO-1";
    public static final String DDO_2 = "DDO-2";
    public static final String DDO_3 = "DDO-3";

    public static final String RESPONSE_DATA_SPLITTER = "#";
    public static final int LEADER_ID = 100;
    public static final int PRIMARY_SERVER_ID = 0;
    public static final int REPLICA1_ID = 1;
    public static final int REPLICA2_ID = 2;

    public static final int LEADER_DOWN_TIME_LIMIT = 1000;

}
