package org.aksw.deer.modules.geo;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.BasicConfigurator;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.index.Term;
import org.apache.lucene.search.FuzzyQuery;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TopScoreDocCollector;
import org.apache.lucene.store.FSDirectory;
import org.apache.lucene.util.Version;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import au.com.bytecode.opencsv.CSVReader;



/**
 * 
 * the Vincenty formula is more accurate than Great Circle formula, 
 * and this is very important when 
 * the distance between points is very short,e.g. in our case
 * the Threshold can be updated as follow: threshold >= 1-(k/100km), k is integer number with rang [1,100], 
 * iff k= 1, it means all distances less than one Km can pass.
 * 
 * 
 * @author Abdullah Ahmed
 *
 */
public class LuceneIndexingfromCSV {


	private static Logger logger = LoggerFactory.getLogger(LuceneIndexingfromCSV.class);

	static String input_1 = "/home/abddatascienceadmin/Downloads/geoconvert.csv";
	public static File csvFile = new File(input_1);

	//private List<String> indexedFields = new ArrayList<String>();
	private IndexSearcher indexSearcher;
	private String NameOfindexDirectory;

	private Double distance;

	protected static double D2R = Math.PI / 180;
	protected static double radius = 6367;
	protected static double dastanceThreshold = 0.99;

	public static final String INDEX_DIRECTORY = "lucene-index";
	public static final Version LUCENE_VERSION = Version.LUCENE_36;

	List<Double> latDb=new ArrayList<Double>();
	List<Double> lonDb=new ArrayList<Double>();


	/**
	 * @param args
	 * @throws ParseException
	 * @throws IOException
	 * @throws org.apache.lucene.queryParser.ParseException
	 */
	public static void main(String[] args) throws ParseException, IOException, org.apache.lucene.queryParser.ParseException {

		BasicConfigurator.configure();

		LuceneIndexingfromCSV indexfromcsv = new LuceneIndexingfromCSV("Nameofdirectory");

		try {
			indexfromcsv.createIndexFromCSV(csvFile, true);

		} catch (IOException e) {
			e.printStackTrace();
		}

	}

	/**
	 * @param Name
	 */

	LuceneIndexingfromCSV(String Name) {
		NameOfindexDirectory = INDEX_DIRECTORY + "_" + Name;
		try {
			if (!new File(NameOfindexDirectory).exists()) {
				new File(NameOfindexDirectory).mkdir();
			} else {
				IndexReader indexReader = IndexReader.open(FSDirectory.open(new File(NameOfindexDirectory)));
				indexSearcher = new IndexSearcher(indexReader);
			}
			logger.info("Lucene index location: " + new File(NameOfindexDirectory).getAbsolutePath());
		} catch (IOException e) {
			logger.error("Error occured while attempting to setup index reader.", e);
		}
	}

	/**
	 * @param csvFile
	 * @param createNewIndex
	 * @throws IOException
	 */
	public void createIndexFromCSV(File csvFile, boolean createNewIndex) throws IOException {
		if (!csvFile.exists()) {
			throw new FileNotFoundException("CSV file not found: " + csvFile);
		}

		IndexWriterConfig config = getIndexWriterConfig(createNewIndex);
		IndexWriter indexWriter = new IndexWriter(FSDirectory.open(new File(NameOfindexDirectory)), config);

		CSVReader reader = new CSVReader(new InputStreamReader(new FileInputStream(csvFile), "UTF-8"));
		String[] nextLine;
		int lineIndex = 0;
		List<String> headers = new ArrayList<String>();
		while ((nextLine = reader.readNext()) != null) {
			lineIndex++;
			// Check the first line
			if (lineIndex == 1) {
				for (String s : nextLine)
					headers.add(s);

			}

			else {
				if (nextLine.length != headers.size())
					continue;
				Document d = new Document();
				String latPos = nextLine[nextLine.length-1];
				String lonPos = nextLine[nextLine.length-2];
				String hausNumberPos = nextLine[nextLine.length-3];
				String streetPos = nextLine[nextLine.length-4];

				String StreetAndHaus= streetPos+ " " + hausNumberPos;

				d.add(new Field("latPos", latPos, Field.Store.YES, Field.Index.NOT_ANALYZED));
				d.add(new Field("lonPos", lonPos, Field.Store.YES, Field.Index.NOT_ANALYZED));
				d.add(new Field("streetPos", StreetAndHaus, Field.Store.YES, Field.Index.NOT_ANALYZED));

				//}
				//}
				indexWriter.addDocument(d);

			}

		}
		indexWriter.close();
		reader.close();
		logger.info("creating index is done.");

		IndexReader indexReader = IndexReader.open(FSDirectory.open(new File(NameOfindexDirectory)));
		indexSearcher = new IndexSearcher(indexReader);

	}

	/**
	 * @param streetName
	 * @return
	 * @throws ParseException
	 * @throws IOException
	 * @throws ParseException
	 * @throws org.apache.lucene.queryParser.ParseException
	 */

	public List<String> getLanandLon(String streetName)
			throws ParseException, IOException, ParseException, org.apache.lucene.queryParser.ParseException {
		List<String> arr = new ArrayList<String>(2);
		org.apache.lucene.search.Query query = null;
		Term term = new Term("streetPos", streetName.trim());

		query = new FuzzyQuery(term, 0.5f);

		TopScoreDocCollector collector = TopScoreDocCollector.create(1, true);
		indexSearcher.search(query, collector);

		ScoreDoc[] hits = collector.topDocs().scoreDocs;

		for (int i = 0; i < hits.length; ++i) {

			int docId = hits[i].doc;
			Document d = indexSearcher.doc(docId);
			String streetValue = d.get("streetPos");

			if(!streetValue.isEmpty()) {
				String latValue = d.get("latPos");
				String lonValue = d.get("lonPos");
				arr.add(0,latValue);
				arr.add(1,lonValue);
			}

		}
		return arr;
	}

	/**
	 * @param lat
	 * @param lon
	 * @return
	 * @throws ParseException
	 * @throws IOException
	 * @throws ParseException
	 * @throws org.apache.lucene.queryParser.ParseException
	 */


	public String getStreetadress(String lat, String lon)

			throws ParseException, IOException, ParseException, org.apache.lucene.queryParser.ParseException {

		String streetValue="";
		org.apache.lucene.search.Query query = null;

		double Lat_Rdf = Double.parseDouble(lat);
		double Lon_Rdf = Double.parseDouble(lon);

		Term term = new Term("latPos", lat.trim());
		query = new FuzzyQuery(term, 0.7f);

		TopScoreDocCollector collector = TopScoreDocCollector.create(4, true);
		indexSearcher.search(query, collector);

		ScoreDoc[] hits = collector.topDocs().scoreDocs;

		for (int i = 0; i < hits.length; ++i) {
			int docId = hits[i].doc;
			Document d = indexSearcher.doc(docId);
			String latValue = d.get("latPos");
			List<String> acumlatValue =new ArrayList<String>();
			acumlatValue.add(latValue);

			for (String temp : acumlatValue) {
				latDb.add(Double.parseDouble(temp));
			}

			if(!latValue.isEmpty());
			Term term_1 = new Term("lonPos", lon.trim());
			query = new FuzzyQuery(term_1, 0.7f);

			String lonValue=d.get("lonPos");
			List<String> acumlonValue =new ArrayList<String>();
			acumlonValue.add(lonValue);

			for (String temp : acumlonValue) {

				lonDb.add(Double.parseDouble(temp));
				System.out.println("Print the lontValue acuumolator: "+ lonDb);
			}

			if(!latValue.isEmpty()&& !lonValue.isEmpty());
			if(latDb.size()==lonDb.size()) {

				for (int k = 0; k < latDb.size(); k++) {


					distance= distanceGreatCircle( Lat_Rdf,  Lon_Rdf, latDb.get(k ),lonDb.get(k));
				}

				double error = 1 / (1 + distance);
				System.out.println(" the DISTANCE = " + distance + " AND "+ "the ERROR = "+ error);
				if (error >= dastanceThreshold)
					streetValue= d.get("streetPos");

				//System.out.println("Print the StreetValue: "+ streetValue );
			}


		}
		return streetValue;
	}

	/**
	 * @param createNewIndex
	 * @return
	 */

	private IndexWriterConfig getIndexWriterConfig(boolean createNewIndex) {
		IndexWriterConfig config = new IndexWriterConfig(LuceneIndexingfromCSV.LUCENE_VERSION,
				new StandardAnalyzer(LuceneIndexingfromCSV.LUCENE_VERSION));
		if (createNewIndex)
			config.setOpenMode(IndexWriterConfig.OpenMode.CREATE);
		else
			config.setOpenMode(IndexWriterConfig.OpenMode.APPEND);
		return config;
	}


	/**
	 * this method is the implementation of Great Circle formula 
	 * @param lat1
	 * @param lon1
	 * @param lat2
	 * @param lon2
	 * @return
	 */

	public static double distanceGreatCircle(double lat1, double lon1, double lat2, double lon2) {

		double value1 = Math.pow(Math.sin((lat1 - lat2) / 2.0) * D2R, 2)
				+ Math.cos(lat1 * D2R) * Math.cos(lat2 * D2R) * Math.pow(Math.sin((lon1 - lon2) / 2.0) * D2R, 2);
		double c = 2 * Math.atan2(Math.sqrt(value1), Math.sqrt(1 - value1));
		double distanceGreatCircleMeasure = radius * c;
		return distanceGreatCircleMeasure;
	}



	/**
	 * this method is the implementation of  Vincenty formula
	 * 
	 * @param lat1
	 * @param lon1
	 * @param lat2
	 * @param lon2
	 * @return
	 */

	public static double distanceVincenty(double lat1, double lon1, double lat2, double lon2) {

		double a = 6378137, b = 6356752.314245, f = 1 / 298.257223563; 
		double L = Math.toRadians(lon2 - lon1);
		double U1 = Math.atan((1 - f) * Math.tan(Math.toRadians(lat1)));
		double U2 = Math.atan((1 - f) * Math.tan(Math.toRadians(lat2)));
		double sinU1 = Math.sin(U1), cosU1 = Math.cos(U1);
		double sinU2 = Math.sin(U2), cosU2 = Math.cos(U2);

		double sinLambda, cosLambda, sinSigma, cosSigma, sigma, sinAlpha, cosSqAlpha, cos2SigmaM;
		double lambda = L, lambdaP, iterLimit = 100;

		do {
			sinLambda = Math.sin(lambda);
			cosLambda = Math.cos(lambda);
			sinSigma = Math.sqrt((cosU2 * sinLambda) * (cosU2 * sinLambda)
					+ (cosU1 * sinU2 - sinU1 * cosU2 * cosLambda) * (cosU1 * sinU2 - sinU1 * cosU2 * cosLambda));

			if (sinSigma == 0)
				return 0; 

			cosSigma = sinU1 * sinU2 + cosU1 * cosU2 * cosLambda;
			sigma = Math.atan2(sinSigma, cosSigma);
			sinAlpha = cosU1 * cosU2 * sinLambda / sinSigma;
			cosSqAlpha = 1 - sinAlpha * sinAlpha;
			cos2SigmaM = cosSigma - 2 * sinU1 * sinU2 / cosSqAlpha;

			if (Double.isNaN(cos2SigmaM))
				cos2SigmaM = 0; 
			double C = f / 16 * cosSqAlpha * (4 + f * (4 - 3 * cosSqAlpha));
			lambdaP = lambda;
			lambda = L + (1 - C) * f * sinAlpha
					* (sigma + C * sinSigma * (cos2SigmaM + C * cosSigma * (-1 + 2 * cos2SigmaM * cos2SigmaM)));

		} 
		while (Math.abs(lambda - lambdaP) > 1e-12 && --iterLimit > 0);

		if (iterLimit == 0)
			return Double.NaN; 

		double uSq = cosSqAlpha * (a * a - b * b) / (b * b);
		double A = 1 + uSq / 16384 * (4096 + uSq * (-768 + uSq * (320 - 175 * uSq)));
		double B = uSq / 1024 * (256 + uSq * (-128 + uSq * (74 - 47 * uSq)));
		double deltaSigma = B
				* sinSigma
				* (cos2SigmaM + B
						/ 4
						* (cosSigma * (-1 + 2 * cos2SigmaM * cos2SigmaM) - B / 6 * cos2SigmaM
								* (-3 + 4 * sinSigma * sinSigma) * (-3 + 4 * cos2SigmaM * cos2SigmaM)));
		double distanceVincentyMeasure = b * A * (sigma - deltaSigma);

		return distanceVincentyMeasure;
	}
}
