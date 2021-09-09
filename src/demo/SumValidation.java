package demo;
import org.testng.Assert;
import org.testng.annotations.Test;

import files.Payload;
import io.restassured.path.json.JsonPath;

public class SumValidation {
	
	@Test
	public void sumOfCourses() {
		
		//Verify if Sum of all Course prices matches with Purchase Amount
		JsonPath js = new JsonPath(Payload.CoursePrice());
		
		int sum = 0;
		int count = js.getInt("courses.size()");
		
		for(int i=0; i<count; i++) {
			int price = js.getInt("courses["+ i +"].price");
			int copies = js.getInt("courses["+ i +"].copies");
			int amount = price * copies;
			System.out.println(amount);
			sum = sum + amount;
		}
		System.out.println(sum);
		int purchaseAmount = js.getInt("dashboard.purchaseAmount");
		Assert.assertEquals(sum, purchaseAmount);
	}
}
