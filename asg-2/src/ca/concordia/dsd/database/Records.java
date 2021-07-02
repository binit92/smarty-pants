package ca.concordia.dsd.database;

import java.io.Serializable;

public class Records implements Serializable {

    private String uniqueId;
    private String lastName;

    public Records(String ui, String ln) {
        this.uniqueId = ui;
        this.lastName = ln;
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

    @Override
    public String toString() {
        return "Records{" +
                "uniqueId='" + uniqueId + '\'' +
                ", lastName='" + lastName + '\'' +
                '}';
    }
}
