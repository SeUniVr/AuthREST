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
@Order(1386)
public class v2Region_20230205203256365{

String baseURL ="https://restcountries.com";

	private void test0() throws JSONException{
		//OPERATION 1
		//Parameter initialization
		Object request1_path_currency = "s";
		//Build request
 		RequestSpecification request1 = RestAssured.given();
		request1.pathParam("currency" , request1_path_currency);
		//Build Response
		Response response1 = request1.when().get(baseURL+"/v3/currency/{currency}");
		String response1_response_body = response1.getBody().asString();

		Assertions.assertTrue(response1.getStatusCode()<=299,"StatusCode not 2xx for previous operation.");
		//OPERATION 0
		//Parameter initialization
		Object request0_path_region = JsonPath.read(response1_response_body , "$[50]['region']");
		//Build request
 		RequestSpecification request0 = RestAssured.given();
		request0.pathParam("region" , request0_path_region);
		//Build Response
		Response response0 = request0.when().get(baseURL+"/v2/{region}");
		String response0_response_body = response0.getBody().asString();

		Assertions.assertFalse(response0.getStatusCode()>=500,"StatusCode 5xx: The test sequence was not executed successfully.");
	}
	@Test
	public void test_v2Region_20230205203256365()  throws JSONException{
		test0();
	}
}
