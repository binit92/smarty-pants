/**
 * CenterServerImpl.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Apr 22, 2006 (06:55:48 PDT) WSDL2Java emitter.
 */

package ca.concordia.dsd.stub;

public interface CenterServerImpl extends java.rmi.Remote {
    public java.lang.String createTRecord(java.lang.String arg0, java.lang.String arg1, java.lang.String arg2, java.lang.String arg3, java.lang.String arg4, java.lang.String arg5, java.lang.String arg6) throws java.rmi.RemoteException;
    public java.lang.String getRecordCounts(java.lang.String arg0) throws java.rmi.RemoteException;
    public java.lang.String editRecord(java.lang.String arg0, java.lang.String arg1, java.lang.String arg2, java.lang.String arg3) throws java.rmi.RemoteException;
    public java.lang.String createSRecord(java.lang.String arg0, java.lang.String arg1, java.lang.String arg2, java.lang.String arg3, boolean arg4, java.lang.String arg5) throws java.rmi.RemoteException;
    public java.lang.String transferRecord(java.lang.String arg0, java.lang.String arg1, java.lang.String arg2) throws java.rmi.RemoteException;
}
