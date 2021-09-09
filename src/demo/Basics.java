package demo;
import io.restassured.RestAssured;
import io.restassured.path.json.JsonPath;

import static io.restassured.RestAssured.*;
import static org.hamcrest.Matchers.*;

import org.testng.Assert;

import files.Payload;
import files.ReUsableMethods;


public class Basics {

	public static void main(String[] args) {
		//given - all input details
		//when - submit the API - resource + http method
		//Then - validate the response
		
		//Add place
		RestAssured.baseURI = "https://rahulshettyacademy.com";
		String response = given().log().all()
				.queryParam("key", "qaclick123")
				.headers("Content-Type", "application/json")
				.body(Payload.AddPlace())
				.when().post("maps/api/place/add/json")
				.then().assertThat().statusCode(200).body("scope", equalTo("APP"))
				.header("server", "Apache/2.4.18 (Ubuntu)").extract().response().asString();
		
		System.out.println(response);
		JsonPath js = ReUsableMethods.rawToJson(response);
		String placeId = js.getString("place_id");
		
		System.out.println(placeId);
		
		//Update place - update place with new address --> get place to validate if new address is present in response
		String newAddress = "70 winter walk, USA";
		
		given().log().all()
				.queryParam("key", "qaclick123")
				.headers("Content-Type", "application/json")
				.body("{\r\n" + 
					"\"place_id\":\""+placeId+"\",\r\n" + 
					"\"address\":\""+newAddress+"\",\r\n" + 
					"\"key\":\"qaclick123\"\r\n" + 
					"}")
				.when().put("maps/api/place/update/json")
				.then().assertThat().log().all().statusCode(200).body("msg", equalTo("Address successfully updated"));
		
		//Get place - assert address changed
		String getPlaceResponse = given().log().all()
				.queryParam("key", "qaclick123")
				.queryParam("place_id", placeId)
				.when().get("maps/api/place/get/json")
				.then().assertThat().log().all().statusCode(200).extract().response().asString();
		JsonPath js1 = ReUsableMethods.rawToJson(getPlaceResponse);
		String actualAddress = js1.getString("address");
		
		System.out.println(actualAddress);
		//Cucumber, Junit, Testng
		Assert.assertEquals(actualAddress, newAddress);
		
	}
}
