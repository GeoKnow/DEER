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

	/////////////// ****** Start of the main method ******//////////////////////

	/**
	 * @param args
	 * @throws ParseException
	 * @throws IOException
	 * @throws org.apache.lucene.queryParser.ParseException
	 */
	public static void main(String[] args)
			throws ParseException, IOException, org.apache.lucene.queryParser.ParseException {
		BasicConfigurator.configure();
		LuceneIndexingfromCSV indexfromcsv = new LuceneIndexingfromCSV("Nameofdirectory");
		try {
			indexfromcsv.createIndexFromCSV(csvFile, true);

		} catch (IOException e) {
			e.printStackTrace();
		}

		String result = indexfromcsv.getThestring("-26.8645202");

		System.out.println(" the keyword is found: " + result);
	}
	/////////////// ****** End of the main method ******/////////////////////////

	////////// +++++++++++++++++++++++++++++++++++++++++++++++++++++++////////////

	static String input_1 = "/home/abddatascienceadmin/Downloads/geo.csv";
	private static File csvFile = new File(input_1);

	private List<String> indexedFields = new ArrayList<String>();

	private IndexSearcher indexSearcher;
	private String NameOfindexDirectory;

	public static final String INDEX_DIRECTORY = "lucene-index";
	public static final Version LUCENE_VERSION = Version.LUCENE_36;

	String stor;

	private static Logger logger = LoggerFactory.getLogger(LuceneIndexingfromCSV.class);
	////////// +++++++++++++++++++++++++++++++++++++++++++++++++++++++////////////

	/**
	 * @param Name
	 */

	public LuceneIndexingfromCSV(String Name) {
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
		/////////////////// <-----------------*********************------------------------->////////////////////////

		IndexWriterConfig config = getIndexWriterConfig(createNewIndex);
		IndexWriter indexWriter = new IndexWriter(FSDirectory.open(new File(NameOfindexDirectory)), config);

		/////////////////// <-----------------*********************------------------------->////////////////////////

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
				for (int i = 0; i < nextLine.length; i++) {
					String s = nextLine[i];
					// System.out.println("print ss values : " + s + "<----->");

					// Skip empty values
					if (s.trim().equals(""))
						continue;

					else {
						String ss = s.toString();// + " " + street + " " + hausNumber + " " + lon +" " +lat;
						// str.add(s);

						System.out.println(" print this STRING str. ---->" + ss);
						d.add(new Field(headers.get(i), ss, Field.Store.YES, Field.Index.NOT_ANALYZED));

					}
				}
				indexWriter.addDocument(d);

			}

		}
		indexWriter.close();
		reader.close();
		logger.info("Done creating index.");

		IndexReader indexReader = IndexReader.open(FSDirectory.open(new File(NameOfindexDirectory)));
		indexSearcher = new IndexSearcher(indexReader);

		indexedFields.addAll(headers);
		System.out.println("print the headers " + headers);
	}

	/**
	 * @param streetName
	 * @return
	 * @throws ParseException
	 * @throws IOException
	 * @throws ParseException
	 * @throws org.apache.lucene.queryParser.ParseException
	 */
	public String getThestring(String streetName)
			throws ParseException, IOException, ParseException, org.apache.lucene.queryParser.ParseException {
		// StandardAnalyzer analyzer = new
		// StandardAnalyzer(LuceneIndexingfromCSV.LUCENE_VERSION);

		for (String field : indexedFields) {
			// QueryParser parser = new QueryParser(LuceneIndexingfromCSV.LUCENE_VERSION,
			// field.trim(), analyzer);
			// System.out.println("sho mw what is inside the indexedfield -----> " +
			// indexedFields);

			org.apache.lucene.search.Query query = null;
			Term term = new Term(field, streetName.trim());

			query = new FuzzyQuery(term, 0.9f);

			// query = parser.parse(streetName.trim());

			// TopDocs hits = searcher.search(query);

			TopScoreDocCollector collector = TopScoreDocCollector.create(100, true);
			indexSearcher.search(query, collector);

			ScoreDoc[] hits = collector.topDocs().scoreDocs;

			// if (hits.length == 0)
			// continue;
			/*
			 * if (hits[0].score >= 0.0f) { int docId = hits[0].doc; Document d =
			 * indexSearcher.doc(docId); String ss;//new ArrayList<Fieldable>(); ss=
			 * d.getFields().toString();
			 * System.out.println("Found match from Lucene for Steet:" + streetName +
			 * "! Document matching field: " + d.get(field));
			 * // @SuppressWarnings("deprecation")
			 * 
			 * //Field ss= d.getField(field); //ss.getIndexOptions();
			 * System.out.println(" print the value of this sssss -----> " + ss); return
			 * getThestring(streetName); }
			 */

			// System.out.println("Found " + hits.length + " hits.");
			for (int i = 0; i < hits.length; ++i) {
				int docId = hits[i].doc;
				System.out.println("Score: " + hits[i].score);
				Document d = indexSearcher.doc(docId);
				System.out.println(i + ". " + d.get(field));
				stor = d.get(field);
				// @SuppressWarnings("deprecation")
				@SuppressWarnings("deprecation")
				Field fieldOfStore = d.getField(field);
				System.out.println("print the fields of stor: " + fieldOfStore);
			}
		}
		return stor;
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
}
