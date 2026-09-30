package it.univr.esameprogettazione;

import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import static org.junit.Assert.*;

public class UnitTest {

    private Timesheet timesheet;
    private Timesheet nuovoTimesheet;
    private Attivita attivita1;
    private Attivita attivita2;

    @Before
    public void setup() {
        timesheet = new Timesheet("Mario Rossi", "Progetto A", "RSSMRA80A01H501Z", new Date());
        nuovoTimesheet = new Timesheet("Luca Bianchi", "Progetto B", "BNCLCU85M01H501X", new Date());
        attivita1 = new Attivita("Analisi", timesheet);
        attivita2 = new Attivita("Sviluppo", timesheet);
    }

    /* Test per verificare che sia possibile impostare l'ID del Timesheet */
    @Test
    public void testSetId() {
        timesheet.setId(100L);
        assertEquals(Long.valueOf(100), timesheet.getId());
    }

    /* Test per la creazione di un Timesheet */
    @Test
    public void testTimesheetCreation() {
        assertNotNull(timesheet);
        assertEquals("Mario Rossi", timesheet.getNomeLavoratore());
        assertEquals("Progetto A", timesheet.getNomeProgetto());
        assertEquals("RSSMRA80A01H501Z", timesheet.getCodiceFiscale());
    }

    /* Test per verificare che un Timesheet contenga Attività */
    @Test
    public void testTimesheetContainsAttivita() {
        timesheet.getAttivita().add(attivita1);
        timesheet.getAttivita().add(attivita2);

        assertEquals(2, timesheet.getAttivita().size());
        assertTrue(timesheet.getAttivita().contains(attivita1));
        assertTrue(timesheet.getAttivita().contains(attivita2));
    }

    /* Test per modificare il nome del progetto */
    @Test
    public void testChangeProjectName() {
        timesheet.setNomeProgetto("Nuovo Progetto B");
        assertEquals("Nuovo Progetto B", timesheet.getNomeProgetto());
    }

    /* Test per inviare una richiesta di approvazione */
    @Test
    public void testRichiestaInviata() {
        assertFalse(timesheet.isRichiestaInviata());
        timesheet.setRichiestaInviata(true);
        assertTrue(timesheet.isRichiestaInviata());
    }

    @Test
    public void testMultipleAttivitaInTimesheet() {
        List<Attivita> attivitaList = new ArrayList<>();
        attivitaList.add(new Attivita("Analisi Requisiti", timesheet));
        attivitaList.add(new Attivita("Sviluppo Backend", timesheet));
        attivitaList.add(new Attivita("Test e Debugging", timesheet));
        timesheet.setAttivita(attivitaList);
        assertEquals(3, timesheet.getAttivita().size());
    }

    @Test
    public void testRemoveNonExistingOreLavorate() {
        Date data = new Date();
        attivita1.removeOreLavorate(data);
        assertFalse(attivita1.getOreLavorateMap().containsKey(data));
    }

    @Test
    public void testModificaNomeAttivita() {
        attivita1.setNomeAttivita("Design UI");
        assertEquals("Design UI", attivita1.getNomeAttivita());
    }

    /* Test per eliminare un'Attività da un Timesheet */
    @Test
    public void testRemoveAttivitaFromTimesheet() {
        timesheet.getAttivita().add(attivita1);
        timesheet.getAttivita().remove(attivita1);
        assertFalse(timesheet.getAttivita().contains(attivita1));
    }

    /* Test per verificare che il metodo getDataOdierna() ritorni la data corretta */
    @Test
    public void testGetDataOdierna() {
        assertNotNull(timesheet.getDataOdierna());
    }

    /* Test per verificare che la data del timesheet venga impostata correttamente */
    @Test
    public void testTimesheetDataOdierna() {
        assertNotNull(timesheet);
        assertNotNull(timesheet.getDataOdierna());

        Calendar cal1 = Calendar.getInstance();
        cal1.setTime(timesheet.getDataOdierna());
        cal1.set(Calendar.HOUR_OF_DAY, 0);
        cal1.set(Calendar.MINUTE, 0);
        cal1.set(Calendar.SECOND, 0);
        cal1.set(Calendar.MILLISECOND, 0);

        Calendar cal2 = Calendar.getInstance();
        cal2.set(Calendar.HOUR_OF_DAY, 0);
        cal2.set(Calendar.MINUTE, 0);
        cal2.set(Calendar.SECOND, 0);
        cal2.set(Calendar.MILLISECOND, 0);

        assertEquals(cal1.getTime(), cal2.getTime());
    }

    /* Test per verificare che sia possibile sostituire l'intera lista di attività */
    @Test
    public void testSetAttivita() {
        List<Attivita> nuoveAttivita = new ArrayList<>();
        nuoveAttivita.add(new Attivita("Testing", timesheet));
        nuoveAttivita.add(new Attivita("Deployment", timesheet));
        timesheet.setAttivita(nuoveAttivita);

        assertEquals(2, timesheet.getAttivita().size());
        assertEquals("Testing", timesheet.getAttivita().get(0).getNomeAttivita());
        assertEquals("Deployment", timesheet.getAttivita().get(1).getNomeAttivita());
    }

    /* Test per verificare che la richiesta di approvazione possa essere reimpostata */
    @Test
    public void testResetRichiestaInviata() {
        timesheet.setRichiestaInviata(true);
        assertTrue(timesheet.isRichiestaInviata());

        timesheet.setRichiestaInviata(false);
        assertFalse(timesheet.isRichiestaInviata());
    }

    /* Test per verificare la creazione di un'Attività */
    @Test
    public void testAttivitaCreation() {
        assertNotNull(attivita1);
        assertEquals("Analisi", attivita1.getNomeAttivita());
        assertEquals(timesheet, attivita1.getTimesheet());
    }

    /* Test per la ricerca di un'attività */
    @Test
    public void testRicercaAttivitaNelTimesheet() {
        timesheet.getAttivita().add(attivita1);
        timesheet.getAttivita().add(attivita2);

        assertTrue(timesheet.getAttivita().stream()
                .anyMatch(a -> a.getNomeAttivita().equals("Analisi")));
    }

    /* Test per aggiungere ore lavorate ad un'Attività */
    @Test
    public void testAddOreLavorate() {
        Date data = new Date();
        attivita1.addOreLavorate(data, 5);
        assertEquals(5, (int) attivita1.getOreLavorateMap().get(data));
    }

    /* Test per non superare il limite di 8 ore giornaliere */
    @Test
    public void testMaxOreLavorate() {
        Date data = new Date();
        attivita1.addOreLavorate(data, 8);
        attivita1.addOreLavorate(data, 2);
        assertEquals(8, (int) attivita1.getOreLavorateMap().get(data));
    }

    /* Test per verificare che il totale delle ore venga calcolato correttamente */
    @Test
    public void testOreTotali() {
        Date data1 = new Date();
        Date data2 = new Date(data1.getTime() + 86400000);

        attivita1.addOreLavorate(data1, 4);
        attivita1.addOreLavorate(data2, 3);
        assertEquals(7, attivita1.getOreTotali());
    }

    /* Test per verificare che un'Attività appartenga a un Timesheet */
    @Test
    public void testAttivitaLinkedToTimesheet() {
        timesheet.getAttivita().add(attivita1);
        assertEquals(timesheet, attivita1.getTimesheet());
        assertTrue(timesheet.getAttivita().contains(attivita1));
    }

    /* Test per verificare che si possa modificare il Timesheet associato all'Attivita */
    @Test
    public void testSetTimesheet() {
        attivita1.setTimesheet(nuovoTimesheet);
        assertEquals(nuovoTimesheet, attivita1.getTimesheet());
    }

    /* Test per verificare la rimozione delle ore lavorate */
    @Test
    public void testRemoveOreLavorate() {
        Date data = new Date();
        attivita1.addOreLavorate(data, 5);
        assertEquals(5, (int) attivita1.getOreLavorateMap().get(data));
        attivita1.removeOreLavorate(data);
        assertFalse(attivita1.getOreLavorateMap().containsKey(data));
        assertEquals(0, attivita1.getOreTotali());
    }

    /* Test per eliminare un timesheet */
    @Test
    public void testDeleteTimesheet() {
        timesheet.getAttivita().add(attivita1);
        timesheet.getAttivita().add(attivita2);

        attivita1.setTimesheet(null);
        attivita2.setTimesheet(null);
        timesheet = null;

        assertNull(attivita1.getTimesheet());
        assertNull(attivita2.getTimesheet());
    }

    /* Test per la somma totale delle ore lavorate*/
    @Test
    public void testCalcoloOreTotali() {
        Date data1 = new Date();
        Date data2 = new Date(data1.getTime() + 86400000); // Giorno successivo

        attivita1.addOreLavorate(data1, 4);
        attivita1.addOreLavorate(data2, 3);

        assertEquals(7, attivita1.getOreTotali());
    }

    /* Test per la rimozione delle ore lavorate e l'aggiornamento del totale */
    @Test
    public void testRimozioneOreAggiornamentoTotale() {
        Date data = new Date();
        attivita1.addOreLavorate(data, 6);
        assertEquals(6, attivita1.getOreTotali());

        attivita1.removeOreLavorate(data);
        assertEquals(0, attivita1.getOreTotali());
    }

}
