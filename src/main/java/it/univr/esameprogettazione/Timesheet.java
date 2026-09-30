package it.univr.esameprogettazione;

import jakarta.persistence.*;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Entity
public class Timesheet {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    private String nomeLavoratore;
    private String nomeProgetto;
    private String codiceFiscale;
    private Date dataOdierna;
    private Boolean richiestaInviata;

    @OneToMany(mappedBy = "timesheet", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.EAGER)
    private List<Attivita> attivita = new ArrayList<>();

    // Costruttori
    protected Timesheet() {}

    public Timesheet(String nomeLavoratore, String nomeProgetto, String codiceFiscale, Date dataOdierna) {
        this.nomeLavoratore = nomeLavoratore;
        this.nomeProgetto = nomeProgetto;
        this.codiceFiscale = codiceFiscale;
        this.dataOdierna = dataOdierna;
        this.richiestaInviata = false;
    }

    // Getter e Setter
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getNomeLavoratore() {
        return nomeLavoratore;
    }

    public String getNomeProgetto() {
        return nomeProgetto;
    }

    public void setNomeProgetto(String nomeProgetto) {
        this.nomeProgetto = nomeProgetto;
    }

    public String getCodiceFiscale() {
        return codiceFiscale;
    }

    public List<Attivita> getAttivita() {
        return attivita;
    }

    public void setAttivita(List<Attivita> attivita) {
        this.attivita = attivita;
    }

    public boolean isRichiestaInviata() {
        return richiestaInviata;
    }

    public void setRichiestaInviata(boolean richiestaInviata) {
        this.richiestaInviata = richiestaInviata;
    }

    public Date getDataOdierna() {
        return dataOdierna;
    }
}
