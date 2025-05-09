package ErrorFuzzer;

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
@Order(2486)
public class v2Name_20230205204107402{

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
		Object request0_path_name = JsonPath.read(response1_response_body , "$[7]['name']");
		Object request0_query_fields = true;
		//Build request
 		RequestSpecification request0 = RestAssured.given();
		request0.pathParam("name" , request0_path_name);
		request0.queryParam("fields" , request0_query_fields);
		//Build Response
		Response response0 = request0.when().get(baseURL+"/v2/name/{name}");
		String response0_response_body = response0.getBody().asString();

		Assertions.assertFalse(response0.getStatusCode()<=299,"StatusCode 2xx: The test sequence was not executed successfully.");
		Assertions.assertFalse(response0.getStatusCode()>=500,"StatusCode 5xx: The test sequence was not executed successfully.");
	}
	@Test
	public void test_v2Name_20230205204107402()  throws JSONException{
		test0();
	}
}
