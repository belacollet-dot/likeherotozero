package de.likeherotozero.config;

import de.likeherotozero.entity.Co2EmissionRecord;
import de.likeherotozero.entity.Country;
import de.likeherotozero.entity.Role;
import de.likeherotozero.entity.ScientistUser;
import de.likeherotozero.repository.Co2EmissionRecordRepository;
import de.likeherotozero.repository.CountryRepository;
import de.likeherotozero.repository.ScientistUserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Component;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


@Component
public class DataInitializer implements CommandLineRunner {

    private static final Logger log = LoggerFactory.getLogger(DataInitializer.class);
    private static final String CSV_SOURCE_LABEL = "World Bank";

    private final CountryRepository countryRepository;
    private final Co2EmissionRecordRepository recordRepository;
    private final ScientistUserRepository userRepository;

    public DataInitializer(CountryRepository countryRepository,
                            Co2EmissionRecordRepository recordRepository,
                            ScientistUserRepository userRepository) {
        this.countryRepository = countryRepository;
        this.recordRepository = recordRepository;
        this.userRepository = userRepository;
    }

    @Override
    public void run(String... args) throws Exception {
        importCsvIfEmpty();
        seedUsersIfEmpty();
    }

    private void importCsvIfEmpty() throws IOException {
        if (countryRepository.count() > 0) {
            return;
        }

        Map<String, Country> countryCache = new HashMap<>();
        List<Co2EmissionRecord> records = new ArrayList<>();

        ClassPathResource resource = new ClassPathResource("co2-emissions.csv");
        try (BufferedReader reader = new BufferedReader(
                new InputStreamReader(resource.getInputStream(), StandardCharsets.UTF_8))) {

            String line = reader.readLine(); 
            while ((line = reader.readLine()) != null) {
                if (line.isBlank()) {
                    continue;
                }

                String[] parts = line.split(",");
                String name = parts[0];
                String isoCode = parts[1];
                int year = Integer.parseInt(parts[2]);
                double value = Double.parseDouble(parts[3]);

                Country country = countryCache.computeIfAbsent(isoCode,
                        code -> countryRepository.save(new Country(name, code)));

                records.add(new Co2EmissionRecord(country, year, value, CSV_SOURCE_LABEL));
            }
        }

        recordRepository.saveAll(records);
        log.info("CSV import finished: {} countries, {} emission records",
                countryCache.size(), records.size());
    }

    private void seedUsersIfEmpty() {
        if (userRepository.count() > 0) {
            return;
        }

        userRepository.save(new ScientistUser(
                "Administrator", "admin@likeherotozero.de", "admin", Role.ADMIN));
        userRepository.save(new ScientistUser(
                "Scientist One", "scientist1@likeherotozero.de", "scientist1", Role.SCIENTIST));

        log.info("Default accounts created (admin@likeherotozero.de/admin, "
                + "scientist1@likeherotozero.de/scientist1)");
    }
}
