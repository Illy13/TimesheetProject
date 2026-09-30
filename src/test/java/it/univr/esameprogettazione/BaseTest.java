package it.univr.esameprogettazione;

import io.github.bonigarcia.wdm.WebDriverManager;
import jdk.jfr.Timespan;
import org.junit.After;
import org.junit.Before;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.firefox.FirefoxDriver;

import java.util.concurrent.TimeUnit;

public class BaseTest {

    protected WebDriver driver;

    @Before
    public void setUp() {
        // Configura e inizializza WebDriverManager per Firefox
        WebDriverManager.firefoxdriver().setup();
        driver = new FirefoxDriver();
        //driver.manage().timeouts().implicitlyWait(5, TimeUnit.SECONDS);
        //visita la mia pagina web
        driver.get("http://localhost:8080/list");
    }

    @After
    public void tearDown() {
        if (driver != null) {
            driver.quit();
        }
    }
}
