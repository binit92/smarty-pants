package ca.concordia.dsd.server.frontend.helper;

import ca.concordia.dsd.server.frontend.FrontEnd;
import ca.concordia.dsd.util.LogUtil;

public class ResponseThread extends Thread {
    private static final String TAG = "|" + ResponseThread.class.getSimpleName() + "| ";

    private final String response;
    private final LogUtil logUtil;

    public ResponseThread(String response, LogUtil logUtil) {
        this.response = response;
        this.logUtil = logUtil;
    }

    public void run() {
        logUtil.log(TAG + " response: " + this.response);

        FrontEnd.receivedResponsesArraylist.add(this.response);
    }

    public String getResponse() {
        return response;
    }
}
