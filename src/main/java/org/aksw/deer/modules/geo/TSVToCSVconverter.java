package org.aksw.deer.modules.geo;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Scanner;

public class TSVToCSVconverter {

	static String input_1 = "/home/abddatascienceadmin/Downloads/geo.tsv";
	static String output_1 = "/home/abddatascienceadmin/Downloads/geoconverted.csv";

	// @SuppressWarnings("resource")
	public static void main(String[] args) throws IOException {

		TSVToCSVconverter tsvTOcsv = new TSVToCSVconverter();
		tsvTOcsv.getInputOutputfile(input_1, output_1);
		/*
		 * FileWriter writer = null; File file = new
		 * File("/home/abddatascienceadmin/Downloads/geo.tsv"); Scanner scan = new
		 * Scanner(file); File file2 = new
		 * File("/home/abddatascienceadmin/Downloads/geoconverted.csv");
		 * file.createNewFile(); writer = new FileWriter(file2);
		 * 
		 * while (scan.hasNext()) { String csv = scan.nextLine().replace("\t", ",");
		 * System.out.println(csv); writer.append(csv); writer.append("\n");
		 * writer.flush(); writer.close(); scan.close();
		 * 
		 * }
		 */
	}

	@SuppressWarnings({ "resource" })
	private void getInputOutputfile(String input, String output) throws IOException {

		FileWriter writer = null;
		File file_1 = new File(input);
		File file_2 = new File(output);

		Scanner scan = new Scanner(file_1);
		file_1.createNewFile();
		writer = new FileWriter(file_2);

		while (scan.hasNext()) {
			String csvFormat= scan.nextLine().replace("\t", ",");
			System.out.println(csvFormat);
			writer.append(csvFormat);
			//System.out.println("the converting from TSV to CVS done <-____->");
			writer.append("\n");
			writer.flush();
			// writer.close();
			// scan.close();
		}
		System.out.println("the converting from TSV to CVS done <-____->");
	}
}