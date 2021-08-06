package ca.concordia.dsd.server.impl;

import java.io.*;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;

import ca.concordia.dsd.model.Record;

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
		String timestamp = dateFormat.format(date).toString();
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


	public void backupMap(HashMap<String, List<Record>> temp) {
		try {
			date = new Date();
			String timestamp = dateFormat.format(date).toString();
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

	private static void print(BufferedWriter bw, HashMap<String, List<Record>> temp) throws IOException {
		for (String name : temp.keySet()) {
			String key = name.toString();
			String value = temp.get(name).toString();
			bw.write(key + ": " + value);
			bw.newLine();
		}

	}

}
