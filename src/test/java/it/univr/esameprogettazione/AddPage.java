package it.univr.esameprogettazione;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;

public class AddPage extends PageObject {

    @FindBy(xpath = "//a[contains(text(),'Aggiungi nuovo timesheet')]")
    private WebElement addNewTimesheetButton;

    public AddPage(WebDriver driver) {
        super(driver);
    }

    public InputProjectPage submit() {
        this.addNewTimesheetButton.click();
        return new InputProjectPage(driver);
    }
}
