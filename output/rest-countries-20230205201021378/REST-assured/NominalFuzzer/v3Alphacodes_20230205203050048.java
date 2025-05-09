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
@Order(796)
public class v3Alphacodes_20230205203050048{

String baseURL ="https://restcountries.com";

	private void test0() throws JSONException{
		//OPERATION 0
		//Parameter initialization
		Object request0_query_fields = "d25da5f1-0dc0-4af3-9911-ef763118fbc0";
		Object request0_query_codes = "00:18:25";
		//Build request
 		RequestSpecification request0 = RestAssured.given();
		request0.queryParam("fields" , request0_query_fields);
		request0.queryParam("codes" , request0_query_codes);
		//Build Response
		Response response0 = request0.when().get(baseURL+"/v3/alpha");
		String response0_response_body = response0.getBody().asString();

		Assertions.assertFalse(response0.getStatusCode()>=500,"StatusCode 5xx: The test sequence was not executed successfully.");
	}
	@Test
	public void test_v3Alphacodes_20230205203050048()  throws JSONException{
		test0();
	}
}
