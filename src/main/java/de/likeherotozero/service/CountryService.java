package de.likeherotozero.service;

import de.likeherotozero.entity.Country;
import de.likeherotozero.repository.CountryRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CountryService {

    private final CountryRepository countryRepository;

    public CountryService(CountryRepository countryRepository) {
        this.countryRepository = countryRepository;
    }

    public List<Country> getAll() {
        return countryRepository.findAllByOrderByNameAsc();
    }

    public Country getById(Long id) {
        return countryRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Country not found: " + id));
    }

    public Country create(String name, String isoCode) {
        return countryRepository.save(new Country(name, isoCode.toUpperCase()));
    }

}
