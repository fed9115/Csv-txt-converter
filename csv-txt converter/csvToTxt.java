package CsvToTxt;

import java.io.BufferedReader;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * Converting csv files to txt files by compressing them to texts so that make
 * them easier to transfer. Giving 3 arguments, args[0] is the path of the
 * folder containing csv files, args[1] is the number of csv files compressed to
 * one txt file, args[2] is the path of the save folder
 * 
 * @author maxwellchen, Erdi Fan
 *
 */

public class csvToTxt {

	public static void main(String[] args) throws FileNotFoundException {

		File dir = new File(args[0]);

		// Iterating all csv files in the folders to get an array of csvs
		File[] files = dir.listFiles();

		ArrayList<File> csvFile = new ArrayList<>();

		for (File f : files) {
			if (f.getName().endsWith(".csv")) {
				// Generate an array list of csv files only
				csvFile.add(f);
			}
		}

		// Use the first csv file in the list to get the date
		String[] fileName = csvFile.get(0).getName().split("_");
		String date = fileName[fileName.length - 1];
		date = date.substring(0, date.length() - 4);

		// No. of elements in the csvFile array we need to convert at a time
		int numOfFilesOneTime = Integer.parseInt(args[1]);

		// save path
		String saveFolderPath = args[2];

		// i is the No. of txt files we need to generate
		for (int i = 0; i < csvFile.size() / numOfFilesOneTime + 1; i++) {
			// name of output txt
			String txtName = "stn_" + (i + 1) + "_" + date + ".txt";
			// create new txt file
			File output = new File(saveFolderPath + txtName);
			try {
				output.createNewFile();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			try {
				FileWriter fw = new FileWriter(output);
				// for how many csv files needed to be converted into one txt file
				for (int j = i * numOfFilesOneTime; j < (i + 1) * numOfFilesOneTime && j < csvFile.size(); j++) {
					// Use BufferedReader to read the contents in the csv files
					@SuppressWarnings("resource")
					BufferedReader reader = new BufferedReader(new FileReader(csvFile.get(j)));
					String[] thisFileName = csvFile.get(j).getName().split("_");
					String thisFileNumber = thisFileName[1];
					fw.write(thisFileNumber + "\t"); // include csv file number before each line
					// Ignore the first line
					reader.readLine();
					String lineContent = null;
					Date lastDate = null; // store last read date and time
					// read each line in csv file
					while ((lineContent = reader.readLine()) != null) {
						String[] items = lineContent.split(",");
						SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
						Date currentDate = null;
						try {
							// get date and time of current line
							currentDate = format.parse(items[0]);
							if (lastDate == null) // goes here on the first line
							{
								lastDate = currentDate;
							}
							// calculate if there's any difference between current time and last read time,
							// by hr
							long differential = (currentDate.getTime() - lastDate.getTime()) / 3600000;
							for (long l = 1; l < differential; l++) // runs if difference is > 1 hr
							{
								for (int m = 1; m < items.length; m++) {
									// deal with data in columns wdir_10, rh_10, p_10
									if (m == 4 || m == 6 || m == 7) {
										// no decimals
										fw.write("0 ");
									} else {
										// one decimal
										fw.write("0.0 ");
									}
								}
								fw.write("\t");
							}
							lastDate = currentDate; // update lastDate
						} catch (ParseException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
						for (int k = 1; k < items.length; k++) { // for each item per line
							String formattedStr;
							if (k == 4 || k == 6 || k == 7) {
								// no decimals
								formattedStr = String.format("%.0f", Double.parseDouble(items[k]));
							} else {
								// 1 decimal
								formattedStr = String.format("%.1f", Double.parseDouble(items[k]));
							}
							fw.write(formattedStr);
							// Separate every two units with a blank space
							fw.write(" ");
						}
						// Separate every rows with a tab
						fw.write("\t");
					}
					// Separate every two csv files with a new line
					fw.write("\r\n");
					fw.flush();
				}
				fw.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			// close the writer
		}
	}
}
