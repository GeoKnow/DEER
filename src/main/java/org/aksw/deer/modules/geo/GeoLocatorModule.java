package org.aksw.deer.modules.geo;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.aksw.deer.json.ParameterType;
import org.aksw.deer.modules.DeerModule;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.NodeIterator;
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
			Model resultModel = findAddress(model, parameters.get(INPUT_LAT_PROPERTY),
					parameters.get(INPUT_LONG_PROPERTY));
			return model.add(resultModel);
		}
		return model;
	}

	/**
	 * @param model
	 * @param inputLatPropName
	 * @param inputLongPropName
	 * @return model
	 * 
	 */
	private Model findAddress(Model model, String inputLatPropName, String inputLongPropName) {

		Property inputLatProp = ResourceFactory.createProperty(inputLatPropName);
		StmtIterator iter_Lat = model.listStatements(null, inputLatProp, (RDFNode) null);
		while (iter_Lat.hasNext()) {
			Statement stmt = iter_Lat.nextStatement(); 
			Resource subject = stmt.getSubject(); 
			RDFNode object = stmt.getObject();
			String OurObjectLat = object.asLiteral().toString();
			Property inputLongProp = ResourceFactory.createProperty(inputLongPropName);
			NodeIterator objectLatItr = model.listObjectsOfProperty(inputLongProp);
			List<String> values_Adress = new ArrayList<>();

			while (objectLatItr.hasNext()) {
				String OurObjectLong = objectLatItr.next().asLiteral().toString();
				FindwantedObject find = new FindwantedObject();
				values_Adress.addAll(find.TSVfindAddress(OurObjectLat, OurObjectLong));
			}
			for (String Iteration : values_Adress) {

				RDFNode NewObjectToAddOfLong = ResourceFactory.createStringLiteral(Iteration);
				Property outputLongProp = ResourceFactory.createProperty(OUTPUT_ADDRESS_PROPERTY);
				model.add(subject, outputLongProp, NewObjectToAddOfLong);
			}
		}
		return model;
	}

	private Model findLongLat(Model model, String inputAddressPropertyName) throws IOException {

		Property inputAddressProp = ResourceFactory.createProperty(inputAddressPropertyName);
		StmtIterator iter = model.listStatements(null, inputAddressProp, (RDFNode) null);
		while (iter.hasNext()) {
			Statement stmt = iter.nextStatement();
			Resource subject = stmt.getSubject();
			RDFNode object = stmt.getObject();

			String OurObjectAdress = object.asLiteral().toString();
			FindwantedObject find = new FindwantedObject();
			List<String> values_Address;
			values_Address = find.TSVfindLonLat(OurObjectAdress);
			for (String Iteration : values_Address) {

				RDFNode NewObjectToAddOfAddress = ResourceFactory.createStringLiteral(Iteration);
				Property outputLatAddress = ResourceFactory.createProperty(OUTPUT_LAT_PROPERTY);
				model.add(subject, outputLatAddress, NewObjectToAddOfAddress);
				//Property outputLongtAddress = ResourceFactory.createProperty(OUTPUT_LONG_PROPERTY);
				//model.add(subject, outputLongtAddress, NewObjectToAddOfAddress);
			}

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
