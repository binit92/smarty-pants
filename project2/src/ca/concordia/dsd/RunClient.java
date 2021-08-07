package ca.concordia.dsd;

import ca.concordia.dsd.client.ManagerClient;

public class RunClient {

    public static void main(String[] args) {
        System.out.println("Running Manager Client to connect to one of the server (MTL,LVL,DDO)");
        ManagerClient client = new ManagerClient();
        client.start();
    }
}
