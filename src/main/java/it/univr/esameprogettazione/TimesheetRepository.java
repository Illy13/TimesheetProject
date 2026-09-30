package it.univr.esameprogettazione;

import org.springframework.data.repository.CrudRepository;
import java.util.List;

public interface TimesheetRepository extends CrudRepository<Timesheet, Long> {
    List<Timesheet> findAll();

    Timesheet findById(long id);

    List<Timesheet> findByNomeProgettoContainingIgnoreCase(String nomeProgetto);

}