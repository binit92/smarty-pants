package ca.concordia.dsd.server.impl;


import ca.concordia.dsd.database.Records;

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
    /**
     * Writes to the filename if file exists, If file does not exist, creates a
     * file and then writes the contents
     *
     * @param filename
     * @param content
     */
    File file;
    FileWriter fw;
    BufferedWriter bw;
    DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
    Date date;

    public DcmsServerBackupWriter() {

    }

    /**
     * Retrieves the current Date of the system and checks for existence of the
     * file. If no, a new file is created and the timestamp is written to it
     *
     * @param filename Has the name of the Filename stored in it.
     */

    public DcmsServerBackupWriter(String filename) {
        super();
        date = new Date();
        String timestamp = dateFormat.format(date);
        this.file = new File(filename);
        System.out.println(this.file);
        if (!file.exists()) {
            try {
                //file.mkdirs();
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

    private static void print(BufferedWriter bw, HashMap<String, List<Records>> temp) throws IOException {
        for (String name : temp.keySet()) {
            String key = name;
            String value = temp.get(name).toString();
            bw.write(key + ": " + value);
            bw.newLine();
        }

    }

    /**
     * Receives the HashMap as a parameter and writes it to the file as a backup
     * along with the current timestamp
     *
     * @param temp HashMap that is the repository of a server
     */

    public void backupMap(HashMap<String, List<Records>> temp) {
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
