package it.univr.esameprogettazione;

import org.openqa.selenium.Alert;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.Select;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;
import java.util.List;
import java.util.concurrent.TimeUnit;

import static org.junit.Assert.assertEquals;

public class ListPage extends PageObject{

    //trova l'header della pagina dove si atterra
    @FindBy(xpath = "//h1[contains(text(),'LISTA PROGETTI')]")
    private WebElement header;

    @FindBy(xpath = "//button[contains(text(),'Ordina per progetto')]")
    private WebElement sortByNameButton;

    @FindBy(xpath = "//input[@id='searchProject']")
    private WebElement searchTextField;

    @FindBy(xpath = "//button[contains(text(),'Cerca')]")
    private WebElement searchButton;

    //trova la tabella con la lista
    @FindBy(xpath = "//table[@id='myTable']")
    private WebElement table;

    @FindBy(xpath = "//a[@class='btn btn-show']")
    private WebElement showButton;

    @FindBy(xpath = "//a[@class='btn btn-edit']")
    private WebElement editButton;

    @FindBy(xpath = "//a[@class='btn btn-delete']")
    private WebElement deleteButton;

    @FindBy(xpath = "//a[@class='btn btn-addActivity']")
    private WebElement addActivity;

    @FindBy(xpath = "//a[@class='btn btn-approve']")
    private WebElement approveButton;

    @FindBy(xpath = "//select[@id='timesheet1']")
    private WebElement compareTimesheet1;

    @FindBy(xpath = "//select[@id='timesheet2']")
    private WebElement compareTimesheet2;

    @FindBy(xpath = "//button[contains(text(),'Confronta')]")
    private WebElement compareButton;

    //costruttore
    public ListPage(WebDriver driver) {
        super(driver);
    }

    //Ritorna la scritta "LISTA PROGETTI" (dove atterra dopo che viene inserita un record)
    public String confirmation() {
        return header.getText();
    }

    //Conta le righe della tabella
    public List<WebElement> getRows() {
        return table.findElements(By.cssSelector("#myTable tbody tr"));
    }

    public void sortByName(){
        sortByNameButton.click();
    }

    public void search(String nomeProgetto){
        searchTextField.sendKeys(nomeProgetto);
        searchButton.click();
    }

    public String getTableText(){
        return table.getText();
    }

    //Guarda che nella tabella ci sia il record con Project 1
    public List<WebElement> findProject(String nomeProgetto) {
        WebElement projectRow = table.findElement(By.xpath(String.format("//tr[td[contains(.,'%s')]]", nomeProgetto)));
        return projectRow.findElements(By.tagName("td"));
    }

    public ShowPage submitShow(){
        this.showButton.click();
        return new ShowPage(driver);
    }

    public EditPage submitEdit() {
        editButton.click();
        return new EditPage(driver);
    }

    public InputActivityPage submitAddActivity() {
        addActivity.click();
        return new InputActivityPage(driver);
    }

    public ApprovePage submitApprove() {
        approveButton.click();
        return new ApprovePage(driver);
    }

    public DeletePage submitDelete() {
        deleteButton.click();
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));
        wait.until(ExpectedConditions.alertIsPresent());

        // Accetta il pop-up
        Alert confirmationAlert = driver.switchTo().alert();
        String confirmationText = confirmationAlert.getText();
        assertEquals("Sei sicuro di voler eliminare questo timesheet?", confirmationText);
        confirmationAlert.accept();
        try {
            Thread.sleep(3000); // Aspetta 5 secondi
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return new DeletePage(driver);
    }

    public ComparePage compareTimesheet(){
        Select timeSheet1 = new Select(compareTimesheet1);
        timeSheet1.selectByVisibleText("Space project");
        Select timeSheet2 = new Select(compareTimesheet2);
        timeSheet2.selectByVisibleText("Car project");
        compareButton.click();
        return new ComparePage(driver);
    }

}
