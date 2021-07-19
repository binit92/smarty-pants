/**
 * CenterServerImplServiceLocator.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Apr 22, 2006 (06:55:48 PDT) WSDL2Java emitter.
 */

package ca.concordia.dsd.stub;

public class CenterServerImplServiceLocator extends org.apache.axis.client.Service implements ca.concordia.dsd.stub.CenterServerImplService {

    public CenterServerImplServiceLocator() {
    }


    public CenterServerImplServiceLocator(org.apache.axis.EngineConfiguration config) {
        super(config);
    }

    public CenterServerImplServiceLocator(java.lang.String wsdlLoc, javax.xml.namespace.QName sName) throws javax.xml.rpc.ServiceException {
        super(wsdlLoc, sName);
    }

    // Use to get a proxy class for CenterServerImplPort
    private java.lang.String CenterServerImplPort_address = "http://localhost:8888/ddo";

    public java.lang.String getCenterServerImplPortAddress() {
        return CenterServerImplPort_address;
    }

    // The WSDD service name defaults to the port name.
    private java.lang.String CenterServerImplPortWSDDServiceName = "CenterServerImplPort";

    public java.lang.String getCenterServerImplPortWSDDServiceName() {
        return CenterServerImplPortWSDDServiceName;
    }

    public void setCenterServerImplPortWSDDServiceName(java.lang.String name) {
        CenterServerImplPortWSDDServiceName = name;
    }

    public ca.concordia.dsd.stub.CenterServerImpl getCenterServerImplPort() throws javax.xml.rpc.ServiceException {
       java.net.URL endpoint;
        try {
            endpoint = new java.net.URL(CenterServerImplPort_address);
        }
        catch (java.net.MalformedURLException e) {
            throw new javax.xml.rpc.ServiceException(e);
        }
        return getCenterServerImplPort(endpoint);
    }

    public ca.concordia.dsd.stub.CenterServerImpl getCenterServerImplPort(java.net.URL portAddress) throws javax.xml.rpc.ServiceException {
        try {
            ca.concordia.dsd.stub.CenterServerImplPortBindingStub _stub = new ca.concordia.dsd.stub.CenterServerImplPortBindingStub(portAddress, this);
            _stub.setPortName(getCenterServerImplPortWSDDServiceName());
            return _stub;
        }
        catch (org.apache.axis.AxisFault e) {
            return null;
        }
    }

    public void setCenterServerImplPortEndpointAddress(java.lang.String address) {
        CenterServerImplPort_address = address;
    }

    /**
     * For the given interface, get the stub implementation.
     * If this service has no port for the given interface,
     * then ServiceException is thrown.
     */
    public java.rmi.Remote getPort(Class serviceEndpointInterface) throws javax.xml.rpc.ServiceException {
        try {
            if (ca.concordia.dsd.stub.CenterServerImpl.class.isAssignableFrom(serviceEndpointInterface)) {
                ca.concordia.dsd.stub.CenterServerImplPortBindingStub _stub = new ca.concordia.dsd.stub.CenterServerImplPortBindingStub(new java.net.URL(CenterServerImplPort_address), this);
                _stub.setPortName(getCenterServerImplPortWSDDServiceName());
                return _stub;
            }
        }
        catch (java.lang.Throwable t) {
            throw new javax.xml.rpc.ServiceException(t);
        }
        throw new javax.xml.rpc.ServiceException("There is no stub implementation for the interface:  " + (serviceEndpointInterface == null ? "null" : serviceEndpointInterface.getName()));
    }

    /**
     * For the given interface, get the stub implementation.
     * If this service has no port for the given interface,
     * then ServiceException is thrown.
     */
    public java.rmi.Remote getPort(javax.xml.namespace.QName portName, Class serviceEndpointInterface) throws javax.xml.rpc.ServiceException {
        if (portName == null) {
            return getPort(serviceEndpointInterface);
        }
        java.lang.String inputPortName = portName.getLocalPart();
        if ("CenterServerImplPort".equals(inputPortName)) {
            return getCenterServerImplPort();
        }
        else  {
            java.rmi.Remote _stub = getPort(serviceEndpointInterface);
            ((org.apache.axis.client.Stub) _stub).setPortName(portName);
            return _stub;
        }
    }

    public javax.xml.namespace.QName getServiceName() {
        return new javax.xml.namespace.QName("http://server.dsd.concordia.ca/", "CenterServerImplService");
    }

    private java.util.HashSet ports = null;

    public java.util.Iterator getPorts() {
        if (ports == null) {
            ports = new java.util.HashSet();
            ports.add(new javax.xml.namespace.QName("http://server.dsd.concordia.ca/", "CenterServerImplPort"));
        }
        return ports.iterator();
    }

    /**
    * Set the endpoint address for the specified port name.
    */
    public void setEndpointAddress(java.lang.String portName, java.lang.String address) throws javax.xml.rpc.ServiceException {
        
if ("CenterServerImplPort".equals(portName)) {
            setCenterServerImplPortEndpointAddress(address);
        }
        else 
{ // Unknown Port Name
            throw new javax.xml.rpc.ServiceException(" Cannot set Endpoint Address for Unknown Port" + portName);
        }
    }

    /**
    * Set the endpoint address for the specified port name.
    */
    public void setEndpointAddress(javax.xml.namespace.QName portName, java.lang.String address) throws javax.xml.rpc.ServiceException {
        setEndpointAddress(portName.getLocalPart(), address);
    }

}
