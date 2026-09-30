/*
package it.univr.esameprogettazione;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
public class TimesheetService {
    @Autowired
    private TimesheetRepository repository;

    public Timesheet saveTimesheet(Timesheet timesheet) {
        for (Map.Entry<LocalDate, Integer> entry : timesheet.getOreLavorate().entrySet()) {
            LocalDate date = entry.getKey();
            int hours = entry.getValue();

            if (date.getDayOfWeek() == DayOfWeek.SATURDAY || date.getDayOfWeek() == DayOfWeek.SUNDAY) {
                throw new IllegalArgumentException("Non puoi inserire ore nei giorni festivi!");
            }

            if (hours > 8) {
                throw new IllegalArgumentException("Non puoi inserire più di 8 ore al giorno!");
            }
        }

        return repository.save(timesheet);
    }

    public Optional<Timesheet> getTimesheetById(Long id) {
        return repository.findById(id);
    }

    public List<Timesheet> getAllTimesheets() {

        return repository.findAll();
    }
}

*/
