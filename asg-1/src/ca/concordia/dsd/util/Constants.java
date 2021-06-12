package ca.concordia.dsd.util;

import java.io.File;

public interface Constants {

    public static final String PROJ_DIR  = System.getProperty("user.dir");
    public static final String LOG_DIR = PROJ_DIR + File.separator  + "logs";

    public static final String MTL_TAG = "MTL";
    public static final String MTL_SERVER_HOST = "localhost";
    public static final int MTL_SERVER_PORT = 7717;

    public static final String LVL_TAG = "LVL";
    public static final String LVL_SERVER_HOST = "localhost";
    public static final int LVL_SERVER_PORT =  7718;

    public static final String DDO_TAG = "DDO";
    public static final String DDO_SERVER_HOST = "localhost";
    public static final int DDO_SERVER_PORT = 7719;


}
