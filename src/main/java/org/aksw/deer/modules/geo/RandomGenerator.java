package org.aksw.deer.modules.geo;

import java.util.ArrayList;
import java.util.List;

public class RandomGenerator {
	
	static List<Double> results=new ArrayList<Double>();
	static List<Double> latValues=new ArrayList<Double>();
	static List<Double> lonValues=new ArrayList<Double>();
	static List<Double> lat2=new ArrayList<Double>();
	static List<Double> lon2=new ArrayList<Double>();
	static List<Double> lat2M=new ArrayList<Double>();
	static List<Double> lon2M=new ArrayList<Double>();
	
	static List<Double> distancePluse=new ArrayList<Double>();
	static List<Double> distanceMinuse=new ArrayList<Double>();
	static List<Double> similartyPluse=new ArrayList<Double>();
	static List<Double> similartyMinuse=new ArrayList<Double>();
	
	protected static double D2R = Math.PI / 180;
	protected static double radius = 6367;
	
	   public static double distance(double lat1, double lon1, double lat2, double lon2) {
	        double value1 = Math.pow(Math.sin((lat1 - lat2) / 2.0)* D2R, 2)
	                + Math.cos(lat1 * D2R) * Math.cos(lat2 * D2R) * Math.pow(Math.sin((lon1 - lon2) / 2.0)* D2R, 2);
	        double c = 2 * Math.atan2(Math.sqrt(value1), Math.sqrt(1 - value1));
	        double d = radius * c;
	        return d;
	    }
	   
	private static double getRandomNumberInRange(double min, double max) {

		if (min >= max) {
			throw new IllegalArgumentException("max must be greater than min");
		}
		double result=(Math.random() * ((max - min) + 1)) + min;
       // 0.000090909
		return result;
		
	}

	public static void main(String[] args) {
		
		latValues.add(60.2647205);
		latValues.add(60.26458);
		latValues.add(60.2648094);
		latValues.add(60.2649712);
		latValues.add(60.2646841);
		latValues.add(60.2647983);
		latValues.add(60.264867);
		latValues.add(60.264692);
		latValues.add(60.264624);
		latValues.add(60.2644563);

		lonValues.add(5.3564849);
		lonValues.add(5.3549628);
		lonValues.add(5.3564384);
		lonValues.add(5.3581036);
		lonValues.add(5.3554944);
		lonValues.add(5.356387);
		lonValues.add(5.3569038);
		lonValues.add(5.3574291);
		lonValues.add(5.3552665);
		lonValues.add(5.3557186);
		
		
		for(int i=0;i<10;i++) {
		
		// TODO Auto-generated method stub
		 double result=getRandomNumberInRange(-0.000090909,0.000090909);
		 
		 results.add(result);
		 
		 double latPluse=latValues.get(i)+results.get(i);
		 lat2.add(latPluse);
		 
		 double lonPluse=lonValues.get(i)+results.get(i);
		 lon2.add(lonPluse);
		 
		 double latMinus=latValues.get(i)-results.get(i);
		 lat2M.add(latMinus);
		 
		 double lonMinus=lonValues.get(i)-results.get(i);
		 lon2M.add(lonMinus);
		 
		 double distancePlus= distance( latValues.get(i), lonValues.get(i),lat2.get(i),lon2.get(i));
		 distancePluse.add(distancePlus);
		 double similartyplus=(1/(1+distancePluse.get(i)));
		 similartyPluse.add(similartyplus);
		 
		 double distanceMinus= distance( latValues.get(i), lonValues.get(i),lat2M.get(i),lon2M.get(i));
		 distanceMinuse.add(distanceMinus);
		 double similartyminus=(1/(1+distanceMinuse.get(i)));
		 similartyMinuse.add(similartyminus);
		 
		 System.out.println("\n "+ " LATVALUES = "+ latValues.get(i)+ " LONVALUES= " + lonValues.get(i)+ " THE RANDOMVALUES= " + results.get(i)+ 
				 " latPlusegenerated= "+ lat2.get(i)+ " lonPlusegenrated = "+ lon2.get(i)+ " Distance Pluse = "+ distancePluse.get(i)+ " SimilartyPluse= "
				 + similartyPluse.get(i) );
		 System.out.println("\n **************************************");
		 System.out.println("\n **************************************");
		 System.out.println("\n "+ "LATVALUES = "+ latValues.get(i)+ " LONVALUES= " + lonValues.get(i)+ " THE RANDOMVALUES= " + results.get(i)+ 
				 " latMinusgenerated= "+ lat2M.get(i)+ " lonMinusgenrated = "+ lon2M.get(i)+ " Distance Minus = "+ distanceMinuse.get(i)+ " SimilartyMinus= "
				 + similartyMinuse.get(i) );
	}

	
	}
}
	
	
