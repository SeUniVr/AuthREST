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
@Order(332)
public class v2Alphacode_20230205202829846{

String baseURL ="https://restcountries.com";

	private void test0() throws JSONException{
		//OPERATION 0
		//Parameter initialization
		Object request0_path_alphacode = "oG8yO";
		Object request0_query_fields = "uaQApLNpUymE_algJ_fEjOFaq9x7fUJXUEEHZKj5q725RJZCuWZdc8fPwrlO_v9thXcD9ZQwH3hQidcJ85yNL1SEVMc9N4L7MAU4bSgRXO11oaGzUwaLlxwa";
		//Build request
 		RequestSpecification request0 = RestAssured.given();
		request0.pathParam("alphacode" , request0_path_alphacode);
		request0.queryParam("fields" , request0_query_fields);
		//Build Response
		Response response0 = request0.when().get(baseURL+"/v2/alpha/{alphacode}");
		String response0_response_body = response0.getBody().asString();

		Assertions.assertFalse(response0.getStatusCode()>=500,"StatusCode 5xx: The test sequence was not executed successfully.");
	}
	@Test
	public void test_v2Alphacode_20230205202829846()  throws JSONException{
		test0();
	}
}
