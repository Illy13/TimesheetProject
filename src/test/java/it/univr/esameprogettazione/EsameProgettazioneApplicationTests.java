package it.univr.esameprogettazione;

import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.MethodSorters;
import org.openqa.selenium.WebElement;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import static org.junit.Assert.*;

import java.io.File;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT, classes = EsameProgettazioneApplication.class)
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class EsameProgettazioneApplicationTests extends BaseTest {

    //------UTILITY PER AGGIUNTA DI PROGETTI-------------------------
    private void addProject(String name, String code, String project, String activity, String date, int hours) {
        AddPage addNewProjectPage = new AddPage(driver);
        InputProjectPage inputPage = addNewProjectPage.submit();
        assertEquals("Nuovo Timesheet", inputPage.confirmationNewProject());
        inputPage.enterData(name, code, project, activity, date, hours);
        inputPage.submit();
    }

    //------UTILITY PER AGGIUNTA DI ATTIVITA'-------------------------
    private void addActivity(String name, int hours, String date) {
        ListPage listPage = new ListPage(driver);
        InputActivityPage addActivityPage = listPage.submitAddActivity();
        assertEquals("Aggiungi Attività", addActivityPage.confirmationNewActivity());
        addActivityPage.enterData(name, hours, date);
        addActivityPage.submit();
    }

    @Test
    public void test10_emptyList() {
        ListPage listPage = new ListPage(driver);
        List<WebElement> rows = listPage.getRows();
        assertEquals(0, rows.size());
    }

    @Test
    //TEST PER AGGIUNGERE E INSERIRE DATI NUOVO PROGETTO
    public void test11_AddNewProject() {
        ListPage listPage = new ListPage(driver);
        addProject("John Doe","JHNDE123A", "Project 1", "Activity1","2025-01-14", 2);
        assertEquals("LISTA PROGETTI",listPage.confirmation());
        //Controllo che la tabella abbia 1 riga
        List<WebElement> rows = listPage.getRows();
        assertEquals(1, rows.size());
        //Controllo che nella riga ci sia Project 1
        List<WebElement> projectName = listPage.findProject("Project 1");
        String name = projectName.get(0).getText();
        //System.out.println("DEBUG - nome progetto: " + name);
        assertEquals("Project 1", name);
    }

    @Test
    //INSERIMENTO PROGETTO CON DATI NON CORRETTI
    public void test12_IncorrectProjectInput(){
        ErrorPage errorPage = new ErrorPage(driver);
        //inserimento giorno festivo
        addProject("Babbo Natale", "BABNAT", "Rudolf", "Regali", "2025-12-25", 8);
        assertEquals("Errore nella creazione del Timesheet", errorPage.confirmation());
        assertEquals("Non è possibile inserire attività in giorni non lavorativi (sabato, domenica o festivi).", errorPage.getErrorText());
        errorPage.returnToListPage();
        //inserimento giorno non lavorativo
        addProject("Micky Mouse", "MKYMOS", "Topolino", "Disney", "2025-02-15", 3);
        assertEquals("Errore nella creazione del Timesheet", errorPage.confirmation());
        assertEquals("Non è possibile inserire attività in giorni non lavorativi (sabato, domenica o festivi).", errorPage.getErrorText());
        errorPage.returnToListPage();
        //inserimento ore = 0
        addProject("Micky Mouse", "MKYMOS", "Topolino", "Disney", "2025-02-14", 0);
        assertEquals("Errore nella creazione del Timesheet", errorPage.confirmation());
        assertEquals("Il totale delle ore lavorate in 2025-02-14 deve essere compreso tra 1 e 8. Totale inserito: 0", errorPage.getErrorText());
    }

    @Test
    //TEST PER MODIFICARE UN PROGETTO
    public void test13_EditProject() {
        ListPage listPage = new ListPage(driver);
        EditPage editPage = listPage.submitEdit();
        assertEquals("Modifica Timesheet", editPage.confirmationEdit());
        //Modifica dati
        editPage.editTimesheet("Nuovo Progetto", 2);
        ListPage editedListPage = editPage.submit();
        assertEquals("LISTA PROGETTI",editedListPage.confirmation());
        //Controllo che nella tabella ci sia il progetto con il nome aggiornato
        List<WebElement> projectName = listPage.findProject("Nuovo Progetto");
        String name = projectName.get(0).getText();
        //System.out.println("DEBUG - nome progetto: " + name);
        assertEquals("Nuovo Progetto", name);
    }

    @Test
    //TEST MODIFICA PROGETTO CON INSERIMENTO DI ORE GIORNALIERE MAGGIORE DI 8
    public void test14_Max8HoursEditInput(){
        addActivity("Activity1.2", 2, "2025-01-14");
        ShowPage showPage = new ShowPage(driver);
        assertEquals("Dettaglio Timesheet", showPage.confirmationShow());
        ListPage listPage = showPage.returnToList();
        EditPage editPage = listPage.submitEdit();
        assertEquals("Modifica Timesheet", editPage.confirmationEdit());
        //Modifica dati inserendo numero di ore che eccedono 8
        editPage.editTimesheet("Nuovo Progetto", 7);
        editPage.submit();
        ErrorPage errorPage = new ErrorPage(driver);
        assertEquals("Errore nella creazione del Timesheet", errorPage.confirmation());
        assertEquals("Il totale delle ore lavorate in 2025-01-14 deve essere compreso tra 1 e 8. Totale inserito: 9", errorPage.getErrorText());
    }

    @Test
    //TEST ORDINA PER PROGETTO
    public void test15_SortProjectsForName(){
        ListPage listPage = new ListPage(driver);
        //inserisce più progetti nella lista
        addProject("Donald Duck", "DNDDUK", "Space project", "Activity4", "2024-10-15", 5);
        addProject("Clark Kent", "CLKKET", "Car project", "Activity3", "2024-11-15", 2);
        addProject("Lois Lane", "LOSLNE", "Angular project", "Activity2", "2025-01-15", 2);
        listPage.sortByName();
        String expectedTableValues = """
                Nome progetto Azioni
                Angular project Mostra Modifica + Attività Richiedi Approvazione Elimina
                Car project Mostra Modifica + Attività Richiedi Approvazione Elimina
                Nuovo Progetto Mostra Modifica + Attività Richiedi Approvazione Elimina
                Space project Mostra Modifica + Attività Richiedi Approvazione Elimina""";
        String tableText = listPage.getTableText();
        //System.out.println("DEBUG - table text" + "\n"+ tableText);
        //System.out.println("DEBUG - expected values" + "\n"+ expectedTableValues);
        assertEquals(expectedTableValues, tableText);
    }

    @Test
    //TEST PER ELIMARE PROGETTO
    public void test16_DeleteProject() throws InterruptedException {
        ListPage listPage = new ListPage(driver);
        List<WebElement> rowsBefore = listPage.getRows();
        //System.out.println("DEBUG - rowsBefore: " + rowsBefore.size());
        DeletePage deletePage = listPage.submitDelete();
        assertEquals("LISTA PROGETTI", deletePage.confirmation());
        List<WebElement> rowsAfter = deletePage.getRows();
        //System.out.println("DEBUG - rowsAfter: " + rowsAfter.size());
        assertEquals((rowsBefore.size() - 1), rowsAfter.size());
    }

    @Test
    //TEST PER VERIFICARE IL MOSTRA TIMESHEET
    public void test17_ShowProject(){
        ListPage listPage = new ListPage(driver);
        ShowPage showPage = listPage.submitShow();
        assertEquals("Dettaglio Timesheet", showPage.confirmationShow());
        String tableText = showPage.getTableText();
        String expectedTableValues = """
                Attività 15 ottobre 2024 Ore Totali
                Activity4 5 5
                Totale Ore 5""";
        //System.out.println("DEBUG - Mostra: " + "\n" + tableText);
        assertEquals(expectedTableValues, tableText);
    }

    @Test
    //TEST AGGIUNTA ATTIVITA'
    public void test18_AddActivity(){
        addActivity("Activity4: part2", 4, "2025-02-13");
        ShowPage showPage = new ShowPage(driver);
        assertEquals("Dettaglio Timesheet", showPage.confirmationShow());
        String tableText = showPage.getTableText();
        String expectedTableValues = """
                Attività 15 ottobre 2024 13 febbraio 2025 Ore Totali
                Activity4 5 0 5
                Activity4: part2 0 4 4
                Totale Ore 9""";
        //System.out.println("DEBUG - Mostra: " + "\n" + tableText);
        assertEquals(expectedTableValues, tableText);
    }

    @Test
    //TEST AGGIUNTA ATTIVITA' PER UN TOTALE DI ORE CHE SUPERANO IL LIMITE DI 8 GIORNALIERE
    public void test19_IncorrectActivityInput(){
        ErrorPage errorPage = new ErrorPage(driver);
        addActivity("Activity4: part2", 6, "2025-02-13");
        try {
            Thread.sleep(3000); // Aspetta 5 secondi
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        assertEquals("Errore nella creazione del Timesheet", errorPage.confirmation());
        assertEquals("Il totale delle ore lavorate in 2025-02-13 supera il massimo di 8 ore. Totale tentato: 10", errorPage.getErrorText());
    }

    @Test
    //CONFRONTA PROGETTI
    public void test20_CompareProjects(){
        ListPage listPage = new ListPage(driver);
        ComparePage comparePage = listPage.compareTimesheet();
        assertEquals("Confronto Timesheet", comparePage.confirmation());
        String tableText = comparePage.getTableText();
        String expectedTableValues = """
                Timesheet Ore Totali
                Space project 9
                Car project 2""";
        assertEquals(expectedTableValues, tableText);
        System.out.println("DEBUG - Compare Mostra Tabella: " + "\n" + tableText);
    }

    @Test
    //TEST CERCA ATTIVITA' PER DATA
    public void test21_SortActivityForHours(){
        addActivity("Activity 1", 2, "2025-02-14");
        ShowPage showPage = new ShowPage(driver);
        assertEquals("Dettaglio Timesheet", showPage.confirmationShow());
        showPage.sortByHours();
        String tableText = showPage.getTableText();
        String expectedTableValues = """
                Attività 15 ottobre 2024 13 febbraio 2025 14 febbraio 2025 Ore Totali
                Activity 1 0 0 2 2
                Activity4: part2 0 4 0 4
                Activity4 5 0 0 5
                Totale Ore 11""";
        //System.out.println("DEBUG - Tabella:" + "\n" + tableText);
        assertEquals(expectedTableValues, tableText);
    }

    //---------UTIILITY PER CERCARE L'ULTIMO FILE NEI DOWNLOADS-------
    public static Optional<File> getLatestFile(String directoryPath) {
        File directory = new File(directoryPath);
        if (!directory.exists() || !directory.isDirectory()) {
            return Optional.empty();
        }
        return Arrays.stream(directory.listFiles()) // Ottieni lista file
                .filter(File::isFile) // Filtra solo i file (no cartelle)
                .max(Comparator.comparingLong(File::lastModified)); // Trova il file più recente
    }

    @Test
    //TEST PER VERIFICARE IL DOWNLOAD
    public void test22_download(){
        ListPage listPage = new ListPage(driver);
        ShowPage showPage = listPage.submitShow();
        assertEquals("Dettaglio Timesheet", showPage.confirmationShow());
        showPage.download();
        String downloadFolder = System.getProperty("user.home") + "/Downloads";
        // Ottieni il file più recente
        Optional<File> latestFile = getLatestFile(downloadFolder);
        // Controlla se il file è stato scaricato
        assertTrue(String.format("Il file %s non è stato trovato!", latestFile.map(File::getName).orElse("Nessun file")),latestFile.isPresent());
        // Stampa il file trovato
        latestFile.ifPresent(file -> System.out.println("Ultimo file scaricato: " + file.getAbsolutePath()));

        /*
        * Versione sempliciotta
        * //Se il file si trova in una cartella specifica (es. Downloads)
        String userHome = System.getProperty("user.home");
        File file = new File(userHome + "/Downloads/timesheet.xlsx");
        System.out.println("DEBUG - Percorso assoluto: " + file.getAbsolutePath());
        Boolean isPresent = file.exists();
        //Fallisce se isPresent = False i.e. il file non è presente
        assertTrue("Il file timesheet.xlsx non è stato trovato!", isPresent);*/
    }

    @Test
    //TEST RICERCA PROGETTO PER NOME
    public void test23_SearchProjectForName(){
        ListPage listPage = new ListPage(driver);
        listPage.search("Space project");
        String tableText = listPage.getTableText();
        String expectedTableValues = """
                Nome progetto Azioni
                Space project Mostra Modifica + Attività Richiedi Approvazione Elimina""";
        //System.out.println("DEBUG - Search Mostra Tabella: " + "\n" + tableText);
        assertEquals(expectedTableValues, tableText);
    }

    @Test
    //TEST RICERCA PROGETTO INSERENDO UN PROGETTO NON PRESENTE
    public void test24_SearchIncorrectProjectForName(){
        ListPage listPage = new ListPage(driver);
        //Inserimento nome progetto errato
        listPage.search("Incorrect project");
        String tableText = listPage.getTableText();
        String expectedTableValues = """
                Nome progetto Azioni""";
        //System.out.println("DEBUG - Search Mostra Tabella: " + "\n" + tableText);
        assertEquals(expectedTableValues, tableText);

        //Inserimento nome progetto stringa vuota
        listPage.search("");
        String tableEmptyText = listPage.getTableText();
        String expectedEmptyTableValues = """
                Nome progetto Azioni
                Space project Mostra Modifica + Attività Richiedi Approvazione Elimina
                Car project Mostra Modifica + Attività Richiedi Approvazione Elimina
                Angular project Mostra Modifica + Attività Richiedi Approvazione Elimina""";
        //System.out.println("DEBUG - THIISSS Search Mostra Tabella: " + "\n" + tableEmptyText);
        assertEquals(expectedEmptyTableValues, tableEmptyText);
    }

    @Test
    //TEST RICHIESTA APPROVAZIONE
    public void test25_GetApproval(){
        ListPage listPage = new ListPage(driver);
        ApprovePage approvePage = listPage.submitApprove();
        assertEquals("Riepilogo", approvePage.confirmation());
        approvePage.submitApprove();
        assertEquals("Richiesta già inviata", approvePage.confirmationFlag());
    }


}




