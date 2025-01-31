package helper;

import com.jayway.jsonpath.DocumentContext;
import com.jayway.jsonpath.JsonPath;
import io.cucumber.java.After;
import io.cucumber.java.Before;
import io.cucumber.java.Scenario;
import lombok.extern.slf4j.Slf4j;
import steps.PrepareRequest;

import java.nio.file.Files;
import java.nio.file.Paths;

@Slf4j
public class Helper {

    public static io.restassured.path.json.JsonPath type;
    public static DocumentContext jo;


    public static void jsonEdit(String path) throws Exception {
        try {
            type = new io.restassured.path.json.JsonPath(readFileAsString("src/test/java/objects/" + path + ".json"));
            jo = JsonPath.parse(readFileAsString("src/test/java/objects/" + path + ".json"));
        } catch (Exception e) {
            log.error("Error Message: " + e);
        }
    }


    public static String getTypeOfValue(String field) {
        try {
            if (type.get(field).getClass().getName().equals("java.lang.Boolean"))
                return "Boolean";
            if (type.get(field).getClass().getName().equals("java.lang.String"))
                return "String";
            if (type.get(field).getClass().getName().equals("java.lang.Integer")) {
                return "Number";
            }
            return "Wrong";
        } catch (Exception e) {
            log.error("Error Message: " + e);
            return "Fail";
        }
    }

    public static String getTypeOfValueResponse(String field) {
        try {
            io.restassured.path.json.JsonPath res = new io.restassured.path.json.JsonPath(PrepareRequest.response.getBody().asString());
            if (res.get(field).getClass().getName().equals("java.lang.Boolean"))
                return "Boolean";
            if (res.get(field).getClass().getName().equals("java.lang.String"))
                return "String";
            if (res.get(field).getClass().getName().equals("java.lang.Integer")) {
                return "Number";
            }
            return "Wrong";
        } catch (Exception e) {
            log.error("Error Message: " + e);
            return "Fail";
        }


    }

    public static String readFileAsString(String file) throws Exception {
        try {
            return new String(Files.readAllBytes(Paths.get(file)));
        } catch (Exception e) {
            log.error("Error Message: " + e);
            return null;
        }

    }

    public static String dataConversionString(String value) {
        try {
            switch (value) {
                case "null":
                    return null;
                case "whiteSpace":
                    return "      ";
                case "noString":
                    return "";
                default:
                    return value;
            }
        } catch (Exception e) {
            log.error("Error Message: " + e);
            return null;
        }

    }

    public static Integer dataConversionInteger(String value) {
        try {
            if (value.equals("null")) {
                return null;
            } else
                return Integer.valueOf(value);
        } catch (Exception e) {
            log.error("Error Message: " + e);
            return null;
        }
    }

    public static Boolean dataConversionBoolean(String value) {
        try {
            if (value.equals("null")) {
                return null;
            } else
                return Boolean.valueOf(value);
        } catch (Exception e) {
            log.error("Error Message: " + e);
            return null;
        }
    }

    @Before
    public static void before(Scenario s) {
        log.info(s.getName() + " Scenario started.");
    }

    @After
    public static void after(Scenario s) {
        log.info(s.getName() + " Scenario ended.");
    }
}