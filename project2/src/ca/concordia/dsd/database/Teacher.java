package ca.concordia.dsd.database;

import java.io.Serializable;

public class Teacher extends Record implements Serializable {
    private String FirstName;
    private String LastName;
    private String Address;
    private String Phone;
    private String Specialization;
    private String Location;
    private String TeacherID;
    private String ManagerID;

    public String getFirstName() {
        return FirstName;
    }

    public void setFirstName(String firstName) {
        this.FirstName = firstName;
    }

    public String getLastName() {
        return LastName;
    }

    public void setLastName(String lastName) {
        this.LastName = lastName;
    }

    public String getAddress() {
        return Address;
    }

    public void setAddress(String address) {
        Address = address;
    }

    public String getPhone() {
        return Phone;
    }

    public void setPhone(String phone) {
        this.Phone = phone;
    }

    public String getSpecialization() {
        return Specialization;
    }

    public void setSpecialization(String specialization) {
        this.Specialization = specialization;
    }

    public String getLocation() {
        return Location;
    }

    public void setLocation(String location) {
        this.Location = location;
    }

    public String getManagerID() {
        return ManagerID;
    }

    public void setManagerID(String managerID) {
        ManagerID = managerID;
    }

    @Override
    public String toString() {
        return this.getManagerID() + ","
                + this.getRecordID() + ","
                + this.getFirstName() + ","
                + this.getLastName() + ","
                + this.getAddress() + ","
                + this.getPhone() + ","
                + this.getSpecialization() + ","
                + this.getLocation();
    }

    public Teacher(String managerID, String teacherID, String firstName,
                   String lastname, String address, String phone, String Specialization,
                   String location) {
        super(teacherID, firstName, lastname);
        this.setManagerID(managerID);
        this.setAddress(address);
        this.setPhone(phone);
        this.setSpecialization(Specialization);
        this.setLocation(location);
    }
}
