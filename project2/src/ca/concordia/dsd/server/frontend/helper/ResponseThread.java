package ca.concordia.dsd.server.frontend.helper;

import ca.concordia.dsd.server.frontend.FrontEnd;

public class ResponseThread extends Thread {
    String response;

    public ResponseThread(String response) {
        this.response = response;
    }

    public void run() {
        System.out.println("=============" + this.response);
        FrontEnd.receivedResponsesArraylist.add(this.response);
    }

    public String getResponse() {
        return response;
    }
}
