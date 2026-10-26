package de.likeherotozero.controller;

import de.likeherotozero.entity.Country;
import de.likeherotozero.service.CountryService;
import de.likeherotozero.service.EmissionService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@Controller
@RequestMapping("/scientist")
public class ScientistController {

    private final CountryService countryService;
    private final EmissionService emissionService;

    public ScientistController(CountryService countryService, EmissionService emissionService) {
        this.countryService = countryService;
        this.emissionService = emissionService;
    }

    @GetMapping
    public String dashboard(@RequestParam(required = false) Long countryId, Model model) {
        model.addAttribute("countries", countryService.getAll());

        if (countryId != null) {
            Country selectedCountry = countryService.getById(countryId);
            model.addAttribute("selectedCountry", selectedCountry);
            model.addAttribute("records", emissionService.getRecordsForCountry(countryId));
        }

        return "scientist-dashboard";
    }

    @PostMapping("/records/update")
    public String updateRecords(@RequestParam List<Long> recordId,
                                 @RequestParam List<Integer> reportingYear,
                                 @RequestParam List<Double> emissionValue,
                                 @RequestParam List<String> source) {
        emissionService.updateRecords(recordId, reportingYear, emissionValue, source);
        return "redirect:/scientist";
    }

    @PostMapping("/record/add")
    public String addRecord(@RequestParam Long countryId,
                             @RequestParam Integer reportingYear,
                             @RequestParam Double emissionValue,
                             @RequestParam String source) {
        emissionService.addRecord(countryId, reportingYear, emissionValue, source, false);
        return "redirect:/scientist?countryId=" + countryId;
    }

    @PostMapping("/record/delete")
    public String deleteRecord(@RequestParam Long recordId, @RequestParam Long countryId) {
        emissionService.deleteRecord(recordId);
        return "redirect:/scientist?countryId=" + countryId;
    }
}
