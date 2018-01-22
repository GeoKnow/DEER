package org.aksw.deer.modules.geo;

import java.io.IOException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.aksw.deer.json.ParameterType;
import org.aksw.deer.modules.DeerModule;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;
import org.apache.jena.rdf.model.Property;
import org.apache.jena.rdf.model.RDFNode;
import org.apache.jena.rdf.model.Resource;
import org.apache.jena.rdf.model.ResourceFactory;
import org.apache.jena.rdf.model.Statement;
import org.apache.jena.rdf.model.StmtIterator;
import org.apache.log4j.Logger;

public class GeoLocatorModule implements DeerModule {

	private static final Logger logger = Logger.getLogger(GeoLocatorModule.class);

	public static final String INPUT_ADDRESS_PROPERTY            = "inputaddressproperty"; 
	public static final String INPUT_LAT_PROPERTY                = "inputlatproperty";
	public static final String INPUT_LONG_PROPERTY               = "inputlongproperty";

	public static final String OUTPUT_ADDRESS_PROPERTY           = "outputaddressproperty"; 
	public static final String OUTPUT_LAT_PROPERTY               = "outputlatproperty";
	public static final String OUTPUT_LONG_PROPERTY              = "outputlongproperty";

	LuceneIndexingfromCSV luceneFinder=new LuceneIndexingfromCSV("nameofdir");


	@Override
	public Model process(Model model, Map<String, String> parameters) {
		if (parameters.containsKey(INPUT_ADDRESS_PROPERTY)) {
			Model resultModel;
			try {
				resultModel = findLongLat(model, parameters.get(INPUT_ADDRESS_PROPERTY));
				return model.add(resultModel);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

		}
		if (parameters.containsKey(INPUT_LAT_PROPERTY) && parameters.containsKey(INPUT_LONG_PROPERTY)) {
			Model resultModel = null;
			try {
				resultModel = findAddress(model, parameters.get(INPUT_LAT_PROPERTY),
						parameters.get(INPUT_LONG_PROPERTY));
			} catch (ParseException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			return model.add(resultModel);
		}
		return model;
	}

	/**
	 * @param model
	 * @param inputLatPropName
	 * @param inputLongPropName
	 * @return model
	 * @throws org.apache.lucene.queryParser.ParseException 
	 * @throws IOException 
	 * @throws ParseException 
	 * 
	 */
	//@SuppressWarnings("null")
	private Model findAddress(Model model, String inputLatPropName, String inputLongPropName) throws ParseException, IOException {

		Property inputLatProp = ResourceFactory.createProperty(inputLatPropName);
		StmtIterator iter_Lat = model.listStatements(null, inputLatProp, (RDFNode) null);

		double totalLuceneTime = 0d;
		while (iter_Lat.hasNext()) {

			Statement stmt = iter_Lat.nextStatement(); 
			Resource subject = stmt.getSubject(); 
			RDFNode object = stmt.getObject();


			//System.out.println("the object is "+object);
			//System.out.println("the subjecct is "+subject);

			double ourObjectLat=object.asLiteral().getDouble();

			Property inputLongProp = ResourceFactory.createProperty(inputLongPropName);
			StmtIterator objectLatItr = subject.listProperties(inputLongProp);

			//String values_Adress = "";// new ArrayList<>();
			Statement nextStmt= objectLatItr.next();
			RDFNode nextObject=nextStmt.getObject();

			double ourObjectLong=nextObject.asLiteral().getDouble();

			//System.out.println(" the lat object: "+ ourObjectLat+" the long object: "+ourObjectLong);
			try {

				long start = System.currentTimeMillis();

				String values_Adress=luceneFinder.getStreetadress(Double.toString(ourObjectLat), Double.toString(ourObjectLong));

				double luceneTime = (System.currentTimeMillis() - start) / 60000.0;
				System.out.println("Lucene time = " + luceneTime);
				totalLuceneTime += luceneTime;
				System.out.println("totsl Lucene time so far = " + totalLuceneTime);

				if (! values_Adress.equals("")) {

					//if(values_Adress!="") {
					RDFNode NewObjectToAddOfLong = ResourceFactory. createStringLiteral(values_Adress);
					Property outputLongProp = ResourceFactory.createProperty(OUTPUT_ADDRESS_PROPERTY);
					model.add(subject, outputLongProp, NewObjectToAddOfLong);
				}
				//if(values_Adress.isEmpty());

				System.out.println(" the obtained Street= "+ values_Adress);

			} catch (org.apache.lucene.queryParser.ParseException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

		}
		return model;
	}

	private Model findLongLat(Model model, String inputAddressPropertyName) throws IOException {
		//Model newModel= ModelFactory.createDefaultModel() ;
		Property inputAddressProp = ResourceFactory.createProperty(inputAddressPropertyName);
		StmtIterator iter = model.listStatements(null, inputAddressProp, (RDFNode) null);

		double totalLuceneTime = 0d;

		while (iter.hasNext()) {
			Statement stmt = iter.nextStatement();
			Resource subject = stmt.getSubject();
			RDFNode object = stmt.getObject();

			String OurObjectAdress = object.asLiteral().toString();
			List<String> addressValues = null;

			try {

				long start = System.currentTimeMillis();
				addressValues = luceneFinder.getLanandLon(OurObjectAdress);
				double luceneTime = (System.currentTimeMillis() - start) / 60000.0;
				System.out.println("Lucene time = " + luceneTime);
				totalLuceneTime += luceneTime;
				System.out.println("totsl Lucene time so far = " + totalLuceneTime);

				System.out.println(" the values of address= "+ addressValues);
			} catch (ParseException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (org.apache.lucene.queryParser.ParseException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			for (String Iteration : addressValues) {
				model= ModelFactory.createDefaultModel() ;
				RDFNode newObjectToAddOfAddress = ResourceFactory.createStringLiteral(Iteration);
				Property outputLatAddress = ResourceFactory.createProperty(OUTPUT_LAT_PROPERTY);
				model.add(subject, outputLatAddress, newObjectToAddOfAddress);

				Property outputLongtAddress = ResourceFactory.createProperty(OUTPUT_LONG_PROPERTY);


				System.out.println(" the model is -----> "+ model.toString());
			}
			//model;
		}

		return model;
	}

	@Override
	public List<String> getParameters() {

		List<String> parameters = new ArrayList<String>();
		parameters.add(INPUT_ADDRESS_PROPERTY);
		parameters.add(INPUT_LAT_PROPERTY);
		parameters.add(INPUT_LONG_PROPERTY);
		parameters.add(OUTPUT_ADDRESS_PROPERTY);
		parameters.add(OUTPUT_LAT_PROPERTY);
		parameters.add(OUTPUT_LONG_PROPERTY);
		// parameters.add("useCache");
		return parameters;
		// TODO Auto-generated method stub

	}

	@Override
	public List<String> getNecessaryParameters() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Map<String, String> selfConfig(Model source, Model target) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<ParameterType> getParameterWithTypes() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Resource getType() {
		// TODO Auto-generated method stub
		return null;
	}
	public static void main(String[] args) {}
}
