package geo;

import java.util.List;

import org.aksw.deer.modules.geo.FindwantedObject;
import org.junit.Test;

import junit.framework.Assert;

@SuppressWarnings("deprecation")
public class FindwantedObjectTest {

	@Test
	public void FindLatLongTest() {
		
		FindwantedObject find = new FindwantedObject();
		
		String y= "-49.1114599";
		String x="-26.8897016";
		List <String> values = find.TSVfindLonLat("Rua Francisco Alb");
		System.out.println(" is this true " + values);
		Assert.assertTrue(values.contains(y));
		Assert.assertTrue(values.contains(x));
System.out.println(" the test1 is  done ");
	}
	@Test
	public void FindAddressTest() {
		//HashMap<String, String> returnObjects = new HashMap<>();
		FindwantedObject find = new FindwantedObject();
		
		String y= "-49.1114599";
		String x="-26.8897016";
		String z= "Rua Francisco Albo";
		List<String> values = find.TSVfindAddress(x,y);
		System.out.println(values);
		Assert.assertTrue(values.contains(z));
	//	Assert.assertTrue(values.containsValue(x));
System.out.println(" the test is  done ");
	}
}