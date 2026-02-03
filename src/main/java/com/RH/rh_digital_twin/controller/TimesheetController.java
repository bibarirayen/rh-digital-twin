package com.RH.rh_digital_twin.controller;

import com.RH.rh_digital_twin.model.Timesheet;
import com.RH.rh_digital_twin.repository.TimesheetRepository;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/timesheets")
public class TimesheetController {

    private final TimesheetRepository timesheetRepository;

    public TimesheetController(TimesheetRepository timesheetRepository) {
        this.timesheetRepository = timesheetRepository;
    }

    @GetMapping
    public List<Timesheet> getAll() {
        return timesheetRepository.findAll();
    }

    @GetMapping("/{id}")
    public Timesheet getById(@PathVariable Long id) {
        return timesheetRepository.findById(id).orElse(null);
    }

    @PostMapping
    public Timesheet create(@RequestBody Timesheet timesheet) {
        return timesheetRepository.save(timesheet);
    }

    @PutMapping("/{id}")
    public Timesheet update(@PathVariable Long id, @RequestBody Timesheet timesheetDetails) {
        Timesheet timesheet = timesheetRepository.findById(id).orElseThrow();
        timesheet.setMois(timesheetDetails.getMois());
        timesheet.setHeuresTravaillees(timesheetDetails.getHeuresTravaillees());
        timesheet.setHeuresSup(timesheetDetails.getHeuresSup());
        timesheet.setEmployee(timesheetDetails.getEmployee());
        return timesheetRepository.save(timesheet);
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable Long id) {
        timesheetRepository.deleteById(id);
    }
}
