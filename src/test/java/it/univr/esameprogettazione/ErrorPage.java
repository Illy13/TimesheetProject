package it.univr.esameprogettazione;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;

public class ErrorPage extends PageObject{

    @FindBy(xpath = "//h2[@style='color: red;']")
    private WebElement header;

    @FindBy(xpath = "//p[@id='error']")
    private WebElement errorMessage;

    @FindBy(xpath = "//a[contains(text(),'Torna alla pagina principale')]")
    private WebElement returnButton;

    public ErrorPage(WebDriver driver) {
        super(driver);
    }

    public String confirmation() {
        return header.getText();
    }

    public String getErrorText(){
        return errorMessage.getText();
    }

    public ListPage returnToListPage() {
        this.returnButton.click();
        return new ListPage(driver);
    }
}
