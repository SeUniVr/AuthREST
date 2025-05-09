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
@Order(198)
public class v3Region_20230205202556471{

String baseURL ="https://restcountries.com";

	private void test0() throws JSONException{
		//OPERATION 2
		//Build request
 		RequestSpecification request2 = RestAssured.given();
		//Build Response
		Response response2 = request2.when().get(baseURL+"/v2/all");
		String response2_response_body = response2.getBody().asString();

		Assertions.assertTrue(response2.getStatusCode()<=299,"StatusCode not 2xx for previous operation.");
		//OPERATION 1
		//Parameter initialization
		Object request1_path_subregion = JsonPath.read(response2_response_body , "$[7]['subregion']");
		//Build request
 		RequestSpecification request1 = RestAssured.given();
		request1.pathParam("subregion" , request1_path_subregion);
		//Build Response
		Response response1 = request1.when().get(baseURL+"/v3/subregion/{subregion}");
		String response1_response_body = response1.getBody().asString();

		Assertions.assertTrue(response1.getStatusCode()<=299,"StatusCode not 2xx for previous operation.");
		//OPERATION 0
		//Parameter initialization
		Object request0_path_region = JsonPath.read(response1_response_body , "$[9]['region']");
		//Build request
 		RequestSpecification request0 = RestAssured.given();
		request0.pathParam("region" , request0_path_region);
		//Build Response
		Response response0 = request0.when().get(baseURL+"/v3.1/region/{region}");
		String response0_response_body = response0.getBody().asString();

		Assertions.assertFalse(response0.getStatusCode()>=500,"StatusCode 5xx: The test sequence was not executed successfully.");
	}
	@Test
	public void test_v3Region_20230205202556471()  throws JSONException{
		test0();
	}
}
