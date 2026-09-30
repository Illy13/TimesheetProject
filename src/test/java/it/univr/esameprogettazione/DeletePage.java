package it.univr.esameprogettazione;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;

import java.util.List;

public class DeletePage extends PageObject{
    @FindBy(xpath = "//h1[contains(text(),'LISTA PROGETTI')]")
    private WebElement header;

    @FindBy(xpath = "//table[@id='myTable']")
    private WebElement table;

    public DeletePage(WebDriver driver) {
        super(driver);
    }

    public String confirmation() {
        return header.getText();
    }

    //Conta le righe della tabella
    public List<WebElement> getRows() {
        return table.findElements(By.cssSelector("#myTable tbody tr"));
    }
}
