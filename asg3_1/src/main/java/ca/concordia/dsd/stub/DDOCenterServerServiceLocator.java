/**
 * DDOCenterServerServiceLocator.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Apr 22, 2006 (06:55:48 PDT) WSDL2Java emitter.
 */

package ca.concordia.dsd.stub;

import java.net.URL;

import javax.xml.rpc.ServiceException;

public class DDOCenterServerServiceLocator extends org.apache.axis.client.Service implements CenterServerImplService {

    public DDOCenterServerServiceLocator() {
    }


    public DDOCenterServerServiceLocator(org.apache.axis.EngineConfiguration config) {
        super(config);
    }

    public DDOCenterServerServiceLocator(java.lang.String wsdlLoc, javax.xml.namespace.QName sName) throws javax.xml.rpc.ServiceException {
        super(wsdlLoc, sName);
    }

    // Use to get a proxy class for CenterServerPort
    private java.lang.String CenterServerPort_address = "http://localhost:7717/ddo";

    public java.lang.String getCenterServerPortAddress() {
        return CenterServerPort_address;
    }

    // The WSDD service name defaults to the port name.
    private java.lang.String CenterServerPortWSDDServiceName = "CenterServerPort";

    public java.lang.String getCenterServerPortWSDDServiceName() {
        return CenterServerPortWSDDServiceName;
    }

    public void setCenterServerPortWSDDServiceName(java.lang.String name) {
        CenterServerPortWSDDServiceName = name;
    }

    public Stubs.CenterServer getCenterServerPort() throws javax.xml.rpc.ServiceException {
       java.net.URL endpoint;
        try {
            endpoint = new java.net.URL(CenterServerPort_address);
        }
        catch (java.net.MalformedURLException e) {
            throw new javax.xml.rpc.ServiceException(e);
        }
        return getCenterServerPort(endpoint);
    }

    public Stubs.CenterServer getCenterServerPort(java.net.URL portAddress) throws javax.xml.rpc.ServiceException {
        try {
            Stubs.CenterServerPortBindingStub _stub = new Stubs.CenterServerPortBindingStub(portAddress, this);
            _stub.setPortName(getCenterServerPortWSDDServiceName());
            return _stub;
        }
        catch (org.apache.axis.AxisFault e) {
            return null;
        }
    }

    public void setCenterServerPortEndpointAddress(java.lang.String address) {
        CenterServerPort_address = address;
    }

    /**
     * For the given interface, get the stub implementation.
     * If this service has no port for the given interface,
     * then ServiceException is thrown.
     */
    public java.rmi.Remote getPort(Class serviceEndpointInterface) throws javax.xml.rpc.ServiceException {
        try {
            if (Stubs.CenterServer.class.isAssignableFrom(serviceEndpointInterface)) {
                Stubs.CenterServerPortBindingStub _stub = new Stubs.CenterServerPortBindingStub(new java.net.URL(CenterServerPort_address), this);
                _stub.setPortName(getCenterServerPortWSDDServiceName());
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
        if ("CenterServerPort".equals(inputPortName)) {
            return getCenterServerPort();
        }
        else  {
            java.rmi.Remote _stub = getPort(serviceEndpointInterface);
            ((org.apache.axis.client.Stub) _stub).setPortName(portName);
            return _stub;
        }
    }

    public javax.xml.namespace.QName getServiceName() {
        return new javax.xml.namespace.QName("http://Servers/", "CenterServerService");
    }

    private java.util.HashSet ports = null;

    public java.util.Iterator getPorts() {
        if (ports == null) {
            ports = new java.util.HashSet();
            ports.add(new javax.xml.namespace.QName("http://Servers/", "CenterServerPort"));
        }
        return ports.iterator();
    }

    /**
    * Set the endpoint address for the specified port name.
    */
    public void setEndpointAddress(java.lang.String portName, java.lang.String address) throws javax.xml.rpc.ServiceException {
        
if ("CenterServerPort".equals(portName)) {
            setCenterServerPortEndpointAddress(address);
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


	@Override
	public String getCenterServerImplPortAddress() {
		// TODO Auto-generated method stub
		return null;
	}


	@Override
	public CenterServerImpl getCenterServerImplPort() throws ServiceException {
		// TODO Auto-generated method stub
		return null;
	}


	@Override
	public CenterServerImpl getCenterServerImplPort(URL portAddress) throws ServiceException {
		// TODO Auto-generated method stub
		return null;
	}

}
