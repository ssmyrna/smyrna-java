package steps;

import helper.Helper;
import io.cucumber.datatable.DataTable;
import io.cucumber.java.en.Then;
import io.restassured.path.json.JsonPath;
import lombok.extern.slf4j.Slf4j;
import org.testng.Assert;

import java.util.List;
import java.util.Map;

import static steps.PrepareRequest.response;

@Slf4j
public class BaseSteps {

    @Then("Expected to see {int} status code")
    public void expectedToSeeStatusCode(int code) {
        try {
            Assert.assertEquals(code, response.statusCode());
            log.info("Actual Status Code= " + response.statusCode() + " Expected Status Code: " + code);
        } catch (Exception e) {
            log.error("Error Message: " + e);
            Assert.fail();
        }
    }

    @Then("Response Control")
    public void responseControl(DataTable dt) {
        try {
            List<Map<String, String>> map = dt.asMaps(String.class, String.class);
            JsonPath res = new JsonPath(response.getBody().asString());
            for (Map<String, String> value : map) {
                log.info("Checking Response " + value.get("path") + " field.");
                if (Helper.getTypeOfValueResponse(value.get("path")).equals("String")) {
                    Assert.assertEquals(res.get(value.get("path")), Helper.dataConversionString(value.get("value")));
                } else if (Helper.getTypeOfValueResponse(value.get("path")).equals("Number")) {
                    Assert.assertEquals(res.get(value.get("path")), Helper.dataConversionInteger(value.get("value")));
                } else if (Helper.getTypeOfValueResponse(value.get("path")).equals("Boolean")) {
                    Assert.assertEquals(res.get(value.get("path")), Helper.dataConversionBoolean(value.get("value")));
                }
            }
        } catch (Exception e) {
            log.error("Error Message: " + e);
            Assert.fail();
        }
    }

    @Then("Expected to see not null control")
    public void expectedToSeeNotNullControl(DataTable dt) {
        try {
            List<String> fields = dt.asList();
            for (String value : fields) {
                log.info("Checking Response " + value + " field.");
                Assert.assertNotNull(response.jsonPath().get(value));
            }
        } catch (Exception e) {
            log.error("Error Message: " + e);
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
        }catch (Exception e){
            log.error("Error Message: " + e);
            Assert.fail();
        }

    }
}