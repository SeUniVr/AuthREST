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
@Order(1378)
public class v2Callingcode_20230205203253799{

String baseURL ="https://restcountries.com";

	private void test0() throws JSONException{
		//OPERATION 0
		//Parameter initialization
		Object request0_query_fields = "swoosh";
		Object request0_path_callingcode = "subhalide";
		//Build request
 		RequestSpecification request0 = RestAssured.given();
		request0.pathParam("callingcode" , request0_path_callingcode);
		request0.queryParam("fields" , request0_query_fields);
		//Build Response
		Response response0 = request0.when().get(baseURL+"/v2/callingcode/{callingcode}");
		String response0_response_body = response0.getBody().asString();

		Assertions.assertFalse(response0.getStatusCode()>=500,"StatusCode 5xx: The test sequence was not executed successfully.");
	}
	@Test
	public void test_v2Callingcode_20230205203253799()  throws JSONException{
		test0();
	}
}
