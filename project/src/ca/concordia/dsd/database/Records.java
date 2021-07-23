package ca.concordia.dsd.database;

import java.io.Serializable;

public class Records implements Serializable {

    public enum RecordType {TEACHER, STUDENT}
    private String uniqueId;
    private String lastName;
    private RecordType type;

    public Records(String ui, String ln,RecordType type) {
        this.uniqueId = ui;
        this.lastName = ln;
        this.type = type;
    }

    public String getUniqueId() {
        return uniqueId;
    }

    public void setUniqueId(String uniqueId) {
        this.uniqueId = uniqueId;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public RecordType getType() {
        return type;
    }

    public void setType(RecordType type) {
        this.type = type;
    }

    @Override
    public String toString() {
        return "Records{" +
                "uniqueId='" + uniqueId + '\'' +
                ", lastName='" + lastName + '\'' +
                ", type='" + type + '\'' +
                '}';
    }
}
