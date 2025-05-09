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
@Order(161)
public class v3Subregion_20230205202532835{

String baseURL ="https://restcountries.com";

	private void test0() throws JSONException{
		//OPERATION 2
		//Parameter initialization
		Object request2_path_capital = "E";
		//Build request
 		RequestSpecification request2 = RestAssured.given();
		request2.pathParam("capital" , request2_path_capital);
		//Build Response
		Response response2 = request2.when().get(baseURL+"/v2/capital/{capital}");
		String response2_response_body = response2.getBody().asString();

		Assertions.assertTrue(response2.getStatusCode()<=299,"StatusCode not 2xx for previous operation.");
		//OPERATION 1
		//Parameter initialization
		Object request1_path_demonym = JsonPath.read(response2_response_body , "$[14]['demonym']");
		//Build request
 		RequestSpecification request1 = RestAssured.given();
		request1.pathParam("demonym" , request1_path_demonym);
		//Build Response
		Response response1 = request1.when().get(baseURL+"/v3/demonym/{demonym}");
		String response1_response_body = response1.getBody().asString();

		Assertions.assertTrue(response1.getStatusCode()<=299,"StatusCode not 2xx for previous operation.");
		//OPERATION 0
		//Parameter initialization
		Object request0_path_subregion = JsonPath.read(response1_response_body , "$[0]['subregion']");
		//Build request
 		RequestSpecification request0 = RestAssured.given();
		request0.pathParam("subregion" , request0_path_subregion);
		//Build Response
		Response response0 = request0.when().get(baseURL+"/v3/subregion/{subregion}");
		String response0_response_body = response0.getBody().asString();

		Assertions.assertFalse(response0.getStatusCode()>=500,"StatusCode 5xx: The test sequence was not executed successfully.");
	}
	@Test
	public void test_v3Subregion_20230205202532835()  throws JSONException{
		test0();
	}
}
