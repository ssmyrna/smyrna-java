package steps;

import config.ConfigReader;
import helper.Helper;
import io.cucumber.datatable.DataTable;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.When;
import io.restassured.RestAssured;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;
import lombok.extern.slf4j.Slf4j;


import java.util.List;
import java.util.Map;

@Slf4j
public class PrepareRequest {
    public static RequestSpecification request;
    public static Response response;


    @Given("Prepare Url {string}")
    public void prepareUrl(String url) {
        try {
            String baseUrl = ConfigReader.get("base.url");
            RestAssured.baseURI = baseUrl + url;
            request = RestAssured.given();
        } catch (Exception e) {
            log.error("Error Message: " + e);
        }

    }

    @When("Send Request {string}")
    public void sendRequest(String method) {
        try {
            if (Helper.jo != null) {
                request.body(Helper.jo.jsonString());
            }
            switch (method) {
                case "GET":
                    response = request.when().log().all().get();
                    break;
                case "POST":
                    response = request.when().log().all().post();
                    break;
                case "PUT":
                    response = request.when().log().all().put();
                    break;
                case "DELETE":
                    response = request.when().log().all().delete();
                    break;
            }
            log.info("Response Body = " + response.asString());
        } catch (Exception e) {
            log.error("Error Message: " + e);
        }


    }

    @Given("Prepare Headers")
    public void prepareHeaders(DataTable dt) {

        try {
            List<Map<String, String>> map = dt.asMaps(String.class, String.class);
            for (Map<String, String> value : map) {
                request.headers(value.get("key"), value.get("value"));
            }
            log.info("Headers Is Ready.");
        } catch (Exception e) {
            log.error("Error Message: " + e);
        }
    }

    @Given("Prepare Request Body {string}")
    public void prepareRequestBody(String path, DataTable dt) throws Exception {
        try {
            List<Map<String, String>> map = dt.asMaps(String.class, String.class);
            Helper.jsonEdit(path);
            for (Map<String, String> value : map) {
                if (Helper.getTypeOfValue(value.get("key")).equals("String")) {
                    Helper.jo.set(value.get("key"), Helper.dataConversionString(value.get("value")));
                } else if (Helper.getTypeOfValue(value.get("key")).equals("Number")) {
                    Helper.jo.set(value.get("key"), Helper.dataConversionInteger(value.get("value")));
                } else if (Helper.getTypeOfValue(value.get("key")).equals("Boolean")) {
                    Helper.jo.set(value.get("key"), Helper.dataConversionBoolean(value.get("value")));
                }
            }
            log.info(path + " Request Body Is Ready.");
        } catch (Exception e) {
            log.error("Error Message: " + e);
        }


    }

    @Given("Fields Control {string} at {string}")
    public void fieldsControlAt(String field, String request) {
        try {
            Helper.jsonEdit(request);
            Helper.jo.delete(field);
            log.info(field + " Field Remove On" + request + " Body");
        } catch (Exception e) {
            log.error("Error Message: " + e);
        }


    }

    @Given("Prepare Params")
    public void prepareParams(DataTable dt) {
        try {
            List<Map<String, String>> map = dt.asMaps(String.class, String.class);
            for (Map<String, String> value : map) {
                request.queryParam(value.get("key"), value.get("value"));
            }
            log.info("Params Is Ready.");
        } catch (Exception e) {
            log.error("Error Message: " + e);
        }
    }
}