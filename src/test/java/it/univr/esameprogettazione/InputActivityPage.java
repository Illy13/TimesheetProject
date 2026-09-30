package it.univr.esameprogettazione;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.FindBy;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.HashSet;
import java.util.Random;
import java.util.Set;

public class InputActivityPage extends PageObject {
    @FindBy(xpath = "//h2[contains(text(),'Aggiungi Attività')]")
    private WebElement header;

    @FindBy(xpath = "//input[@type='text']")
    private WebElement inputNomeAttivita;

    @FindBy(xpath = "//input[@type='number']")
    private WebElement inputOre;

    @FindBy(xpath = "//input[@type='date']")
    private WebElement inputData;

    @FindBy(xpath = "//input[@type='submit']")
    private WebElement submitButton;


    public InputActivityPage(WebDriver driver) {
        super(driver);
    }

    public String confirmationNewActivity() {
        return header.getText();
    }

    public void enterData(String nomeAttivita, Integer hours, String data) {
        inputNomeAttivita.clear();
        inputNomeAttivita.sendKeys(nomeAttivita);
        inputOre.clear();
        inputOre.sendKeys(hours.toString());
        Actions actions = new Actions(driver);
        actions.moveToElement(inputData).click().perform();
        inputData.sendKeys(data);
    }


    public ShowPage submit() {
        submitButton.click();
        return new ShowPage(driver);
    }


}
