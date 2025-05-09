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
@Order(171)
public class v3Subregion_20230205202532887{

String baseURL ="https://restcountries.com";

	private void test0() throws JSONException{
		//OPERATION 1
		//Build request
 		RequestSpecification request1 = RestAssured.given();
		//Build Response
		Response response1 = request1.when().get(baseURL+"/v2/all");
		String response1_response_body = response1.getBody().asString();

		Assertions.assertTrue(response1.getStatusCode()<=299,"StatusCode not 2xx for previous operation.");
		//OPERATION 0
		//Parameter initialization
		Object request0_path_subregion = JsonPath.read(response1_response_body , "$[2]['subregion']");
		//Build request
 		RequestSpecification request0 = RestAssured.given();
		request0.pathParam("subregion" , request0_path_subregion);
		//Build Response
		Response response0 = request0.when().get(baseURL+"/v3/subregion/{subregion}");
		String response0_response_body = response0.getBody().asString();

		Assertions.assertFalse(response0.getStatusCode()>=500,"StatusCode 5xx: The test sequence was not executed successfully.");
	}
	@Test
	public void test_v3Subregion_20230205202532887()  throws JSONException{
		test0();
	}
}
