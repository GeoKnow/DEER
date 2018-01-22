package org.aksw.deer.modules.geo;

import org.aksw.deer.io.Reader;
import org.apache.jena.rdf.model.Model;

public class FMeasure {

	public double P,R,F;

	/**
	 * 
	 *@author sherif
	 */
	public FMeasure(double p, double r, double f) {
		P = p;
		R = r;
		F = f;
	}


	public static FMeasure computePRF(Model current, Model target){
		double p = computePrecision(current, target);
		double r = computeRecall(current, target);
		double f = 2 * p * r / (p + r);
		return new FMeasure(p, r, f);
	}

	public static double computeFMeasure(Model current, Model target){
		double p = computePrecision(current, target);
		double r = computeRecall(current, target);
		return 2 * p * r / (p + r);
	}

	public static double computePrecision(Model current, Model target){
		return (double) current.intersection(target).size() / (double) current.size();
	}

	public static double computeRecall(Model current, Model target){
		return (double) current.intersection(target).size() / (double) target.size();
	}

	@Override
	public String toString() {
		return "FMeasure [P=" + P + ", R=" + R + ", F=" + F + "]";
	}


	public static void main(String[] args) {

		Model current =Reader.readModel("/home/abddatascienceadmin/deer/Data_4/geoDataosm1.ttl");
		Model target  =Reader.readModel("/home/abddatascienceadmin/deer/datasets/6/output4.ttl");
		FMeasure fMeasure= computePRF(current, target);

		System.out.println("FMeasure = "+ fMeasure);
		// TODO Auto-generated method stub

	}

}
