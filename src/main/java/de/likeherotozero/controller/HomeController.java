package de.likeherotozero.controller;

import de.likeherotozero.entity.Country;
import de.likeherotozero.service.CountryService;
import de.likeherotozero.service.EmissionService;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

@Controller
public class HomeController {

    private static final String SESSION_KEY_SELECTED = "selectedCountryIds";

    private final CountryService countryService;
    private final EmissionService emissionService;

    public HomeController(CountryService countryService, EmissionService emissionService) {
        this.countryService = countryService;
        this.emissionService = emissionService;
    }

    @GetMapping("/")
    public String home(HttpSession session, Model model) {
        LinkedList<Long> selectedOrder = getSelectedOrder(session);
        List<Country> all = countryService.getAll();

        List<Country> selected = new ArrayList<>();
        for (Long id : selectedOrder) {
            all.stream().filter(c -> c.getId().equals(id)).findFirst().ifPresent(selected::add);
        }

        List<Country> unselected = all.stream()
                .filter(c -> !selectedOrder.contains(c.getId()))
                .sorted(Comparator.comparing(Country::getName))
                .toList();

        List<Country> countries = new ArrayList<>(selected);
        countries.addAll(unselected);

        Set<Long> selectedIds = new LinkedHashSet<>(selectedOrder);

        model.addAttribute("countries", countries);
        model.addAttribute("selectedIds", selectedIds);
        model.addAttribute("chartData", emissionService.buildChartData(selectedOrder));

        return "home";
    }

    @PostMapping("/countries/toggle")
    public String toggle(@RequestParam Long countryId, HttpSession session) {
        LinkedList<Long> selectedOrder = getSelectedOrder(session);

        if (!selectedOrder.remove(countryId)) {
            selectedOrder.addFirst(countryId);
        }

        session.setAttribute(SESSION_KEY_SELECTED, selectedOrder);
        return "redirect:/";
    }

    @PostMapping("/countries/reset")
    public String reset(HttpSession session) {
        session.removeAttribute(SESSION_KEY_SELECTED);
        return "redirect:/";
    }

    @SuppressWarnings("unchecked")
    private LinkedList<Long> getSelectedOrder(HttpSession session) {
        Object attr = session.getAttribute(SESSION_KEY_SELECTED);
        if (attr instanceof LinkedList<?>) {
            return new LinkedList<>((LinkedList<Long>) attr);
        }
        return new LinkedList<>();
    }
}
