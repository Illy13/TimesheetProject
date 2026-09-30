package it.univr.esameprogettazione;

import jakarta.persistence.*;
import java.util.*;

@Entity
public class Attivita {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    private String nomeAttivita;

    @ElementCollection
    @CollectionTable(name = "ore_lavorate", joinColumns = @JoinColumn(name = "attivita_id"))
    @MapKeyColumn(name = "data_lavoro")
    @Column(name = "ore_lavorate")
    @Temporal(TemporalType.DATE)
    private Map<Date, Integer> oreLavorateMap = new HashMap<>();

    @ManyToOne
    @JoinColumn(name = "timesheet_id", nullable = false)
    private Timesheet timesheet;

    private int oreTotali;  // Totale delle ore lavorate

    protected Attivita() {}

    public Attivita(String nomeAttivita, Timesheet timesheet) {
        this.nomeAttivita = nomeAttivita;
        this.timesheet = timesheet;
        this.oreLavorateMap = new HashMap<>();
        this.oreTotali = 0;  // All'inizio deve essere settato a 0
    }

    public String getNomeAttivita() {
        return nomeAttivita;
    }

    public Timesheet getTimesheet() {
        return timesheet;
    }

    public void setTimesheet(Timesheet timesheet) {
        this.timesheet = timesheet;
    }

    public Map<Date, Integer> getOreLavorateMap() {
        return oreLavorateMap;
    }

    public int getOreTotali() {
        return oreTotali;
    }

    /* Aggiunge le ore lavorate ad una specifica data e aggiorna il tatale delle ore lavorate */
    public void addOreLavorate(Date data, int ore) {
        int orePrecedenti = oreLavorateMap.getOrDefault(data, 0);

        // Blocca il massimo giornaliero a 8 ore
        if (orePrecedenti + ore > 8) {
            oreLavorateMap.put(data, 8);
        } else {
            oreLavorateMap.put(data, orePrecedenti + ore);
        }

        aggiornaOreTotali();
    }

    /* Ricalcolcola il totale delle ore lavorate */
    private void aggiornaOreTotali() {
        this.oreTotali = this.oreLavorateMap.values().stream().mapToInt(Integer::intValue).sum();
    }

    public void removeOreLavorate(Date data) {
        if (this.oreLavorateMap.containsKey(data)) {
            this.oreLavorateMap.remove(data);
            aggiornaOreTotali(); // Aggiorna il totale dopo la rimozione
        }
    }

    public void setNomeAttivita(String nomeAttivita) {
        this.nomeAttivita = nomeAttivita;
    }
}
