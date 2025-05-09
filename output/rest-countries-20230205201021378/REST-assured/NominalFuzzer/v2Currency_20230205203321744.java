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
@Order(1542)
public class v2Currency_20230205203321744{

String baseURL ="https://restcountries.com";

	private void test0() throws JSONException{
		//OPERATION 0
		//Parameter initialization
		Object request0_query_fields = "2013/03/26";
		Object request0_path_currency = "x5FhN";
		//Build request
 		RequestSpecification request0 = RestAssured.given();
		request0.pathParam("currency" , request0_path_currency);
		request0.queryParam("fields" , request0_query_fields);
		//Build Response
		Response response0 = request0.when().get(baseURL+"/v2/currency/{currency}");
		String response0_response_body = response0.getBody().asString();

		Assertions.assertFalse(response0.getStatusCode()>=500,"StatusCode 5xx: The test sequence was not executed successfully.");
	}
	@Test
	public void test_v2Currency_20230205203321744()  throws JSONException{
		test0();
	}
}
