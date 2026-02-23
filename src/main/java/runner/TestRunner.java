package runner;

import io.cucumber.testng.AbstractTestNGCucumberTests;
import io.cucumber.testng.CucumberOptions;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.annotations.AfterSuite;
import org.testng.annotations.BeforeSuite;
import steps.BaseSteps;


@CucumberOptions(
        features = "src/test/java/features",
        glue = {"io.github.ssmyrna.steps","io.github.ssmyrna.helper"}, plugin = {"pretty", "json:target/cucumber-report.json"}
)
public class TestRunner extends AbstractTestNGCucumberTests {
    private static final Logger logger = LoggerFactory.getLogger(BaseSteps.class);

    @BeforeSuite
    public static void beforeSuite() {
        logger.info("Test Started.");
    }
//TODO test1
    @AfterSuite
    public static void afterSuite() {
        logger.info("Test Ended.");
    }
}