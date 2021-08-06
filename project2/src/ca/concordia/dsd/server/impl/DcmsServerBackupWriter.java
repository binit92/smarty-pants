package ca.concordia.dsd.server.impl;

import ca.concordia.dsd.database.Record;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

public class DcmsServerBackupWriter {

    File file;
    FileWriter fw;
    BufferedWriter bw;
    DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
    Date date;

    public DcmsServerBackupWriter() {

    }


    public DcmsServerBackupWriter(String filename) {
        super();
        date = new Date();
        String timestamp = dateFormat.format(date);
        this.file = new File(filename);
        System.out.println(this.file);
        if (!file.exists()) {
            try {

                file.createNewFile();

                fw = new FileWriter(file.getAbsoluteFile());
                bw = new BufferedWriter(fw);
                bw.write(timestamp + " : " + "DCMS_Backup");
                bw.newLine();
                bw.close();
                fw.close();
            } catch (IOException e) {
                e.printStackTrace();
            }

        }
    }

    private static void print(BufferedWriter bw, HashMap<String, List<Record>> temp) throws IOException {
        for (String name : temp.keySet()) {
            String key = name;
            String value = temp.get(name).toString();
            bw.write(key + ": " + value);
            bw.newLine();
        }

    }

    public void backupMap(HashMap<String, List<Record>> temp) {
        try {
            date = new Date();
            String timestamp = dateFormat.format(date);
            fw = new FileWriter(file.getAbsoluteFile(), true);
            bw = new BufferedWriter(fw);
            bw.write(timestamp + " : ");
            bw.newLine();
            print(bw, temp);
            bw.close();
            fw.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
