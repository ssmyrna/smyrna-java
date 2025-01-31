package runner;

import io.cucumber.testng.AbstractTestNGCucumberTests;
import io.cucumber.testng.CucumberOptions;
import lombok.extern.slf4j.Slf4j;
import org.testng.annotations.AfterSuite;
import org.testng.annotations.BeforeSuite;

@Slf4j
@CucumberOptions(
        features = "src/test/java/features",
        glue = {"steps","helper"}, plugin = {"pretty", "json:target/cucumber-report.json"}
)
public class TestRunner extends AbstractTestNGCucumberTests {
    @BeforeSuite
    public static void beforeSuite() {
        log.info("Test Started.");
    }

    @AfterSuite
    public static void afterSuite() {
        log.info("Test Ended.");
    }
}