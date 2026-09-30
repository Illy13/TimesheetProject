package it.univr.esameprogettazione;

import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

import jakarta.servlet.http.HttpServletResponse;
import java.io.PrintWriter;

@Controller
public class TimesheetController {
    @Autowired
    private TimesheetRepository repository;

    @RequestMapping("/")
    public String index() {
        return "timesheet-list";
    }

    /*@RequestMapping("/list")
    public String list(Model model) {
        List<Timesheet> data = repository.findAll();
        model.addAttribute("timesheets", data);
        return "timesheet-list";
    }*/

    @RequestMapping("/input")
    public String input() {
        return "input";
    }

    /* --------- METODO CREATE --------- */
    @PostMapping("/create")
    public String create(
            @RequestParam(name = "nomeLavoratore") String nomeLavoratore,
            @RequestParam(name = "codiceFiscale") String codiceFiscale,
            @RequestParam(name = "nomeProgetto") String nomeProgetto,
            @RequestParam(name = "attivitaNomi") String[] attivitaNomi,
            @RequestParam(name = "attivitaOre") int[] attivitaOre,
            @RequestParam(name = "attivitaDate") String[] attivitaDate,
            Model model) {

        // Utilizza la data corrente per il timesheet
        Date data = new Date(); // Corrisponde alla data di creazione del timesheet
        Timesheet timesheet = new Timesheet(nomeLavoratore, nomeProgetto, codiceFiscale, data); // Creo il nuovo timesheet
        Map<String, Attivita> attivitaMap = new HashMap<>();    /* Mi serve per raggruppare le ore lavorate per la stessa attività -> nel caso
                                                                    in cui l'attività esiste già, allora si sommare le ore lavorate*/
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");    // Converte le date yy-mm-dd in oggetti Date -> altrimenti ho problemi con la visualizzazione

        /* Per l'attività del timesheet converto la data yy-mm-dd e controllo se si tratta di un sabato o una domenica oppure un giorno festivo
         * NB!! Possiamo aggiungere le attività solamente dal lunedì al venerdì e le ore lavorate giornaliere possono essere al massimo 8 */
        try {
            Date dataAttivita = formatter.parse(attivitaDate[0]);
            // Controlla se la data è sabato, domenica o festiva
            Calendar cal = Calendar.getInstance();
            cal.setTime(dataAttivita);
            int dayOfWeek = cal.get(Calendar.DAY_OF_WEEK);
            if (dayOfWeek == Calendar.SATURDAY || dayOfWeek == Calendar.SUNDAY || isHoliday(dataAttivita)) {
                model.addAttribute("errorMessage", "Non è possibile inserire attività in giorni non lavorativi (sabato, domenica o festivi).");
                return "error";
            }
            // Controllo che le ore siano comprese tra 1 e 8
            if (attivitaOre[0] < 1 || attivitaOre[0] > 8) {
                model.addAttribute("errorMessage", "Il totale delle ore lavorate in " + formatter.format(dataAttivita) +
                        " deve essere compreso tra 1 e 8. Totale inserito: " + attivitaOre[0]);
                return "error";
            }

            // Creo l'attività e la aggiunge al timesheet
            Attivita attivita = new Attivita(attivitaNomi[0], timesheet);
            attivita.addOreLavorate(dataAttivita, attivitaOre[0]);
            timesheet.setAttivita(Arrays.asList(attivita));
            repository.save(timesheet);
        } catch (ParseException e) {
            return "error";
        }

        return "redirect:/list";
    }


    /* --------- METODO READ --------- */
    @RequestMapping("/read")
    public String read(@RequestParam(name = "id") Long id,
                       @RequestParam(name = "sort", required = false) String sort,
                       Model model) {

        Optional<Timesheet> result = repository.findById(id);
        if (result.isPresent()) {
            Timesheet timesheet = result.get();
            model.addAttribute("timesheet", timesheet);

            Map<String, Map<Date, Integer>> oreLavorateMap = new HashMap<>();    // Mappa per raccogliere le ore lavorate per ogni attività e per ogni data
            Map<String, Integer> oreTotaliMap = new HashMap<>();    // Mappa per memorizzare il totale delle ore per ciascuna attività
            Set<Date> giorniAttivita = new TreeSet<>(); // Set per raccogliere tutte le date delle attività e ordinarle automaticamente
            int totaleOreComplessivo = 0;   // Variabile per il totale delle ore lavorate nel timesheet

            for (Attivita attivita : timesheet.getAttivita()) {
                Map<Date, Integer> orePerData = attivita.getOreLavorateMap();   // Recupero la mappa delle ore lavorate per ogni data
                giorniAttivita.addAll(orePerData.keySet());
                int totaleOre = orePerData.values().stream().mapToInt(Integer::intValue).sum();  // Calcolo il totale delle ore lavorate per questa attività

                // Salvo i dati nelle mappe
                oreLavorateMap.put(attivita.getNomeAttivita(), orePerData);
                oreTotaliMap.put(attivita.getNomeAttivita(), totaleOre);
                totaleOreComplessivo += totaleOre;
            }

            // Controlla se è richiesto l'ordinamento per ore totali
            List<Map.Entry<String, Integer>> sortedEntries = new ArrayList<>(oreTotaliMap.entrySet());
            if ("ore".equals(sort)) {
                sortedEntries.sort(Map.Entry.comparingByValue());
            }

            // Ricostruisco le mappe per passare i dati ordinati alla vista
            LinkedHashMap<String, Map<Date, Integer>> sortedOreLavorateMap = new LinkedHashMap<>();
            LinkedHashMap<String, Integer> sortedOreTotaliMap = new LinkedHashMap<>();

            for (Map.Entry<String, Integer> entry : sortedEntries) {
                sortedOreLavorateMap.put(entry.getKey(), oreLavorateMap.get(entry.getKey()));
                sortedOreTotaliMap.put(entry.getKey(), entry.getValue());
            }

            model.addAttribute("giorniAttivita", giorniAttivita);
            model.addAttribute("oreLavorateMap", sortedOreLavorateMap);
            model.addAttribute("oreTotaliMap", sortedOreTotaliMap);
            model.addAttribute("totaleOreComplessivo", totaleOreComplessivo);

            return "read";
        }

        return "notfound";
    }


    /* --------- METODO EDIT --------- */
    @RequestMapping("/edit")
    public String edit(@RequestParam Long id, Model model) {
        Optional<Timesheet> result = repository.findById(id);
        if (!result.isPresent()) {
            return "notfound";
        }

        Timesheet timesheet = result.get();

        // Effettuiamo i controlli sulle date e sui totali delle ore
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
        Map<String, Integer> dailyTotals = new HashMap<>();
        Calendar cal = Calendar.getInstance();

        // Itera su ogni attività e sulle relative date
        for (Attivita attivita : timesheet.getAttivita()) {
            for (Map.Entry<Date, Integer> entry : attivita.getOreLavorateMap().entrySet()) {
                Date data = entry.getKey();
                int ore = entry.getValue();

                // Controlla se la data è sabato, domenica o festiva
                cal.setTime(data);
                int dayOfWeek = cal.get(Calendar.DAY_OF_WEEK);
                if (dayOfWeek == Calendar.SATURDAY || dayOfWeek == Calendar.SUNDAY || isHoliday(data)) {
                    model.addAttribute("errorMessage", "Il timesheet contiene attività in giorni non lavorativi: " + formatter.format(data));
                    return "error";
                }

                // Accumula il totale delle ore per la data corrente
                String dayStr = formatter.format(data);
                dailyTotals.put(dayStr, dailyTotals.getOrDefault(dayStr, 0) + ore);
            }
        }

        // Controlla che il totale delle ore per ogni data sia compreso tra 0 e 8
        for (Map.Entry<String, Integer> entry : dailyTotals.entrySet()) {
            int totalOre = entry.getValue();
            if (totalOre < 0 || totalOre > 8) {
                model.addAttribute("errorMessage", "Il totale delle ore lavorate in " + entry.getKey() +
                        " deve essere compreso tra 0 e 8. Totale attuale: " + totalOre);
                return "error";
            }
        }

        // Prepara la lista delle attività modificabili per la vista
        List<Map<String, Object>> attivitaModificabili = new ArrayList<>();
        for (Attivita attivita : timesheet.getAttivita()) {
            for (Map.Entry<Date, Integer> entry : attivita.getOreLavorateMap().entrySet()) {
                Map<String, Object> row = new HashMap<>();
                row.put("nomeAttivita", attivita.getNomeAttivita());
                row.put("data", entry.getKey());
                row.put("oreLavorate", entry.getValue());
                attivitaModificabili.add(row);
            }
        }

        model.addAttribute("timesheet", timesheet);
        model.addAttribute("attivitaModificabili", attivitaModificabili);
        return "edit";
    }


    /* --------- METODO UPDATE --------- */
    @PostMapping("/update")
    public String update(
            @RequestParam(name = "id") Long id,
            @RequestParam(name = "nomeProgetto") String nomeProgetto,
            @RequestParam(name = "attivitaNome", required = false) List<String> attivitaNomi,
            @RequestParam(name = "attivitaOre", required = false) List<Integer> attivitaOre,
            @RequestParam(name = "attivitaDate", required = false) List<String> attivitaDate,
            Model model) {

        Optional<Timesheet> result = repository.findById(id);
        if (!result.isPresent()) {
            return "notfound";
        }

        Timesheet timesheet = result.get();
        timesheet.setNomeProgetto(nomeProgetto);
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");

        /* Si costruisce una mappa -> per ogni attività già presente nel timesheet, la chiave è il nome dell'attività e il valore è l'oggetto Attivita corrispondente
         *  Lo facciamo per verificare se un'attività è già presente nel timesheet -> nel caso in cui sia presente, allora la dovrò aggiornare */
        Map<String, Attivita> attivitaMap = new HashMap<>();
        for (Attivita att : timesheet.getAttivita()) {
            attivitaMap.put(att.getNomeAttivita(), att);
        }

        /* Per ogni riga del timesheet controllo che la data non sia sabato/domenica o festivi e se l'attività è già presente, allora l'aggiorna,
         *  mentre se l'attività è nuova, allora viene creato un nuovo oggetto Attività, quindi viene aggiunta al timesheet */
        for (int i = 0; i < attivitaNomi.size(); i++) {
            try {
                Date dataLavoro = formatter.parse(attivitaDate.get(i));
                // Controlla se la data è weekend o festiva
                Calendar cal = Calendar.getInstance();
                cal.setTime(dataLavoro);
                int dayOfWeek = cal.get(Calendar.DAY_OF_WEEK);
                if (dayOfWeek == Calendar.SATURDAY || dayOfWeek == Calendar.SUNDAY || isHoliday(dataLavoro)) {
                    model.addAttribute("errorMessage", "Non è possibile inserire attività in giorni non lavorativi (sabato, domenica o festivi).");
                    return "error";
                }
                String nomeAttivita = attivitaNomi.get(i);
                int nuoveOre = attivitaOre.get(i);

                if (attivitaMap.containsKey(nomeAttivita)) {
                    Attivita attivitaEsistente = attivitaMap.get(nomeAttivita);
                    // Sostituisce il valore per quella data
                    attivitaEsistente.getOreLavorateMap().put(dataLavoro, nuoveOre);
                } else {
                    Attivita nuovaAttivita = new Attivita(nomeAttivita, timesheet);
                    nuovaAttivita.addOreLavorate(dataLavoro, nuoveOre);
                    timesheet.getAttivita().add(nuovaAttivita);
                }
            } catch (ParseException e) {
                return "error";
            }
        }

        // Calcolo i totali giornalieri per il timesheet aggiornato
        Map<String, Integer> dailyTotals = new HashMap<>();
        for (Attivita attivita : timesheet.getAttivita()) {
            for (Map.Entry<Date, Integer> entry : attivita.getOreLavorateMap().entrySet()) {
                String dayStr = formatter.format(entry.getKey());
                dailyTotals.put(dayStr, dailyTotals.getOrDefault(dayStr, 0) + entry.getValue());
            }
        }
        for (Map.Entry<String, Integer> entry : dailyTotals.entrySet()) {
            int totalOre = entry.getValue();
            if (totalOre < 1 || totalOre > 8) {
                model.addAttribute("errorMessage", "Il totale delle ore lavorate in " + entry.getKey() +
                        " deve essere compreso tra 1 e 8. Totale inserito: " + totalOre);
                return "error";
            }
        }

        try {
            repository.save(timesheet);
        } catch (Exception e) {
            return "error";
        }
        return "redirect:/list";
    }


    /* --------- METODO DELETE --------- */
    @RequestMapping("/delete")
    public String delete(@RequestParam Long id) {
        Optional<Timesheet> result = repository.findById(id);
        if (result.isPresent()) {
            repository.delete(result.get());
            return "redirect:/list";
        }
        return "notfound";
    }

    /* --------- METODO PER MOSTRARE FORM DI AGGIUNTA ATTIVITÀ --------- */
    @GetMapping("/addActivity")
    public String addActivity(@RequestParam(name = "id") Long timesheetId, Model model) {
        Optional<Timesheet> result = repository.findById(timesheetId);
        if (result.isPresent()) {
            model.addAttribute("timesheet", result.get());
            return "addActivity";
        } else {
            return "notfound";
        }
    }

    /* --------- METODO PER SALVARE NUOVA ATTIVITÀ AL PROGETTO --------- */
    @PostMapping("/saveActivity")
    public String saveActivity(
            @RequestParam(name = "timesheetId") Long timesheetId,
            @RequestParam(name = "nomeAttivita") String nomeAttivita,
            @RequestParam(name = "oreLavorate") int oreLavorate,
            @RequestParam(name = "dataLavoro") String dataLavoro,
            Model model) {

        Optional<Timesheet> result = repository.findById(timesheetId);
        if (!result.isPresent()) {
            model.addAttribute("errorMessage", "Errore: Timesheet non trovato.");
            return "error";
        }

        Timesheet timesheet = result.get();
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
        Date data;
        try {
            data = formatter.parse(dataLavoro);
        } catch (ParseException e) {
            model.addAttribute("errorMessage", "Errore: Data non valida.");
            return "error";
        }

        // Controlla se la data è weekend o festiva
        Calendar cal = Calendar.getInstance();
        cal.setTime(data);
        int dayOfWeek = cal.get(Calendar.DAY_OF_WEEK);
        if (dayOfWeek == Calendar.SATURDAY || dayOfWeek == Calendar.SUNDAY || isHoliday(data)) {
            model.addAttribute("errorMessage", "Non è possibile inserire attività in giorni non lavorativi (sabato, domenica o festivi).");
            return "error";
        }

        // Calcoliamo il totale delle ore PRIMA di aggiornare l'attività
        int oreTotaliGiornaliere = timesheet.getAttivita().stream()
                .mapToInt(a -> a.getOreLavorateMap().getOrDefault(data, 0))
                .sum();

        int oreDopoAggiornamento = oreTotaliGiornaliere + oreLavorate;

        // Controllo che il totale delle ore lavorate giornaliere sia <= 8
        if (oreDopoAggiornamento > 8) {
            model.addAttribute("errorMessage", "Il totale delle ore lavorate in " + formatter.format(data) +
                    " supera il massimo di 8 ore. Totale tentato: " + oreDopoAggiornamento);
            return "error";
        }

        // Se esiste già un'attività con lo stesso nome, aggiorna le ore
        Attivita attivitaEsistente = timesheet.getAttivita().stream()
                .filter(a -> a.getNomeAttivita().equalsIgnoreCase(nomeAttivita))
                .findFirst()
                .orElse(null);

        if (attivitaEsistente != null) {
            attivitaEsistente.addOreLavorate(data, oreLavorate);
        } else {
            Attivita nuovaAttivita = new Attivita(nomeAttivita, timesheet);
            nuovaAttivita.addOreLavorate(data, oreLavorate);
            timesheet.getAttivita().add(nuovaAttivita);
        }

        repository.save(timesheet);
        return "redirect:/read?id=" + timesheetId;
    }

    /*------ METODO COMPARE ----------*/
    @RequestMapping("/compare")
    public String compare(
            @RequestParam(name = "timesheet1") Long timesheetId1,
            @RequestParam(name = "timesheet2") Long timesheetId2,
            Model model) {

        Optional<Timesheet> result1 = repository.findById(timesheetId1);
        Optional<Timesheet> result2 = repository.findById(timesheetId2);

        if (result1.isPresent() && result2.isPresent()) {
            Timesheet timesheet1 = result1.get();
            Timesheet timesheet2 = result2.get();

            model.addAttribute("timesheet1", timesheet1);
            model.addAttribute("timesheet2", timesheet2);

            /* Creiamo le mappe per il confronto delle ore lavorate per attività.
             *  In particolare, la mappa associa il nome di ciascuna attività ad una mappa contenente le ore lavorate per ciascuna data */
            Map<String, Map<Date, Integer>> oreLavorate1 = new HashMap<>();
            Map<String, Map<Date, Integer>> oreLavorate2 = new HashMap<>();

            /* Mappe per associare ad ogni attività il nome del progetto a cui appartengono */
            Map<String, String> progetti1 = new HashMap<>();
            Map<String, String> progetti2 = new HashMap<>();

            Set<Date> giorniConfronto = new TreeSet<>();    // Set per raccogliere tutte le date

            // Elaborazione timesheet1
            for (Attivita attivita : timesheet1.getAttivita()) {
                oreLavorate1.put(attivita.getNomeAttivita(), attivita.getOreLavorateMap());
                progetti1.put(attivita.getNomeAttivita(), attivita.getTimesheet().getNomeProgetto());
                giorniConfronto.addAll(attivita.getOreLavorateMap().keySet());
            }

            // Elaborazione timesheet2
            for (Attivita attivita : timesheet2.getAttivita()) {
                oreLavorate2.put(attivita.getNomeAttivita(), attivita.getOreLavorateMap());
                progetti2.put(attivita.getNomeAttivita(), attivita.getTimesheet().getNomeProgetto());
                giorniConfronto.addAll(attivita.getOreLavorateMap().keySet());
            }

            // Calcolo delle ore totali per ciascun timesheet
            int totaleOre1 = timesheet1.getAttivita()
                    .stream()
                    .mapToInt(Attivita::getOreTotali)
                    .sum();
            int totaleOre2 = timesheet2.getAttivita()
                    .stream()
                    .mapToInt(Attivita::getOreTotali)
                    .sum();

            model.addAttribute("oreLavorate1", oreLavorate1);
            model.addAttribute("oreLavorate2", oreLavorate2);
            model.addAttribute("progetti1", progetti1);
            model.addAttribute("progetti2", progetti2);
            model.addAttribute("giorniConfronto", giorniConfronto);
            model.addAttribute("totaleOre1", totaleOre1);
            model.addAttribute("totaleOre2", totaleOre2);

            return "compare";
        }

        return "notfound";
    }

    /* --------- METODO PER VERIFICARE SE UNA DATA E' FESTIVA --------- */
    private boolean isHoliday(Date date) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        String dateStr = sdf.format(date);
        List<String> holidays = Arrays.asList("2025-01-01", "2025-01-06", "2025-04-20", "2025-04-21", "2025-04-25", "2025-05-01", "2025-06-02", "2025-08-15", "2025-11-01", "2025-12-08", "2025-12-25", "2025-12-26");
        return holidays.contains(dateStr);
    }

    /* -------- APPROVE ------------ */
    @RequestMapping("/approve")
    public String approve(@RequestParam(name = "id") Long id, Model model) {
        Optional<Timesheet> result = repository.findById(id);

        if (result.isPresent()) {
            Timesheet timesheet = result.get();
            model.addAttribute("timesheet", timesheet);

            boolean richiestaInviata = timesheet.isRichiestaInviata(); // variabile per verificare se presente o meno la richiesta di approvazione
            model.addAttribute("richiestaInviata", richiestaInviata);

            // 🔹 Creiamo una mappa per raccogliere le ore lavorate per ogni attività
            Map<String, Map<Date, Integer>> oreLavorateMap = new HashMap<>();
            Map<String, Integer> oreTotaliMap = new HashMap<>(); // 🔹 Mappa per le ore totali di ogni attività
            Set<Date> giorniAttivita = new TreeSet<>();
            int totaleOreComplessivo = 0; // 🔹 Variabile per il totale delle ore lavorate

            for (Attivita attivita : timesheet.getAttivita()) {
                Map<Date, Integer> orePerData = attivita.getOreLavorateMap();
                giorniAttivita.addAll(orePerData.keySet());

                // Aggiungiamo alla mappa delle ore lavorate
                oreLavorateMap.put(attivita.getNomeAttivita(), orePerData);

                // Calcoliamo il totale delle ore lavorate per questa attività
                int totaleOre = orePerData.values().stream().mapToInt(Integer::intValue).sum();
                oreTotaliMap.put(attivita.getNomeAttivita(), totaleOre);

                // Aggiungiamo il totale della singola attività al totale generale
                totaleOreComplessivo += totaleOre;
            }

            System.out.println("DEBUG - Mappa Finale Ore Lavorate per Thymeleaf: " + oreLavorateMap);
            System.out.println("DEBUG - Mappa Ore Totali per Thymeleaf: " + oreTotaliMap);
            System.out.println("DEBUG - Totale Complessivo Ore: " + totaleOreComplessivo);

            model.addAttribute("giorniAttivita", giorniAttivita);
            model.addAttribute("oreLavorateMap", oreLavorateMap);
            model.addAttribute("oreTotaliMap", oreTotaliMap); // 🔹 Passiamo la mappa con le ore totali
            model.addAttribute("totaleOreComplessivo", totaleOreComplessivo); // 🔹 Passiamo il totale generale

            return "approve";
        }
        return "notfound";
    }

    @PostMapping("/approve-request")
    public String approveRequest(@RequestParam(name = "id") Long id) {
        Optional<Timesheet> result = repository.findById(id);

        if (result.isPresent()) {
            Timesheet timesheet = result.get();
            timesheet.setRichiestaInviata(true); // Imposta richiestaInviata a true
            repository.save(timesheet); // Salva le modifiche nel database
        }

        return "redirect:/approve?id=" + id; // Reindirizza alla pagina di approvazione
    }



    /*------ METODO DOWNLOAD DI UN TIMESHEET ----------*/
    @GetMapping("/downloadTimesheetExcel")
    public void downloadTimesheetExcel(@RequestParam Long id, HttpServletResponse response) throws IOException {
        Optional<Timesheet> result = repository.findById(id);
        if (!result.isPresent()) {
            response.sendError(HttpServletResponse.SC_NOT_FOUND, "Timesheet non trovato");
            return;
        }

        Timesheet timesheet = result.get();

        response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
        response.setHeader("Content-Disposition", "attachment; filename=timesheet.xlsx");

        Workbook workbook = new XSSFWorkbook();
        Sheet sheet = workbook.createSheet("Timesheet");

        // Creazione dello stile per l'intestazione
        CellStyle headerStyle = workbook.createCellStyle();
        Font headerFont = workbook.createFont();
        headerFont.setBold(true);
        headerStyle.setFont(headerFont);

        // Creazione della riga di intestazione
        Row headerRow = sheet.createRow(0);
        String[] columns = {"Attività", "Data", "Ore Totali"};
        for (int i = 0; i < columns.length; i++) {
            Cell cell = headerRow.createCell(i);
            cell.setCellValue(columns[i]);
            cell.setCellStyle(headerStyle);
        }

        // Creazione delle righe con i dati delle attività
        int rowNum = 1;
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");

        for (Attivita attivita : timesheet.getAttivita()) {
            for (Map.Entry<Date, Integer> entry : attivita.getOreLavorateMap().entrySet()) {
                Row row = sheet.createRow(rowNum++);
                row.createCell(0).setCellValue(attivita.getNomeAttivita());
                row.createCell(1).setCellValue(dateFormat.format(entry.getKey()));
                row.createCell(2).setCellValue(entry.getValue());
            }
        }

        // Auto-size delle colonne per una migliore leggibilità
        for (int i = 0; i < columns.length; i++) {
            sheet.autoSizeColumn(i);
        }

        // Scrive il file Excel nella risposta HTTP
        workbook.write(response.getOutputStream());
        workbook.close();
    }


    /*------ METODO PER LA RICERCA DI UN TIMESHEET ----------*/
    @GetMapping("/search")
    public String searchTimesheets(@RequestParam(name = "query", required = false) String query, Model model) {
        List<Timesheet> timesheets;

        if (query == null || query.trim().isEmpty()) {
            // Se la query è vuota, restituisce tutti i timesheet
            timesheets = repository.findAll();
        } else {
            // Cerca timesheet che contengono il nome del progetto inserito
            timesheets = repository.findByNomeProgettoContainingIgnoreCase(query);
        }

        model.addAttribute("timesheets", timesheets);
        return "timesheet-list"; // Ritorna la stessa pagina con i risultati filtrati
    }


    /*------ METODO PER L'ORDINAMENTO DEI TIMESHEET ----------*/
    @GetMapping("/list")    /* NB!! Ho dovuto eliminare il metodo @RequestMapping("/list"), perchè altrimenti ho un un conflitto
                            dato che ho GetMapping(/list) e RequestMapping(/list) */
    public String list(
            @RequestParam(name = "query", required = false) String query,
            @RequestParam(name = "sort", required = false) String sort,
            Model model) {

        List<Timesheet> timesheets;

        if (query != null && !query.trim().isEmpty()) {
            timesheets = repository.findByNomeProgettoContainingIgnoreCase(query);
        } else {
            timesheets = repository.findAll();
        }

        // Ordina per nome del progetto se richiesto
        if ("nomeProgetto".equals(sort)) {
            timesheets.sort(Comparator.comparing(Timesheet::getNomeProgetto, String.CASE_INSENSITIVE_ORDER));
        }

        model.addAttribute("timesheets", timesheets);
        return "timesheet-list";  // Nome del template HTML
    }

}
