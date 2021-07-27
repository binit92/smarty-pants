package ca.concordia.dsd.util;

import java.io.File;

public interface Constants {

    public static final String PROJ_DIR = System.getProperty("user.dir");
    public static final String LOG_DIR = PROJ_DIR + File.separator + "logs";

    public static final String MTL_TAG = "MTL";
    public static final String MTL_SERVER_HOST = "localhost";
    public static final int MTL_SERVER_PORT = 7717;
    public static final int MTL_UDP_PORT_LEADER = 9881;
    public static final int MTL_UDP_PORT_REPLICA1 = 9882;
    public static final int MTL_UDP_PORT_REPLICA2 = 9883;

    public static final String LVL_TAG = "LVL";
    public static final String LVL_SERVER_HOST = "localhost";
    public static final int LVL_SERVER_PORT = 7718;
    public static final int LVL_UDP_PORT_LEADER = 9884;
    public static final int LVL_UDP_PORT_REPLICA1 = 9885;
    public static final int LVL_UDP_PORT_REPLICA2 = 9886;

    public static final String DDO_TAG = "DDO";
    public static final String DDO_SERVER_HOST = "localhost";
    public static final int DDO_SERVER_PORT = 7719;
    public static final int DDO_UDP_PORT_LEADER = 9887;
    public static final int DDO_UDP_PORT_REPLICA1 = 9888;
    public static final int DDO_UDP_PORT_REPLICA2 = 9999;


}
