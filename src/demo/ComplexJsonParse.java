package demo;
import org.testng.Assert;

import files.Payload;
import io.restassured.path.json.JsonPath;

public class ComplexJsonParse {

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		
		JsonPath js = new JsonPath(Payload.CoursePrice());
		
		//Print num of courses returned by API 
		int count = js.getInt("courses.size()");
		System.out.println(count);
		
		//Print Purchase Amount
		int totalAmount = js.getInt("dashboard.purchaseAmount");
		System.out.println(totalAmount);
		
		//Print Title of the first course
		String firstCourseTitle = js.get("courses[0].title");
		System.out.println(firstCourseTitle);
		
		//Print All course titles and their respective Prices
		for(int i=0; i<count; i++) {
			String coursetitles = js.get("courses["+ i +"].title");
			System.out.println(js.get("courses["+ i +"].price").toString());
			System.out.println(coursetitles);
		}
		
		//Print no of copies sold by RPA Course
		for(int i=0; i<count; i++) {
			String coursetitles = js.get("courses["+ i +"].title");
			if (coursetitles.equalsIgnoreCase("RPA")) {
				//copies sold
				System.out.println(js.get("courses["+ i +"].copies").toString());
				break;
			}
		}
		
		//Verify if Sum of all Course prices matches with Purchase Amount
		int purchaseAmount = js.getInt("dashboard.purchaseAmount");
		int sum = 0;
		for(int i=0; i<count; i++) {
			sum += js.getInt("courses["+ i +"].price");
		}
		Assert.assertEquals(sum, purchaseAmount);
	}

}
