package org.aksw.deer.modules.geo;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Scanner;

public class TSVToCSVconverter {

	static String input_1 = "/home/abddatascienceadmin/Downloads/Geo_Data/planet-latest_housenumbers.tsv";
	static String output_1 = "/home/abddatascienceadmin/Downloads/Geo_Data/geosample.csv";


	static String Osm_id;
	static String Street_id;
	static String StreetName;
	static String HausNumber;
	static String Lon;
	static String Lat;
	static String csvMerge;


	// @SuppressWarnings("resource")
	public static void main(String[] args) throws IOException {

		TSVToCSVconverter tsvTOcsv = new TSVToCSVconverter();
		tsvTOcsv.getInputOutputfile(input_1, output_1);

	}

	@SuppressWarnings("resource")
	private void getInputOutputfile(String input, String output) throws IOException {

		FileWriter writer = null;
		File file_1 = new File(input);
		File file_2 = new File(output);

		Scanner scan = new Scanner(file_1);
		file_1.createNewFile();
		writer = new FileWriter(file_2);

		while (scan.hasNext()) {

			String csvFormat= scan.nextLine().replace("\t", ",");

			String[] tokens = csvFormat.split(",");

			for (int i=0;i<=tokens.length; i++) {
				if(tokens.length!=1&&tokens.length!=2&&tokens.length!=3&&tokens.length!=4&&tokens.length!=5) {

					Osm_id     = tokens[tokens.length-6];
					Street_id  = tokens[tokens.length-5];
					StreetName = tokens[tokens.length-4];
					HausNumber = tokens[tokens.length-3];
					Lon        = tokens[tokens.length-2];
					Lat        = tokens[tokens.length-1];}

			}

			csvMerge= Osm_id +","+ Street_id+","+StreetName+" "+ HausNumber+","+Lon+","+Lat;

			writer.append(csvMerge);

			//System.out.println("the converting from TSV to CVS done <-____->");
			writer.append("\n");
			writer.flush();
			// writer.close();
			// scan.close();
		}
		System.out.println("the converting from TSV to CVS done <-____->");
	}



}