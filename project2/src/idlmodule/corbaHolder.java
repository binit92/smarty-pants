package idlmodule;

/**
* idlmodule/corbaHolder.java .
* Generated by the IDL-to-Java compiler (portable), version "3.2"
* from corba.idl
* Thursday, 5 August, 2021 8:11:48 PM EDT
*/

public final class corbaHolder implements org.omg.CORBA.portable.Streamable
{
  public idlmodule.corba value = null;

  public corbaHolder ()
  {
  }

  public corbaHolder (idlmodule.corba initialValue)
  {
    value = initialValue;
  }

  public void _read (org.omg.CORBA.portable.InputStream i)
  {
    value = idlmodule.corbaHelper.read (i);
  }

  public void _write (org.omg.CORBA.portable.OutputStream o)
  {
    idlmodule.corbaHelper.write (o, value);
  }

  public org.omg.CORBA.TypeCode _type ()
  {
    return idlmodule.corbaHelper.type ();
  }

}
