package it.univr.esameprogettazione;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;

public class EditPage extends PageObject{
    //trova l'header della pagina dove si atterra
    @FindBy(xpath = "//h2[contains(text(),'Modifica Timesheet')]")
    private WebElement header;

    @FindBy(xpath = "//input[@id='nome-progetto']")
    private WebElement insertProjectName;

    @FindBy(xpath = "//input[@id='attivita-ore']")
    private WebElement insertActivityHours;

    @FindBy(xpath = "//input[@type='submit']")
    private WebElement submit;

    public EditPage(WebDriver driver) {
        super(driver);
    }

    //Ritorna la scritta "Modifica Timesheet" (dove atterra dopo che viene premuto il bottone edit)
    public String confirmationEdit() {
        return header.getText();
    }

    public void editTimesheet(String nomeProgetto, Integer oreAttivita ) {
        insertProjectName.clear();
        insertProjectName.sendKeys(nomeProgetto);
        insertActivityHours.clear();
        insertActivityHours.sendKeys(oreAttivita.toString());
    }

    public ListPage submit() {
        this.submit.click();
        return new ListPage(driver);
    }
}
