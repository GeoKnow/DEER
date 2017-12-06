package org.aksw.deer.modules.geo;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Abdullah Ahmed
 *
 */
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
	protected static double radius = 6367000;

	public static double distanceGreatCircle(double lat1, double lon1, double lat2, double lon2) {
		double value1 = Math.pow(Math.sin((lat1 - lat2) / 2.0)* D2R, 2)
				+ Math.cos(lat1 * D2R) * Math.cos(lat2 * D2R) * Math.pow(Math.sin((lon1 - lon2) / 2.0)* D2R, 2);
		double c = 2 * Math.atan2(Math.sqrt(value1), Math.sqrt(1 - value1));
		double distanceGreatCircleMeasure = radius * c;
		return distanceGreatCircleMeasure;
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

		latValues.add(60.2647205); // 60.2647205 +1=  61.2647205, 60.2647205-1= 59.2647205
		latValues.add(60.26458);
		latValues.add(60.2648094);
		latValues.add(60.2649712);
		latValues.add(60.2646841);
		latValues.add(60.2647983);
		latValues.add(60.264867);
		latValues.add(60.264692);
		latValues.add(60.264624);
		latValues.add(60.2644563);

		lonValues.add(5.3564849); // 5.3564849+1= 6.3564849, 5.3564849-1= 4.3564849
		lonValues.add(5.3549628);
		lonValues.add(5.3564384);
		lonValues.add(5.3581036);
		lonValues.add(5.3554944);
		lonValues.add(5.356387);
		lonValues.add(5.3569038);
		lonValues.add(5.3574291);
		lonValues.add(5.3552665);
		lonValues.add(5.3557186);


		long startTime = System.currentTimeMillis();
		double distanceVincenty= distanceVincenty(60.2647205, 5.3564879,60.7647205,5.3564849);


		long stopTime = System.currentTimeMillis();
		long elapsedTime = stopTime - startTime;
		System.out.println(elapsedTime);
		double similartyVincetly= 1/(1+distanceVincenty);


		long startTime2 = System.currentTimeMillis();
		double distance_1= distanceGreatCircle( 60.2647205, 5.3564879,60.7647205,5.3564849);// add one to lat and fix the lon
		long stopTime2 = System.currentTimeMillis();
		long elapsedTime2 = stopTime2 - startTime2;
		System.out.println(elapsedTime2);


		double similarty1=1/(1+distance_1);
		System.out.println(" distanceVincenty = "+ distanceVincenty +" similartyVincetly= "+ similartyVincetly);
		System.out.println(" distanceGreat = "+ distance_1+ " similartyGreat= "+ similarty1);
		System.out.println(" the diffrence of distance = "+ (distanceVincenty-distance_1));
		System.out.println(" the diffrence of the similarty = "+ (similartyVincetly-similarty1));


		/*		double distance_1= distance( 60.2647205, 5.3564849,61.2647205,5.3564849);// add one to lat and fix the lon
		double similarty1=1/(1+distance_1);
		double distance_2= distance( 60.2647205, 5.3564849,59.2647205,5.3564849);// sub one from the lat and fix the lon
		double similarty2=1/(1+distance_2);
		double distance_3= distance( 60.2647205, 5.3564849,61.2647205,6.3564849);// add one to both lat and   lon
		double similarty3=1/(1+distance_3);
		double distance_4= distance( 60.2647205, 5.3564849,59.2647205,4.3564849);// sub one from both  lat and lon
		double similarty4=1/(1+distance_4);

		System.out.println("distance_1 \t \t"+ "distance_2 \t \t "+ "distance_3 \t \t "+ "distance_4");
		System.out.println(distance_1 +"\t"+ distance_2+"\t"+ distance_3 +"\t"+ distance_4);
		System.out.println("\n");
		System.out.println("similarty1 \t \t "+ "similarty2 \t \t "+ "similarty3 \t \t"+ "similarty4");
		System.out.println(similarty1+"\t"+ similarty2+"\t"+ similarty3 +"\t"+ similarty4);*/



		/*
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

		 */



		/**
		 * Calculates geodetic distance between two points specified by latitude/longitude using Vincenty inverse formula
		 * for ellipsoids
		 * 
		 * @param lat1
		 *            first point latitude in decimal degrees
		 * @param lon1
		 *            first point longitude in decimal degrees
		 * @param lat2
		 *            second point latitude in decimal degrees
		 * @param lon2
		 *            second point longitude in decimal degrees
		 * @returns distance in meters between points with 5.10<sup>-4</sup> precision
		 * @see <a href="http://www.movable-type.co.uk/scripts/latlong-vincenty.html">Originally posted here</a>
		 */
	}
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
		} while (Math.abs(lambda - lambdaP) > 1e-12 && --iterLimit > 0);

		if (iterLimit == 0)
			return Double.NaN; // formula failed to converge

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


