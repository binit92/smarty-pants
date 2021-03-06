package ca.concordia.dsd.database;

import java.io.Serializable;
import java.util.List;

public class Student extends Record implements Serializable {
    String firstName;
    String lastName;
    List<String> CoursesRegistered;
    String status;
    String statusDate;
    String studentID;
    String ManagerID;

    public Student(String managerID, String studentID, String firstName,
                   String lastname, List<String> CoursesRegistered, String status,
                   String statusDate) {
        super(studentID, firstName, lastname);
        this.setManagerID(managerID);
        this.setCoursesRegistered(CoursesRegistered);
        this.setStatus(status);
        this.setStatusDate(statusDate);
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public List<String> getCoursesRegistered() {
        return CoursesRegistered;
    }

    public void setCoursesRegistered(List<String> courses) {
        CoursesRegistered = courses;
    }

    public String isStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getStatusDate() {
        return statusDate;
    }

    public void setStatusDate(String statusDate) {
        this.statusDate = statusDate;
    }

    public String getStudentID() {
        return studentID;
    }

    public void setStudentID(String studentID) {
        this.studentID = studentID;
    }

    public String getManagerID() {
        return ManagerID;
    }

    public void setManagerID(String managerID) {
        ManagerID = managerID;
    }


    public String toString() {
        return this.getManagerID() + "," + this.getRecordID() + ","
                + this.getFirstName() + "," + this.getLastName() + ","
                + this.getCoursesRegistered() + "," + this.isStatus() + ","
                + this.getStatusDate();
    }

}