package org.aksw.deer.modules.geo;

import java.io.IOException;
import java.util.ArrayList;

import org.aksw.deer.io.Reader;
import org.aksw.deer.io.Writer;
import org.apache.jena.rdf.model.Literal;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;
import org.apache.jena.rdf.model.Property;
import org.apache.jena.rdf.model.RDFNode;
import org.apache.jena.rdf.model.Resource;
import org.apache.jena.rdf.model.ResourceFactory;
import org.apache.jena.rdf.model.Statement;
import org.apache.jena.rdf.model.StmtIterator;

public class ModelDistortion {

	static ArrayList<String> allObjectsOFstreet=new ArrayList<String>();
	static String OurObjectStreet;

	static ArrayList<String> allSubject=new ArrayList<String>();
	static String subjectofModel;

	static ArrayList<String> allsubjectOFstreet=new ArrayList<String>();
	static String OurSubjectStreet;

	static ArrayList<String> allsubjectOFlat=new ArrayList<String>();
	static String OurSubjectlat;

	static ArrayList<String> allsubjectOFlon=new ArrayList<String>();
	static String OurSubjectlon;

	static ArrayList<Literal> allObjectsOFlat=new ArrayList<Literal>();
	static Literal OurObjectLat;

	static ArrayList<Literal> allObjectsOFlon=new ArrayList<Literal>();
	static Literal OurObjectLon;

	boolean user = true;


	/**
	 * @param input
	 * @param destortionSize
	 * @param degreeofMut
	 * @return
	 */
	private Model modeldistor(Model input, int destortionSize,double degreeofMut) {


		Model newModel= ModelFactory.createDefaultModel() ;
		Model newModel2= ModelFactory.createDefaultModel() ;
		Property street = ResourceFactory.createProperty("http://ex.org/a#street");
		Property lon = ResourceFactory.createProperty("http://ex.org/a#lon");
		Property lat = ResourceFactory.createProperty("http://ex.org/a#lat");

		StmtIterator iter_street= input.listStatements();

		while (iter_street.hasNext()) {

			Statement stmt = iter_street.nextStatement(); 
			Resource subject = stmt.getSubject(); 
			Property property= stmt.getPredicate();
			RDFNode object = stmt.getObject();

			//subjectofModel=subject.asResource().toString();


			if(property.toString().contains("a#street")) {
				OurObjectStreet = object.asLiteral().toString();
				allObjectsOFstreet.add(OurObjectStreet);

				OurSubjectStreet=subject.asResource().toString();
				allsubjectOFstreet.add(OurSubjectStreet);

			}


			if(property.toString().contains("a#lat")) {
				OurObjectLat = object.asLiteral();
				allObjectsOFlat.add(OurObjectLat);

				OurSubjectlat=subject.asResource().toString();
				allsubjectOFlat.add(OurSubjectlat);
			}

			if(property.toString().contains("a#lon")) {
				OurObjectLon = object.asLiteral();
				allObjectsOFlon.add(OurObjectLon);

				OurSubjectlon=subject.asResource().toString();
				allsubjectOFlon.add(OurSubjectlon);
			}

		}

		allSubject.add(subjectofModel);

		if(user==false) {

			for (int counter = 0; counter <(allsubjectOFstreet.size())/destortionSize; counter++) { 		      


				String newString=swap(allObjectsOFstreet.get(counter),degreeofMut);
				RDFNode NewObjectToAddOfStreet = ResourceFactory.createStringLiteral(newString);
				//RDFNode NewObjectToAddOflat = ResourceFactory.createStringLiteral(allObjectsOFlat.get(counter));
				//RDFNode NewObjectToAddOflon = ResourceFactory.createStringLiteral(allObjectsOFlon.get(counter));
				Resource newSubject= ResourceFactory.createResource(allsubjectOFstreet.get(counter));


				newModel.add(newSubject, street, NewObjectToAddOfStreet);
				//newModel.add(newSubject, lon, NewObjectToAddOflon);
				//newModel.add(newSubject, lat, NewObjectToAddOflat);

			}

			for (int counter = (allsubjectOFstreet.size())/destortionSize; counter <allsubjectOFstreet.size()-1; counter++) { 		      

				RDFNode NewObjectToAddOfStreet = ResourceFactory.createStringLiteral(allObjectsOFstreet.get(counter));
				//RDFNode NewObjectToAddOflon = ResourceFactory.createStringLiteral(allObjectsOFlon.get(counter));
				//RDFNode NewObjectToAddOflat = ResourceFactory.createStringLiteral(allObjectsOFlat.get(counter));

				Resource newSubject= ResourceFactory.createResource(allsubjectOFstreet.get(counter));

				newModel2.add(newSubject, street, NewObjectToAddOfStreet);
				//newModel2.add(newSubject, lon, NewObjectToAddOflon);
				//newModel2.add(newSubject, lat, NewObjectToAddOflat);

				newModel2.add(newModel);
			}

		}
		else if(user==true) {



			for (int counter = 0; counter <(allsubjectOFlon.size())/destortionSize; counter++) { 		      

				//System.out.println("the object values is = "+ allObjectsOFlat.get(counter).getDouble());

				double newDoublelon=lonmodification(allObjectsOFlon.get(counter).getDouble(),degreeofMut);
				RDFNode NewObjectToAddOflon = ResourceFactory.createTypedLiteral(newDoublelon);

				double newDoublelat=latmodification(allObjectsOFlat.get(counter).getDouble(),degreeofMut);
				RDFNode NewObjectToAddOflat = ResourceFactory.createTypedLiteral(newDoublelat);

				//RDFNode NewObjectToAddOfStreet = ResourceFactory.createStringLiteral(newString);

				Resource newSubject= ResourceFactory.createResource(allsubjectOFlon.get(counter));


				//newModel.add(newSubject, street, NewObjectToAddOfStreet);
				newModel.add(newSubject, lon, NewObjectToAddOflon);
				newModel.add(newSubject, lat, NewObjectToAddOflat);

			}

			for (int counter = (allsubjectOFlon.size())/destortionSize; counter <allsubjectOFlon.size()-1; counter++) { 		      

				//RDFNode NewObjectToAddOfStreet = ResourceFactory.createStringLiteral(allObjectsOFstreet.get(counter));
				RDFNode NewObjectToAddOflon = ResourceFactory.createTypedLiteral(allObjectsOFlon.get(counter).getDouble());
				RDFNode NewObjectToAddOflat = ResourceFactory.createTypedLiteral(allObjectsOFlat.get(counter).getDouble());

				Resource newSubject= ResourceFactory.createResource(allsubjectOFlon.get(counter));

				//newModel2.add(newSubject, street, NewObjectToAddOfStreet);
				newModel2.add(newSubject, lon, NewObjectToAddOflon);
				newModel2.add(newSubject, lat, NewObjectToAddOflat);

				newModel2.add(newModel);
			}
		}
		//newModel.difference(input);
		return newModel2;}


	/**
	 * @param str
	 * @param degreeofMut
	 * @return
	 */
	private String swap(String str, double degreeofMut) {

		char[] c = str.toCharArray();
		double pos_1=degreeofMut+1;
		double pos_2=degreeofMut+2;

		if(pos_1<str.length()&&pos_2<str.length()){

			char temp = c[(int) pos_1];
			c[(int) pos_1] = c[(int) pos_2];
			c[(int) pos_2] = temp;
		}
		String swappedString = new String(c);

		return swappedString;
	}


	/**
	 * @param lat
	 * @param degreeofMut
	 * @return
	 */
	private double latmodification(double lat,double degreeofMut) {

		double modifiedLat=lat+degreeofMut;

		return modifiedLat;

	}

	/**
	 * @param lon
	 * @param degreeofMut
	 * @return
	 */
	private double lonmodification(double lon, double degreeofMut) {

		double modifiedLon=lon+degreeofMut;
		return modifiedLon;

	}


	/*	private static double getRandomNumberInRange(double min, double max) {

		if (min >= max) {
			throw new IllegalArgumentException("max must be greater than min");
		}
		double result=(Math.random() * ((max - min) + 1)) + min;
		// 0.000090909
		return result;

	}*/


	public static void main(String[] args) throws IOException {

		Model model= Reader.readModel("/home/abddatascienceadmin/deer/Data_4/geoDataLonLat.ttl");

		ModelDistortion ModelDistortion= new ModelDistortion();
		Model outputModel=ModelDistortion.modeldistor(model, 10,0.0001);

		String outputFile= "/home/abddatascienceadmin/deer/Data_4/OutgeoDataLonLat10%.ttl";
		Writer.writeModel(outputModel, "TTL", outputFile);

	}

}