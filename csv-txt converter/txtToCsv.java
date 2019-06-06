package test9;

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
 * Convert txt files back to csv files for uncompressing after transfer.
 * 
 * @author maxwellchen, Erdi Fan
 *
 */

public class txtToCsv {

	public static void main(String[] args) throws FileNotFoundException {

		File dir = new File(args[0]);

		// Iterating all txt files in the folders to get an array of txts
		File[] files = dir.listFiles();

		ArrayList<File> txtFiles = new ArrayList<>();

		for (File f : files) {
			if (f.getName().endsWith(".txt")) {
				// Generate an array list of txt files
				txtFiles.add(f);
			}
		}

		// Get the date of the first file
		String[] fileName = txtFiles.get(0).getName().split("_");
		String date = fileName[fileName.length - 1];
		date = date.substring(0, date.length() - 4);
		SimpleDateFormat format = new SimpleDateFormat("yyyyMMdd"); // date format for every file
		Date beginDate = null;
		try {
			beginDate = format.parse(date.substring(0, 8)); // get date of file from fileName
		} catch (ParseException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}

		Date start = new Date(beginDate.getTime() + 3600000 * 24); // get next day after file date
		SimpleDateFormat startDate = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss"); // time format for every line

		String saveFolderPath = args[1];

		for (int i = 0; i < txtFiles.size(); i++) // for every txt file
		{
			BufferedReader reader = new BufferedReader(new FileReader(txtFiles.get(i)));
			String csvFile = null;
			try {
				while ((csvFile = reader.readLine()) != null) {
					String[] lines = csvFile.split("\t"); // split into every line in csv
					String csvName = "stn_" + lines[0] + "_" + date + ".csv"; // create csv file with file number
					File output = new File(saveFolderPath + csvName);
					output.createNewFile();
					FileWriter fw = new FileWriter(output);
					fw.write("datetime,rain_sfc,snow_sfc,wspd_10,wdir_10,t_10,rh_10,p_10\r\n"); // header

					for (int j = 1; j < lines.length; j++) // for each line in csv
					{
						Date current = new Date(start.getTime() + 3600000 * (j - 1)); // get time for current line
						String dateString = startDate.format(current);
						fw.write(dateString + ",");
						String[] line = lines[j].split(" "); // items in every csv line
						for (int k = 0; k < line.length; k++) {
							fw.write(line[k]);
							if (k != line.length - 1) // not last item
							{
								fw.write(",");
							}
						}
						fw.write("\r\n");
						fw.flush();
					}
					fw.close();
				}
				reader.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

		}
	}
}
