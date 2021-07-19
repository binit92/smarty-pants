package ca.concordia.dsd.server;

import ca.concordia.dsd.util.Constants;

import javax.xml.ws.Endpoint;

public class CenterServerPublisher {

    public static void main(String args[]){
        try{
            CenterServerImpl MTLServer = new CenterServerImpl(Constants.MTL_TAG, Constants.MTL_SERVER_PORT,Constants.MTL_UDP_PORT);
            String MTLLink = "http://localhost:9000/mtl";
            Endpoint MTLEndpoint = Endpoint.publish(MTLLink, MTLServer);
            System.out.println("Publishing MTL Server at "+ MTLLink );

            /*
            CenterServerImpl LVLServer = new CenterServerImpl(Constants.LVL_TAG, Constants.LVL_SERVER_PORT,Constants.LVL_UDP_PORT);
            String LVLLink = "http://localhost:8082/lvl";
            Endpoint LVLEndpoint = Endpoint.publish(LVLLink, LVLServer);
            System.out.println("Publishing LVL Server at "+ LVLLink );

            CenterServerImpl DDOServer = new CenterServerImpl(Constants.DDO_TAG, Constants.DDO_SERVER_PORT,Constants.DDO_UDP_PORT);
            String DDOLink = "http://localhost:8083/ddo";
            Endpoint DDOEndpoint = Endpoint.publish(DDOLink, DDOServer);
            System.out.println("Publishing DDO Server at "+ DDOLink );
            */
        }catch(Exception e){
            e.printStackTrace();
        }
    }
}
