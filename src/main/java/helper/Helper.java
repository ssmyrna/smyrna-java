package helper;

import com.jayway.jsonpath.DocumentContext;
import com.jayway.jsonpath.JsonPath;
import io.cucumber.java.After;
import io.cucumber.java.Before;
import io.cucumber.java.Scenario;
import org.testng.Assert;
import steps.PrepareRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.file.Files;
import java.nio.file.Paths;

public class Helper {
    private static final Logger logger = LoggerFactory.getLogger(Helper.class);
    public static io.restassured.path.json.JsonPath type;
    public static DocumentContext jo;


    public static void jsonEdit(String path) throws Exception {
        try {
            type = new io.restassured.path.json.JsonPath(readFileAsString("src/test/java/requests/" + path + ".json"));
            jo = JsonPath.parse(readFileAsString("src/test/java/requests/" + path + ".json"));
        } catch (Exception e) {
            logger.error("Error Message: {}", String.valueOf(e));
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
            logger.error("Error Message: {}", String.valueOf(e));
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
            logger.error("Error Message: {}", String.valueOf(e));
            Assert.fail();
            return "Fail";
        }


    }

    public static String readFileAsString(String file) throws Exception {
        try {
            return new String(Files.readAllBytes(Paths.get(file)));
        } catch (Exception e) {
            logger.error("Error Message: {}", String.valueOf(e));
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
            logger.error("Error Message: {}", String.valueOf(e));
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
            logger.error("Error Message: {}", String.valueOf(e));
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
            logger.error("Error Message: {}", String.valueOf(e));
            return null;
        }
    }

    public static Object dataConversionByType(String type, String value) {
        if ("String".equals(type)) {
            return dataConversionString(value);
        } else if ("Number".equals(type)) {
            return dataConversionInteger(value);
        } else if ("Boolean".equals(type)) {
            return dataConversionBoolean(value);
        }
        return value;
    }

    @Before
    public static void before(Scenario s) {
        logger.info(s.getName() + " Scenario started.");
    }

    @After
    public static void after(Scenario s) {
        logger.info(s.getName() + " Scenario ended.");
    }
}