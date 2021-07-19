package ca.concordia.dsd.stub;

public class CenterServerImplProxy implements ca.concordia.dsd.stub.CenterServerImpl {
  private String _endpoint = null;
  private ca.concordia.dsd.stub.CenterServerImpl centerServerImpl = null;
  
  public CenterServerImplProxy() {
    _initCenterServerImplProxy();
  }
  
  public CenterServerImplProxy(String endpoint) {
    _endpoint = endpoint;
    _initCenterServerImplProxy();
  }
  
  private void _initCenterServerImplProxy() {
    try {
      centerServerImpl = (new ca.concordia.dsd.stub.CenterServerImplServiceLocator()).getCenterServerImplPort();
      if (centerServerImpl != null) {
        if (_endpoint != null)
          ((javax.xml.rpc.Stub)centerServerImpl)._setProperty("javax.xml.rpc.service.endpoint.address", _endpoint);
        else
          _endpoint = (String)((javax.xml.rpc.Stub)centerServerImpl)._getProperty("javax.xml.rpc.service.endpoint.address");
      }
      
    }
    catch (javax.xml.rpc.ServiceException serviceException) {}
  }
  
  public String getEndpoint() {
    return _endpoint;
  }
  
  public void setEndpoint(String endpoint) {
    _endpoint = endpoint;
    if (centerServerImpl != null)
      ((javax.xml.rpc.Stub)centerServerImpl)._setProperty("javax.xml.rpc.service.endpoint.address", _endpoint);
    
  }
  
  public ca.concordia.dsd.stub.CenterServerImpl getCenterServerImpl() {
    if (centerServerImpl == null)
      _initCenterServerImplProxy();
    return centerServerImpl;
  }
  
  public java.lang.String createTRecord(java.lang.String arg0, java.lang.String arg1, java.lang.String arg2, java.lang.String arg3, java.lang.String arg4, java.lang.String arg5, java.lang.String arg6) throws java.rmi.RemoteException{
    if (centerServerImpl == null)
      _initCenterServerImplProxy();
    return centerServerImpl.createTRecord(arg0, arg1, arg2, arg3, arg4, arg5, arg6);
  }
  
  public java.lang.String getRecordCounts(java.lang.String arg0) throws java.rmi.RemoteException{
    if (centerServerImpl == null)
      _initCenterServerImplProxy();
    return centerServerImpl.getRecordCounts(arg0);
  }
  
  public java.lang.String editRecord(java.lang.String arg0, java.lang.String arg1, java.lang.String arg2, java.lang.String arg3) throws java.rmi.RemoteException{
    if (centerServerImpl == null)
      _initCenterServerImplProxy();
    return centerServerImpl.editRecord(arg0, arg1, arg2, arg3);
  }
  
  public java.lang.String createSRecord(java.lang.String arg0, java.lang.String arg1, java.lang.String arg2, java.lang.String arg3, boolean arg4, java.lang.String arg5) throws java.rmi.RemoteException{
    if (centerServerImpl == null)
      _initCenterServerImplProxy();
    return centerServerImpl.createSRecord(arg0, arg1, arg2, arg3, arg4, arg5);
  }
  
  public java.lang.String transferRecord(java.lang.String arg0, java.lang.String arg1, java.lang.String arg2) throws java.rmi.RemoteException{
    if (centerServerImpl == null)
      _initCenterServerImplProxy();
    return centerServerImpl.transferRecord(arg0, arg1, arg2);
  }
  
  
}