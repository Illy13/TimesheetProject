package it.univr.esameprogettazione;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;

public class ShowPage extends PageObject{

    @FindBy(xpath = "//h2[contains(text(),'Dettaglio Timesheet')]")
    private WebElement header;

    @FindBy(xpath = "//table[@id='myTable']")
    private WebElement table;

    @FindBy(xpath = "//a[contains(text(),'Torna alla lista')]")
    private WebElement returnToList;

    @FindBy(xpath = "//a[@class='btn btn-sortHours']")
    private WebElement sortButton;

    @FindBy(xpath = "//button[@class='btn btn-download']")
    private WebElement downloadButton;

    public ShowPage(WebDriver driver) {
        super(driver);
    }

    public String confirmationShow() {
        return header.getText();
    }

    public String getTableText(){
        return table.getText();
    }

    public void sortByHours(){
        sortButton.click();
    }

    public void download(){
        downloadButton.click();
        try {
            Thread.sleep(2000); // Aspetta 5 secondi che venga completato il download
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public ListPage returnToList() {
        returnToList.click();
        return new ListPage(driver);
    }
}
