package it.univr.esameprogettazione;

import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.FindBy;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Date;

public class InputProjectPage extends PageObject {

    @FindBy(xpath = "//h2[contains(text(),'Nuovo Timesheet')]")
    private WebElement header;

    @FindBy(xpath = "//input[@id='ricercatore']")
    private WebElement inputNomeLavoratore;

    @FindBy(xpath = "//input[@id='CF']")
    private WebElement inputCF;

    @FindBy(xpath = "//input[@id='nomeProgetto']")
    private WebElement inputNomeProgetto;

    @FindBy(xpath = "//tbody[@id='activityTable']//tr//td//input[@type='date']")
    private WebElement inputData;

    @FindBy(xpath = "//tbody[@id='activityTable']//tr//td//input[@type='text']")
    private WebElement inputActivity;

    @FindBy(xpath = "//input[@type='number']")
    private WebElement inputHours;

    @FindBy(xpath = "//input[@type='submit']")
    private WebElement submitButton;


    public InputProjectPage(WebDriver driver) {
        super(driver);
    }

    public void enterData(String nomeLavoratore, String CF, String nomeProgetto, String activity, String data, Integer hours) {
        inputNomeLavoratore.clear();
        inputNomeLavoratore.sendKeys(nomeLavoratore);
        inputCF.clear();
        inputCF.sendKeys(CF);
        inputNomeProgetto.clear();
        inputNomeProgetto.sendKeys(nomeProgetto);
        inputActivity.clear();
        inputActivity.sendKeys(activity);
        Actions actions = new Actions(driver);
        actions.moveToElement(inputData).click().perform();
        inputData.sendKeys(data);
        inputHours.clear();
        inputHours.sendKeys(hours.toString());
    }

    public String confirmationNewProject() {
        return header.getText();
    }

    public ListPage submit() {
        submitButton.click();
        return new ListPage(driver);
    }
}
