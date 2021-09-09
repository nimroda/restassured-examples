package demo;
import io.restassured.path.json.JsonPath;
import pojo.Api;
import pojo.GetCourse;
import pojo.WebAutomation;

import static io.restassured.RestAssured.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.testng.Assert;

import io.restassured.parsing.Parser;

public class oAuthTest {

	public static void main(String[] args) throws InterruptedException {
		
		//DeSerialize Example
		
		//Expected array
		String[] courseTitles = {"Selenium Webdriver Java", "Cypress", "Protractor"};
		
		//Get OPT code
//		System.setProperty("webdriver.chrome.driver", "C:\\Users\\naviram\\Downloads\\chromedriver_win32\\chromedriver.exe");
//		WebDriver driver = new ChromeDriver();
//		driver.get("https://accounts.google.com/o/oauth2/v2/auth/identifier?scope=https%3A%2F%2Fwww.googleapis.com%2Fauth%2Fuserinfo.email&auth_url=https%3A%2F%2Faccounts.google.com%2Fo%2Foauth2%2Fv2%2Fauth&client_id=692183103107-p0m7ent2hk7suguv4vq22hjcfhcr43pj.apps.googleusercontent.com&response_type=code&redirect_uri=https%3A%2F%2Frahulshettyacademy.com%2FgetCourse.php&state=verifyjtds&flowName=GeneralOAuthFlow");
//		driver.findElement(By.cssSelector("input[type='email']")).sendKeys("nimoaviram@gmail.com");
//		driver.findElement(By.cssSelector("input[type='email']")).sendKeys(Keys.ENTER);
//		Thread.sleep(3000);
//		driver.findElement(By.cssSelector("input[type='password']")).sendKeys("Friends123");
//		driver.findElement(By.cssSelector("input[type='password']")).sendKeys(Keys.ENTER);
//		Thread.sleep(3000);
//		String url = driver.getCurrentUrl();
		String url = "https://rahulshettyacademy.com/getCourse.php?state=verifyjtds&code=4%2F0AX4XfWh_318fuaZPLOhLMmuhnkIy7KgFL-47DWLcuonNSQhWRv9AWRd9t_7gIlPmWgDvow&scope=email+openid+https%3A%2F%2Fwww.googleapis.com%2Fauth%2Fuserinfo.email&authuser=0&prompt=none";
		
		String partialCode = url.split("code=")[1];
		String code =  partialCode.split("&scope")[0];
		System.out.println(code);
		
		//GET Access Token
		String accessTokenResponse = given().urlEncodingEnabled(false)  
			   .queryParams("code", code)
			   .queryParams("client_id", "692183103107-p0m7ent2hk7suguv4vq22hjcfhcr43pj.apps.googleusercontent.com")
			   .queryParams("client_secret", "erZOWM9g3UtwNRj340YYaK_W")
			   .queryParam("redirect_uri", "https://rahulshettyacademy.com/getCourse.php")
			   .queryParam("grant_type", "authorization_code")
		.when().log().all()
		.post("https://www.googleapis.com/oauth2/v4/token").asString();
		
		JsonPath js = new JsonPath(accessTokenResponse);
		String accessToken = js.getString("access_token");
		
		//Actual service - get fields using pojo classes
		GetCourse gc = given().queryParam("access_token", accessToken).expect().defaultParser(Parser.JSON)
		.when()
		.get("https://rahulshettyacademy.com/getCourse.php").as(GetCourse.class);
		
		System.out.println(gc.getLinkedIn());
		System.out.println(gc.getInstructor());
		System.out.println(gc.getCourses().getApi().get(1).getCourseTitle());
		
		//Get the price of the soapUI course title
		List<Api> apiCourses = gc.getCourses().getApi();
		for (int i = 0; i <apiCourses.size(); i++) {
			if (apiCourses.get(i).getCourseTitle().equalsIgnoreCase("SoapUI Webservices testing")) {
				System.out.println(apiCourses.get(i).getPrice());
			}
		}
		
		//Print all course title of WebAutomation - using ArrayList to be dynamic and not a fixed size array (we don't know how many indexes the json will have)
		ArrayList<String> actualList = new ArrayList<String>();
		
		List<WebAutomation> webAutomationCourses = gc.getCourses().getWebAutomation();
		for (int i = 0; i < webAutomationCourses.size(); i++) {
			actualList.add(webAutomationCourses.get(i).getCourseTitle());
		}
		//Converting Array to ArrayList
		List<String> expectedList = Arrays.asList(courseTitles);
		//Assertion
		Assert.assertTrue(actualList.equals(expectedList));
		
	}
}
