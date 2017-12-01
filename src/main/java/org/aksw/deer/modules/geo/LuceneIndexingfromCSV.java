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



public class LuceneIndexingfromCSV {

	private static Logger logger = LoggerFactory.getLogger(LuceneIndexingfromCSV.class);

	static String input_1 = "/home/abddatascienceadmin/Downloads/geoconvert.csv";
	public static File csvFile = new File(input_1);
	//private List<String> indexedFields = new ArrayList<String>();
	private IndexSearcher indexSearcher;
	private String NameOfindexDirectory;
	
	List<Double> latDb=new ArrayList<Double>();
	List<Double> lonDb=new ArrayList<Double>();
	private Double distance;
	
	protected static double D2R = Math.PI / 180;
	protected static double radius = 6367;
	protected static double dastanceThreshold = 0.98;

	
	public static final String INDEX_DIRECTORY = "lucene-index";
	public static final Version LUCENE_VERSION = Version.LUCENE_36;

	//String stor;

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

		List<String> result = indexfromcsv.getLanandLon("SectorIElinglesillo 12");
		//System.out.println(" the latValue is found: " + result.get(0));
		//System.out.println(" the lonValue is found: " + result.get(1));

		String result_2= indexfromcsv.getStreetadress("-26.8178935","-49.1116889" );

		System.out.println(" the street is found: " + result_2);
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

		/*		indexedFields.addAll(headers);
		System.out.println("print the headers " + headers);*/
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

		query = new FuzzyQuery(term, 0.94f);

		// query = parser.parse(streetName.trim());
		// TopDocs hits = searcher.search(query);

		TopScoreDocCollector collector = TopScoreDocCollector.create(1, true);
		indexSearcher.search(query, collector);

		ScoreDoc[] hits = collector.topDocs().scoreDocs;

		for (int i = 0; i < hits.length; ++i) {
			int docId = hits[i].doc;
			//System.out.println("Score: " + hits[i].score);
			Document d = indexSearcher.doc(docId);
			//System.out.println(i + ". " + d.get("streetPos"));
			String streetValue = d.get("streetPos");
			if(!streetValue.isEmpty());

			String latValue = d.get("latPos");
			String lonValue = d.get("lonPos");
			arr.add(0,latValue);
			arr.add(1,lonValue);

			//System.out.println("Print the latValue: "+ arr[0]);
			//System.out.println("Print the lonValue: "+ arr[0]);

		}
		//}
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
		String streetValue=null;
		org.apache.lucene.search.Query query = null;
		
		double Lat_Rdf = Double.parseDouble(lat);
		double Lon_Rdf = Double.parseDouble(lon);
		Term term = new Term("latPos", lat.trim());
          String xx= term.text();
          System.out.println(" print the term = "+ xx);
		query = new FuzzyQuery(term, 0.7f);

		// query = parser.parse(streetName.trim());
		// TopDocs hits = searcher.search(query);

		TopScoreDocCollector collector = TopScoreDocCollector.create(4, true);
		indexSearcher.search(query, collector);

		ScoreDoc[] hits = collector.topDocs().scoreDocs;

		for (int i = 0; i < hits.length; ++i) {
			int docId = hits[i].doc;
			//System.out.println("Score: " + hits[i].score);
			Document d = indexSearcher.doc(docId);
			//System.out.println(i + ". " + d.get("latPos"));
			String latValue = d.get("latPos");
                   List<String> acumlatValue =new ArrayList<String>();
                   acumlatValue.add(latValue);
                   
           		for (String temp : acumlatValue) {
        			//System.out.println(temp);
        			latDb.add(Double.parseDouble(temp));
        			System.out.println("Print the latValue acuumolator: "+ latDb);
        		}
			//System.out.println("Print the latValue acuumolator: "+ latDb);

			if(!latValue.isEmpty());
			Term term_1 = new Term("lonPos", lon.trim());

			query = new FuzzyQuery(term_1, 0.7f);

			String lonValue=d.get("lonPos");
		     List<String> acumlonValue =new ArrayList<String>();
             acumlonValue.add(lonValue);
             
        		for (String temp : acumlonValue) {
     			//System.out.println(temp);
     			lonDb.add(Double.parseDouble(temp));
    			System.out.println("Print the lontValue acuumolator: "+ lonDb);

     		}
             
			if(!latValue.isEmpty()&& !lonValue.isEmpty());
			if(latDb.size()==lonDb.size()) {
				for (int k = 0; k < latDb.size(); k++) {
				
				
			 distance= distance( Lat_Rdf,  Lon_Rdf, latDb.get(k ),lonDb.get(k));}
				double error = 1 / (1 + distance);
				System.out.println(" the DISTANCE = " + distance + " AND "+ "the ERROR = "+ error);
				if (error >= dastanceThreshold)
			streetValue= d.get("streetPos");
				System.out.println("Print the StreetValue: "+ streetValue );
			}
			//System.out.println("Print the StreetValue: "+ streetValue );


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
