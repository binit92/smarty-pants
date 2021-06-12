package ca.concordia.dsd.server;

import ca.concordia.dsd.util.LogUtil;

public class MTLServer extends CenterServerImpl implements Runnable {

    private static String SERVER_NAME = "MTL";
    private LogUtil logUtil;
    //private HashMap<String, List<>>

    public MTLServer(){
        logUtil = new LogUtil(SERVER_NAME);

    }

    @Override
    public void run() {
        try{
            while(true){

            }
        }catch (Exception e){
            logUtil.log(e.getMessage());
        }finally {
            logUtil.close();
        }
    }
}
