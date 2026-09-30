package it.univr.esameprogettazione;

import org.openqa.selenium.Alert;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;

import static org.junit.Assert.assertEquals;

public class ApprovePage extends PageObject {

    @FindBy(xpath = "//h2[contains(text(),'Riepilogo')]")
    private WebElement header;

    @FindBy (xpath = "//button[@type='submit']")
    private WebElement approveButton;

    @FindBy(xpath = "//span[@style='color: darkgreen; text-align: center; font-weight: bold;']")
    private WebElement flagApprove;

    public ApprovePage(WebDriver driver) {
        super(driver);
    }

    public String confirmation() {
        return header.getText();
    }

    public void submitApprove() {
        approveButton.click();
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));
        wait.until(ExpectedConditions.alertIsPresent());

        // Accetta il pop-up
        Alert confirmationAlert = driver.switchTo().alert();
        String confirmationText = confirmationAlert.getText();
        assertEquals("Richiesta inviata con successo", confirmationText);
        confirmationAlert.accept();
        try {
            Thread.sleep(3000); // Aspetta 3 secondi
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public String confirmationFlag() {
        return flagApprove.getText();
    }

}
