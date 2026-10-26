package de.likeherotozero.controller;

import de.likeherotozero.service.CountryService;
import de.likeherotozero.service.EmissionService;
import de.likeherotozero.service.UserService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@Controller
@RequestMapping("/admin")
public class AdminController {

    private final UserService userService;
    private final CountryService countryService;
    private final EmissionService emissionService;

    public AdminController(UserService userService, CountryService countryService, EmissionService emissionService) {
        this.userService = userService;
        this.countryService = countryService;
        this.emissionService = emissionService;
    }

    @GetMapping
    public String dashboard(@RequestParam(required = false) Long countryId, Model model) {
        model.addAttribute("scientists", userService.getAllScientists());
        model.addAttribute("countries", countryService.getAll());
        model.addAttribute("pendingRecords", emissionService.getPendingRecords());

        if (countryId != null) {
            model.addAttribute("selectedCountry", countryService.getById(countryId));
            model.addAttribute("records", emissionService.getRecordsForCountry(countryId));
        }

        return "admin-dashboard";
    }

    @PostMapping("/scientist/add")
    public String addScientist(@RequestParam String name,
                                @RequestParam String email,
                                @RequestParam String password) {
        userService.createScientist(name, email, password);
        return "redirect:/admin";
    }

    @PostMapping("/scientists/update")
    public String updateScientists(@RequestParam List<Long> id,
                                    @RequestParam List<String> name,
                                    @RequestParam List<String> email) {
        userService.updateScientists(id, name, email);
        return "redirect:/admin";
    }

    @PostMapping("/scientist/delete")
    public String deleteScientist(@RequestParam Long id) {
        userService.deleteScientist(id);
        return "redirect:/admin";
    }

    @PostMapping("/country/add")
    public String addCountry(@RequestParam String name, @RequestParam String isoCode) {
        countryService.create(name, isoCode);
        return "redirect:/admin";
    }

    @PostMapping("/records/update")
    public String updateRecords(@RequestParam List<Long> recordId,
                                 @RequestParam List<Integer> reportingYear,
                                 @RequestParam List<Double> emissionValue,
                                 @RequestParam List<String> source) {
        emissionService.updateRecordsAsAdmin(recordId, reportingYear, emissionValue, source);
        return "redirect:/admin";
    }

    @PostMapping("/record/add")
    public String addRecord(@RequestParam Long countryId,
                             @RequestParam Integer reportingYear,
                             @RequestParam Double emissionValue,
                             @RequestParam String source) {
        emissionService.addRecord(countryId, reportingYear, emissionValue, source, true);
        return "redirect:/admin?countryId=" + countryId;
    }

    @PostMapping("/record/delete")
    public String deleteRecord(@RequestParam Long recordId, @RequestParam Long countryId) {
        emissionService.deleteRecord(recordId);
        return "redirect:/admin?countryId=" + countryId;
    }

    @PostMapping("/record/approve")
    public String approveRecord(@RequestParam Long recordId) {
        emissionService.approveRecord(recordId);
        return "redirect:/admin";
    }

    @PostMapping("/record/reject")
    public String rejectRecord(@RequestParam Long recordId) {
        emissionService.rejectRecord(recordId);
        return "redirect:/admin";
    }
}
