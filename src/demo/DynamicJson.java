package demo;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import files.Payload;
import files.ReUsableMethods;
import io.restassured.RestAssured;
import io.restassured.path.json.JsonPath;

import static io.restassured.RestAssured.*;

public class DynamicJson {
	
	@Test(dataProvider="BooksData")
	public void addBook(String isbn, String aisle) {
		
		RestAssured.baseURI="http://216.10.245.166";
		String response = 
				given().header("Content-Type", "application/json")
			   .body(Payload.Addbook(isbn, aisle))
			   .when()
			   .post("/Library/Addbook.php")
			   .then().log().all().assertThat().statusCode(200)
			   .extract().response().asString();
		JsonPath js = ReUsableMethods.rawToJson(response);		
		String id = js.get("ID");
		System.out.println(id);
		
	}
	
	@Test(dataProvider="BooksData")
	public void deleteBook(String isbn, String aisle) {
		
		RestAssured.baseURI="http://216.10.245.166";
		String response = 
				given().header("Content-Type", "application/json")
				.body(Payload.Deletebook(isbn, aisle))
				.when()
				.post("/Library/DeleteBook.php")
				.then().log().all().assertThat().statusCode(200)
				.extract().response().asString();
		JsonPath js = ReUsableMethods.rawToJson(response);
		
	}
	
	@DataProvider(name="BooksData")
	public Object[][] getData() {
		
		//multidimenentional array == collection of arrays
		return new Object[][] { {"ahst","6400"}, {"ascad","5634"}, {"asvfd","8934"} };
	}
}
