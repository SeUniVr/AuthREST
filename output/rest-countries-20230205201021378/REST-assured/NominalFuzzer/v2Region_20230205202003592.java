package NominalFuzzer;

import static io.restassured.RestAssured.*;
import com.jayway.jsonpath.JsonPath;
import io.restassured.RestAssured;
import io.restassured.specification.RequestSpecification;
import static io.restassured.matcher.RestAssuredMatchers.*;
import static org.hamcrest.Matchers.*;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import io.restassured.common.mapper.TypeRef;
import java.util.List;
import java.util.Map;
import java.util.HashMap;
import org.json.*;
import org.junit.jupiter.api.*;
//import org.junit.runners.*;

//@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
@Order(108)
public class v2Region_20230205202003592{

String baseURL ="https://restcountries.com";

	private void test0() throws JSONException{
		//OPERATION 3
		//Parameter initialization
		Object request3_path_capital = "P";
		//Build request
 		RequestSpecification request3 = RestAssured.given();
		request3.pathParam("capital" , request3_path_capital);
		//Build Response
		Response response3 = request3.when().get(baseURL+"/v2/capital/{capital}");
		String response3_response_body = response3.getBody().asString();

		Assertions.assertTrue(response3.getStatusCode()<=299,"StatusCode not 2xx for previous operation.");
		//OPERATION 2
		//Parameter initialization
		Object request2_path_demonym = JsonPath.read(response3_response_body , "$[1]['demonym']");
		//Build request
 		RequestSpecification request2 = RestAssured.given();
		request2.pathParam("demonym" , request2_path_demonym);
		//Build Response
		Response response2 = request2.when().get(baseURL+"/v3/demonym/{demonym}");
		String response2_response_body = response2.getBody().asString();

		Assertions.assertTrue(response2.getStatusCode()<=299,"StatusCode not 2xx for previous operation.");
		//OPERATION 1
		//Parameter initialization
		Object request1_path_capital = JsonPath.read(response2_response_body , "$[0]['capital'][0]");
		//Build request
 		RequestSpecification request1 = RestAssured.given();
		request1.pathParam("capital" , request1_path_capital);
		//Build Response
		Response response1 = request1.when().get(baseURL+"/v3/capital/{capital}");
		String response1_response_body = response1.getBody().asString();

		Assertions.assertTrue(response1.getStatusCode()<=299,"StatusCode not 2xx for previous operation.");
		//OPERATION 0
		//Parameter initialization
		Object request0_path_region = JsonPath.read(response1_response_body , "$[0]['region']");
		//Build request
 		RequestSpecification request0 = RestAssured.given();
		request0.pathParam("region" , request0_path_region);
		//Build Response
		Response response0 = request0.when().get(baseURL+"/v2/{region}");
		String response0_response_body = response0.getBody().asString();

		Assertions.assertFalse(response0.getStatusCode()>=500,"StatusCode 5xx: The test sequence was not executed successfully.");
	}
	@Test
	public void test_v2Region_20230205202003592()  throws JSONException{
		test0();
	}
}
