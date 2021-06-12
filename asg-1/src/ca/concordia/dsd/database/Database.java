package ca.concordia.dsd.database;

import java.util.ArrayList;
import java.util.HashMap;

// in-memory records
public class Database {

    private HashMap<String, ArrayList<Records>> db = new HashMap<String, ArrayList<Records>>();

    public void createTRecord(TeacherRecord teacher){
        if (teacher != null){
            String id = teacher.getTeacherId();
            ArrayList<Records> records = db.get(id);
            if (records == null){
                records = new ArrayList<Records>();
                records.add(teacher);
            }else{
                records.add(teacher);
            }
            db.put(id,records);
        }
    }

    public void createSRecord(StudentRecord student){
        if (student != null){
            String id = student.getStudentID();
            ArrayList<Records> records = db.get(id);
            if (records == null){
                records = new ArrayList<Records>();
                records.add(student);
            }else{
                records.add(student);
            }
            db.put(id,records);
        }
    }

    public int getRecordCount(){
        int total = 0;
        for (ArrayList<Records> records : db.values()){
            total += records.size();
        }
        return total;
    }

    public void editRecord(){

    }

}
