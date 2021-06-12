package ca.concordia.dsd.database;

import java.util.ArrayList;

public class StudentRecord extends Records {

    private String studentID;
    private String firstName;
    private String lastName;
    private ArrayList<String> coursesRegistered = new ArrayList<>();
    private String status;
    private String statusDate;

    public StudentRecord(String id, String fn, String ln, ArrayList<String> c, String s, String sd){
        super(id,ln);
        this.studentID = id;
        this.firstName = fn;
        this.lastName = ln;
        this.coursesRegistered = c;
        this.status = s;
        this.statusDate= sd;
    }

    public String getStudentID() {
        return studentID;
    }

    public void setStudentID(String studentID) {
        this.studentID = studentID;
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

    public ArrayList<String> getCoursesRegistered() {
        return coursesRegistered;
    }

    public void setCoursesRegistered(ArrayList<String> coursesRegistered) {
        this.coursesRegistered = coursesRegistered;
    }

    public String getStatus() {
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
}
