package ca.concordia.dsd.server.frontend;

// Thread to transfer the response from leader to front end
public class TransferResponseThread  extends Thread{
    private String response;
    public TransferResponseThread(String response ){
        this.response = response;
    }

    @Override
    public void run() {
        System.out.println("TransferReponseThread :: " + this.response);
        FrontEnd.listOfResponse.add(this.response);
    }

    public String getResponse(){
        return response;
    }
}
