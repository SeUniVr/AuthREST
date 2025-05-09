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
@Order(2403)
public class v3Translation_20230205204056602{

String baseURL ="https://restcountries.com";

	private void test0() throws JSONException{
		//OPERATION 0
		//Parameter initialization
		Object request0_path_translation = true;
		//Build request
 		RequestSpecification request0 = RestAssured.given();
		request0.pathParam("translation" , request0_path_translation);
		//Build Response
		Response response0 = request0.when().get(baseURL+"/v3/translation/{translation}");
		String response0_response_body = response0.getBody().asString();

		Assertions.assertFalse(response0.getStatusCode()<=299,"StatusCode 2xx: The test sequence was not executed successfully.");
		Assertions.assertFalse(response0.getStatusCode()>=500,"StatusCode 5xx: The test sequence was not executed successfully.");
	}
	@Test
	public void test_v3Translation_20230205204056602()  throws JSONException{
		test0();
	}
}
