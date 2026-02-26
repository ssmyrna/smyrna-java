package helper;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.jayway.jsonpath.DocumentContext;
import com.jayway.jsonpath.JsonPath;
import config.ConfigReader;
import io.cucumber.java.After;
import io.cucumber.java.Before;
import io.cucumber.java.Scenario;
import net.minidev.json.JSONArray;
import net.minidev.json.JSONValue;
import org.testng.Assert;
import steps.PrepareRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.file.Files;
import java.nio.file.Paths;
import java.sql.*;
import java.util.Map;

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

    public static JSONArray executeQuery(String query) {
        String connectionString = ConfigReader.get("db.connection.string");

        if (connectionString != null) {
            connectionString = connectionString.trim();
            if (connectionString.startsWith("\"") && connectionString.endsWith("\"") && connectionString.length() > 1) {
                connectionString = connectionString.substring(1, connectionString.length() - 1);
            }
        }

        try {
            Class.forName("com.microsoft.sqlserver.jdbc.SQLServerDriver");
        } catch (ClassNotFoundException e) {
            logger.error("SQL Server JDBC driver not found in runtime classpath.");
            return new JSONArray();
        }

        try (Connection connection = DriverManager.getConnection(connectionString);
             Statement statement = connection.createStatement();
             ResultSet resultSet = statement.executeQuery(query)) {
            return resultSetToJsonArray(resultSet);
        } catch (SQLException e) {
            logger.error("Error Message: {} | Connection: {}", String.valueOf(e), maskPassword(connectionString));
            return new JSONArray();
        }
    }

    private static String maskPassword(String connectionString) {
        if (connectionString == null) {
            return null;
        }

        return connectionString.replaceAll("(?i)(password=)[^;]*", "$1***");
    }

    public static JSONArray resultSetToJsonArray(ResultSet resultSet) throws SQLException {
        ObjectMapper mapper = new ObjectMapper();
        ArrayNode arrayNode = mapper.createArrayNode();
        ResultSetMetaData metaData = resultSet.getMetaData();
        int columnCount = metaData.getColumnCount();
        while (resultSet.next()) {
            ObjectNode objectNode = mapper.createObjectNode();
            for (int i = 1; i <= columnCount; i++) {
                String columnName = metaData.getColumnName(i);
                Object columnValue = resultSet.getObject(i);
                objectNode.putPOJO(columnName, columnValue);
            }
            arrayNode.add(objectNode);
        }
        logger.info(arrayNode.toString());
        Object parsed = JSONValue.parse(arrayNode.toString());

        if (parsed instanceof JSONArray) {
            return (JSONArray) parsed;
        }

        return new JSONArray();
    }

    public static Object dataConversionByActualValue(Object actualValue, String value) {
        if (actualValue == null) {
            return dataConversionString(value);
        }

        try {
            if (actualValue instanceof Boolean) {
                return dataConversionBoolean(value);
            }

            if (actualValue instanceof Integer) {
                return dataConversionInteger(value);
            }

            if (actualValue instanceof Long) {
                return "null".equals(value) ? null : Long.valueOf(value);
            }

            if (actualValue instanceof Float) {
                return "null".equals(value) ? null : Float.valueOf(value);
            }

            if (actualValue instanceof Double) {
                return "null".equals(value) ? null : Double.valueOf(value);
            }

            if (actualValue instanceof java.math.BigDecimal) {
                return "null".equals(value) ? null : new java.math.BigDecimal(value);
            }

            return dataConversionString(value);
        } catch (Exception e) {
            logger.error("Error Message: {}", String.valueOf(e));
            return null;
        }
    }

    public static String getMapValueIgnoreCase(Map<String, String> map, String key) {
        for (Map.Entry<String, String> entry : map.entrySet()) {
            if (entry.getKey() != null && entry.getKey().equalsIgnoreCase(key)) {
                return entry.getValue();
            }
        }

        return null;
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
