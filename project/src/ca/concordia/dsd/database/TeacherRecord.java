package ca.concordia.dsd.database;

public class TeacherRecord extends Records {

    String teacherId;
    String firstName;
    String lastName;
    String address;
    String phone;
    String specialization;
    String location;

    public TeacherRecord(String id, String fn, String ln, String a, String p, String s, String l) {
        super(id, ln,RecordType.TEACHER);
        this.teacherId = id;
        this.firstName = fn;
        this.lastName = ln;
        this.address = a;
        this.phone = p;
        this.specialization = s;
        this.location = l;
    }

    public String getTeacherId() {
        return teacherId;
    }

    public void setTeacherId(String teacherId) {
        this.teacherId = teacherId;
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

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getSpecialization() {
        return specialization;
    }

    public void setSpecialization(String specialization) {
        this.specialization = specialization;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    @Override
    public String toString() {
        return "TeacherRecord{" +
                "teacherId='" + teacherId + '\'' +
                ", firstName='" + firstName + '\'' +
                ", lastName='" + lastName + '\'' +
                ", address='" + address + '\'' +
                ", phone='" + phone + '\'' +
                ", specialization='" + specialization + '\'' +
                ", location='" + location + '\'' +
                '}';
    }
}
