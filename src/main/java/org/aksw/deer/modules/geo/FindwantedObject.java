package org.aksw.deer.modules.geo;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import uk.ac.shef.wit.simmetrics.similaritymetrics.JaccardSimilarity;

public class FindwantedObject {

	final String fileName = "src/main/resources/geo.tsv";

	int osmIdHeader = 0;
	int streetIdHeader = 1;
	int streetHeader = 2;
	int houseNumberHeader = 3;
	int lonHeader = 4;
	int latHeader = 5;
	double Minthreshold = 0.4;
	double dastanceThreshold = 0.988;

	protected static double D2R = Math.PI / 180;
	protected static double radius = 6367;

	public static void main(String[] args) {

		FindwantedObject find = new FindwantedObject();
		List<String> longLatValues = find.TSVfindLonLat("Nordheimsvegen 11A");

		if (!longLatValues.isEmpty()) {
			String latValue = longLatValues.get(0);
			String longValue = longLatValues.get(1);
			System.out.println(" the Long and Lat values " + " = " + latValue + " " + longValue);
		}
		List<String> AddressValues = find.TSVfindAddress("37.0111495","-6.5585288");
		if (!AddressValues.isEmpty()) {
			String AddrValue = AddressValues.get(0);
			System.out.println(" the address value " + AddrValue);
		}
	}

	/**
	 * @param StreetObject
	 * @return
	 */
	public List<String> TSVfindLonLat(String StreetObject) {

		BufferedReader reader = null;
		List<String> result = new ArrayList<>();
		try {
			reader = new BufferedReader(new FileReader(fileName));
			String line = reader.readLine(); // skip header
			try {
				while ((line = reader.readLine()) != null) {
					String[] entryArray = line.split("\t");
					JaccardSimilarity JaccardSim = new JaccardSimilarity();
					String secondString = entryArray[streetHeader] + " " + entryArray[houseNumberHeader];
					double similarityThreshold = JaccardSim.getSimilarity(secondString, StreetObject);
					if (similarityThreshold >= Minthreshold) {
						result.add(entryArray[lonHeader]);
						result.add(entryArray[latHeader]);
					}
				}

			} finally {
				reader.close();
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return result;

	}

	/**
	 * @param Long
	 * @param Lat
	 * @return
	 */
	public List<String> TSVfindAddress(String Lat, String Long) {

		BufferedReader reader = null;
		List<String> result = new ArrayList<>();
		try {
			reader = new BufferedReader(new FileReader(fileName));
			String line = reader.readLine();
			try {
				while ((line = reader.readLine()) != null) {

					String[] entryArray = line.split("\t");
					double Lat_DB = Double.parseDouble(entryArray[latHeader]);
					double Long_DB = Double.parseDouble(entryArray[lonHeader]);

					double Lat_Rdf = Double.parseDouble(Lat);
					double Long_Rdf = Double.parseDouble(Long);

					double d = distance(Lat_Rdf, Long_Rdf, Lat_DB, Long_DB);
					double error = 1 / (1 + d);
				//	System.out.println(" the error = " + error);

					if (error >= dastanceThreshold)
						result.add(entryArray[streetHeader]);
				}

			} finally {
				reader.close();
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return result;

	}

	/**
	 * @param lat1
	 * @param lon1
	 * @param lat2
	 * @param lon2
	 * @return
	 */
	public static double distance(double lat1, double lon1, double lat2, double lon2) {

		double value1 = Math.pow(Math.sin((lat1 - lat2) / 2.0) * D2R, 2)
				+ Math.cos(lat1 * D2R) * Math.cos(lat2 * D2R) * Math.pow(Math.sin((lon1 - lon2) / 2.0) * D2R, 2);
		double c = 2 * Math.atan2(Math.sqrt(value1), Math.sqrt(1 - value1));
		double d = radius * c;
		return d;
	}

}
