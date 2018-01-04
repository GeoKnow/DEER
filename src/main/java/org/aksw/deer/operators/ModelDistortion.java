package org.aksw.deer.operators;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.jena.rdf.model.Model;

public class ModelDistortion implements DeerOperator{



	@Override
	public List<Model> process(List<Model> models, Map<String, String> parameters) {
		// TODO Auto-generated method stub
		return models;
	}

	@Override
	public List<String> getParameters() {
		List<String> parameters = new ArrayList<String>();
		return parameters;
	}

	@Override
	public List<String> getNecessaryParameters() {
		List<String> parameters = new ArrayList<String>();
		return parameters;
	}

	
	
	public static void main(String[] args) {
		// TODO Auto-generated method stub

	}
}
