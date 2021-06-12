package ca.concordia.dsd;

import ca.concordia.dsd.server.MTLServer;

public class RunMTL {
    public static void main(String[] args) {
        System.out.println("Run MTL Server");
        Thread t = new Thread(new MTLServer());
        t.start();
    }
}
