package steps;

import com.jayway.jsonpath.DocumentContext;
import config.ConfigReader;
import helper.Helper;
import io.cucumber.datatable.DataTable;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import io.restassured.RestAssured;
import io.restassured.path.json.JsonPath;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.Assert;

import java.util.List;
import java.util.Map;

import static steps.PrepareRequest.response;


public class BaseSteps {
    private static final Logger logger = LoggerFactory.getLogger(BaseSteps.class);

    public static RequestSpecification mockRequest;

    public static Response mockResponse;

    @Then("Expected to see {int} status code")
    public void expectedToSeeStatusCode(int code) {
        try {
            Assert.assertEquals(code, response.statusCode());
            logger.info("Actual Status Code= " + response.statusCode() + " Expected Status Code: " + code);
        } catch (Exception e) {
            logger.error("Error Message: " + e);
            Assert.fail();
        }
    }

    @Then("Response Control")
    public void responseControl(DataTable dt) {
        try {
            List<Map<String, String>> map = dt.asMaps(String.class, String.class);
            JsonPath res = new JsonPath(response.getBody().asString());

            for (Map<String, String> value : map) {
                String path = value.get("path");
                String rawValue = value.get("value");
                String type = Helper.getTypeOfValueResponse(path);

                logger.info("Checking Response {} field.", path);

                Object expectedValue;
                if ("String".equals(type)) {
                    expectedValue = Helper.dataConversionString(rawValue);
                } else if ("Number".equals(type)) {
                    expectedValue = Helper.dataConversionInteger(rawValue);
                } else if ("Boolean".equals(type)) {
                    expectedValue = Helper.dataConversionBoolean(rawValue);
                } else {
                    Assert.fail("Unsupported type. path: " + path + ", type: " + type);
                    return;
                }

                Assert.assertEquals(res.get(path), expectedValue, "Path: " + path);
            }
        } catch (Exception e) {
            logger.error("Error Message: {}", e.toString(), e);
            Assert.fail();
        }
    }


    @Then("Expected to see not null control")
    public void expectedToSeeNotNullControl(DataTable dt) {
        try {
            List<String> fields = dt.asList();
            for (String value : fields) {
                logger.info("Checking Response " + value + " field.");
                Assert.assertNotNull(response.jsonPath().get(value));
            }
        } catch (Exception e) {
            logger.error("Error Message: " + e);
            Assert.fail();
        }
    }

    @Then("Response Size Control")
    public void responseSizeControl(DataTable dt) {
        try {
            List<Map<String, String>> map = dt.asMaps(String.class, String.class);
            for (Map<String, String> value : map) {
                Assert.assertEquals(response.jsonPath().getInt(value.get("path") + ".size()"), Integer.parseInt(value.get("size")));
            }
        } catch (Exception e) {
            logger.error("Error Message: " + e);
            Assert.fail();
        }

    }

    @When("Delete Mock Request Port {int}")
    public void deleteMockRequestPort(int port) {
        try {
            String mockUrl = ConfigReader.get("mock.url");
            if (port == 9999) {
                RestAssured.baseURI = mockUrl;
                mockRequest = RestAssured.given();
                mockResponse = mockRequest.when().log().all().get();
                JsonPath res = new JsonPath(mockResponse.getBody().asString());
                int a = res.getList("imposters").size();
                for (int i = 0; i < a; i++) {
                    RestAssured.baseURI = mockUrl + res.getInt("imposters[" + i + "].port") + "/savedRequests";
                    mockRequest = RestAssured.given();
                    mockResponse = mockRequest.when().log().all().delete();
                    logger.info("Deleted Mock Request Port " + res.getInt("imposters[" + i + "].port"));
                }
            } else {
                RestAssured.baseURI = mockUrl + port + "/savedRequests";
                mockRequest = RestAssured.given();
                mockResponse = mockRequest.when().log().all().delete();
                logger.info("Deleted Mock Request Port " + port);
            }


        } catch (Exception e) {
            logger.error("Error Message: " + e);
        }

    }

    @Then("Control mock requests on port {int} with")
    public void controlMockRequestsOnPortWith(int port, DataTable dt) {
        try {//index,key,value
            List<Map<String, String>> map = dt.asMaps(String.class, String.class);
            String mockUrl = ConfigReader.get("mock.url");
            RestAssured.baseURI = mockUrl + port;
            mockRequest = RestAssured.given();
            mockResponse = mockRequest.when().log().all().get();
            JsonPath jp = mockResponse.jsonPath();
            for (Map<String, String> data : map) {
                logger.info("Checking Request " + data.get("key") + " with request index value " + data.get("index"));
                Assert.assertEquals((Object) jp.get("requests[" + data.get("index") + "]." + data.get("key") + ""), data.get("value"));
            }
        } catch (Exception e) {
            logger.error("Error Message: " + e);
            Assert.fail();
        }
    }

    @Then("Control mock request headers on port {int} with")
    public void controlMockRequestHeadersOnPortWith(int port, DataTable dt) {
        try {
            List<Map<String, String>> map = dt.asMaps(String.class, String.class);
            String mockUrl = ConfigReader.get("mock.url");
            RestAssured.baseURI = mockUrl + port;
            mockRequest = RestAssured.given();
            mockResponse = mockRequest.when().log().all().get();
            JsonPath jp = mockResponse.jsonPath();
            for (Map<String, String> data : map) {
                logger.info("Checking Request Headers " + data.get("key") + " with request index value " + data.get("index"));
                Assert.assertEquals((Object) jp.get("requests[" + data.get("index") + "].headers." + data.get("key") + ""), data.get("value"));
            }
        } catch (Exception e) {
            logger.error("Error Message: " + e);
            Assert.fail();
        }
    }

    @Then("Control mock request params on port {int} with")
    public void controlMockRequestParamsOnPortWith(int port, DataTable dt) {

        try {
            List<Map<String, String>> map = dt.asMaps(String.class, String.class);
            String mockUrl = ConfigReader.get("mock.url");
            RestAssured.baseURI = mockUrl + port;
            mockRequest = RestAssured.given();
            mockResponse = mockRequest.when().log().all().get();
            JsonPath jp = mockResponse.jsonPath();
            for (Map<String, String> data : map) {
                logger.info("Checking Request Query " + data.get("key") + " with request index value " + data.get("index"));
                Assert.assertEquals((Object) jp.get("requests[" + data.get("index") + "].query." + data.get("key")), data.get("value"));
            }
        } catch (Exception e) {
            logger.error("Error Message: " + e);
            Assert.fail();
        }

    }

    @Then("Mock Request Count Equal {int} on port {int}")
    public void mockRequestCountEqualOnPort(int count, int port) {

        try {
            String mockUrl = ConfigReader.get("mock.url");
            RestAssured.baseURI = mockUrl + port;
            mockRequest = RestAssured.given();
            mockResponse = mockRequest.when().log().all().get();
            JsonPath jp = mockResponse.jsonPath();

            Assert.assertEquals(jp.getList("requests").size(), count);
            Assert.assertEquals(jp.getInt("numberOfRequests"), count);
        } catch (Exception e) {
            logger.error("Error Message: " + e);
            Assert.fail();
        }

    }

    @Then("Compare response with {string} files")
    public void compareResponseWithFiles(String path) {
        try {
            String expectedResponseString = Helper.readFileAsString("src/test/java/responses/" + path + ".json");
            DocumentContext expectedResponse = com.jayway.jsonpath.JsonPath.parse(expectedResponseString);

            String actual = PrepareRequest.response.getBody().asString();
            DocumentContext actualResponse = com.jayway.jsonpath.JsonPath.parse(actual);

            Assert.assertEquals(actualResponse.jsonString(), expectedResponse.jsonString(), "Response not matched");

        } catch (Exception e) {
            logger.error("Error while comparing response with expected file", e);
            Assert.fail("An error occurred during the response comparison");
        }
    }

    @Then("Control Response Array")
    public void controlResponseArray(DataTable dt) {

        try {
            List<Map<String, String>> map = dt.asMaps(String.class, String.class);
            JsonPath res = new JsonPath(response.getBody().asString());
            int size = res.getList("").size();

            for (Map<String, String> value : map) {

                String path = value.get("path");
                String type = Helper.getTypeOfValueResponse("[0]." + path);

                if (type.equals("String")) {
                    for (int i = 0; i < size; i++) {
                        logger.info("Checking rowIndex={}, path={}", i, path);
                        Assert.assertEquals(
                                res.get("[" + i + "]." + path),
                                Helper.dataConversionByType(type, value.get("value"))
                        );
                    }
                } else if (type.equals("Number")) {
                    for (int i = 0; i < size; i++) {
                        logger.info("Checking rowIndex={}, path={}", i, path);
                        Assert.assertEquals(
                                res.get("[" + i + "]." + path),
                                Helper.dataConversionByType(type, value.get("value"))
                        );
                    }
                } else if (type.equals("Boolean")) {
                    for (int i = 0; i < size; i++) {
                        logger.info("Checking rowIndex={}, path={}", i, path);
                        Assert.assertEquals(
                                res.get("[" + i + "]." + path),
                                Helper.dataConversionByType(type, value.get("value"))
                        );
                    }
                }
            }
        } catch (Exception e) {
            logger.error("Error Message: " + e);
            Assert.fail();
        }
    }

    @Then("Database control with {string} query")
    @Then("Database Control With {string} Query")
    public void databaseControlWithQuery(String query, DataTable dt) {
        try {
            List<Map<String, String>> map = dt.asMaps(String.class, String.class);
            net.minidev.json.JSONArray resultRows = Helper.executeQuery(query);

            Assert.assertNotNull(resultRows, "Database query result is null.");
            Assert.assertTrue(!resultRows.isEmpty(), "Database query returned no rows.");

            Object firstRowObject = resultRows.get(0);
            Assert.assertTrue(firstRowObject instanceof Map, "Unexpected query result format.");

            Map<?, ?> firstRow = (Map<?, ?>) firstRowObject;

            for (Map<String, String> row : map) {
                String key = Helper.getMapValueIgnoreCase(row, "Key");
                String expectedRaw = Helper.getMapValueIgnoreCase(row, "Value");

                Assert.assertNotNull(key, "DataTable must contain Key column.");
                logger.info("Checking DB field: {}", key);
                Assert.assertTrue(firstRow.containsKey(key), "Query result does not contain field: " + key);

                Object actualValue = firstRow.get(key);
                Object expectedValue = Helper.dataConversionByActualValue(actualValue, expectedRaw);

                Assert.assertEquals(actualValue, expectedValue, "Mismatch on field: " + key);
            }
        } catch (Exception e) {
            logger.error("Error Message: {}", e.toString(), e);
            Assert.fail();
        }
    }
}
