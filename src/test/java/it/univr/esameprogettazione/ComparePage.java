package it.univr.esameprogettazione;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;

public class ComparePage extends PageObject {

    @FindBy(xpath = "//h2[@class='container']")
    public WebElement header;

    @FindBy(xpath = "//body/div[@class='container']/table[2]")
    public WebElement table;

    //costruttore
    public ComparePage(WebDriver driver) {
        super(driver);
    }

    public String confirmation() {
        return header.getText();
    }

    public String getTableText(){
        return table.getText();
    }


}
