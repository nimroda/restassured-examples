package demo;
import io.restassured.RestAssured;
import io.restassured.filter.session.SessionFilter;
import io.restassured.path.json.JsonPath;

import static io.restassured.RestAssured.*;

import java.io.File;

import org.testng.Assert;

public class JiraTest {

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		
		RestAssured.baseURI = "http://localhost:8080";
		
		//login scenario
		SessionFilter session = new SessionFilter(); //handle session info
		//using https without certificates
		String response = given().relaxedHTTPSValidation().header("Content-Type", "application/json")
		.body("{ \"username\": \"nimoaviram\", \"password\": \"jcrho123\" }").log().all().filter(session)
		.when()
		.post("/rest/auth/1/session").then().log().all().extract().response().asString();
		
		//Add comment
		String expectedMessage = "This is my first comment from code";
		String addCommentResponse = given().pathParam("issueIdOrKey", "10100").log().all()
		.header("Content-Type", "application/json")
		.body("{\r\n" + 
				"    \"body\": \""+expectedMessage+"\",\r\n" + 
				"    \"visibility\": {\r\n" + 
				"        \"type\": \"role\",\r\n" + 
				"        \"value\": \"Administrators\"\r\n" + 
				"    }\r\n" + 
				"}")
		.filter(session)
		.when()
		.post("/rest/api/2/issue/{issueIdOrKey}/comment")
		.then().log().all().assertThat().statusCode(201).extract().response().asString();
		JsonPath js =new JsonPath(addCommentResponse);
		String commentId =  js.getString("id");
		
		//Add Attachment
		given().header("X-Atlassian-Token","no-check").filter(session).pathParam("issueIdOrKey", "10100")
		.header("Content-Type","multipart/form-data")
		.multiPart("file",new File("jira.txt"))
		.when()
		.post("rest/api/2/issue/{issueIdOrKey}/attachments").then().log().all().assertThat().statusCode(200);

		//Get Issue - limiting response by using queryParam
		String issueDetails = given().filter(session).pathParam("issueIdOrKey", "10100")
		.queryParam("fields", "comment")
		.log().all().when().get("/rest/api/2/issue/{issueIdOrKey}").then()
		.log().all().extract().response().asString();

		System.out.println(issueDetails);

		JsonPath js1 =new JsonPath(issueDetails);
		int commentsCount = js1.getInt("fields.comment.comments.size()");
		
		//Get all comment id's and match for the added comment
		for(int i=0; i<commentsCount; i++){
			String commentIdIssue = js1.get("fields.comment.comments["+i+"].id").toString();
			if (commentIdIssue.equalsIgnoreCase(commentId)){
				String message = js1.get("fields.comment.comments["+i+"].body").toString();
				System.out.println(message);
				Assert.assertEquals(message, expectedMessage);
			}
		}
	}
}
