package de.likeherotozero.service;

import de.likeherotozero.entity.Co2EmissionRecord;
import de.likeherotozero.entity.Country;
import de.likeherotozero.repository.Co2EmissionRecordRepository;
import de.likeherotozero.repository.CountryRepository;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Service
public class EmissionService {

    private final Co2EmissionRecordRepository recordRepository;
    private final CountryRepository countryRepository;

    public EmissionService(Co2EmissionRecordRepository recordRepository, CountryRepository countryRepository) {
        this.recordRepository = recordRepository;
        this.countryRepository = countryRepository;
    }

    public List<Co2EmissionRecord> getRecordsForCountry(Long countryId) {
        return recordRepository.findByCountryIdOrderByReportingYearAsc(countryId);
    }

    public List<Co2EmissionRecord> getPendingRecords() {
        return recordRepository.findAllPending();
    }

    public Co2EmissionRecord addRecord(Long countryId, Integer reportingYear, Double emissionValue,
                                        String source, boolean approved) {
        Country country = countryRepository.findById(countryId)
                .orElseThrow(() -> new IllegalArgumentException("Country not found: " + countryId));
        Co2EmissionRecord record = new Co2EmissionRecord(country, reportingYear, emissionValue, source);
        record.setApproved(approved);
        return recordRepository.save(record);
    }

    public void updateRecords(List<Long> recordIds, List<Integer> reportingYears,
                               List<Double> emissionValues, List<String> sources) {
        for (int i = 0; i < recordIds.size(); i++) {
            Co2EmissionRecord record = findRecord(recordIds.get(i));
            Integer year = reportingYears.get(i);
            Double value = emissionValues.get(i);
            String source = sources.get(i);

            if (record.isApproved()) {
                if (matches(year, value, source, record.getEffectiveReportingYear(),
                        record.getEffectiveEmissionValue(), record.getEffectiveSource())) {
                    continue;
                }
                if (matches(year, value, source, record.getReportingYear(),
                        record.getEmissionValue(), record.getSource())) {
                    record.clearPendingChange();
                } else {
                    record.setPendingReportingYear(year);
                    record.setPendingEmissionValue(value);
                    record.setPendingSource(source);
                }
            } else {
                if (matches(year, value, source, record.getReportingYear(),
                        record.getEmissionValue(), record.getSource())) {
                    continue;
                }
                record.setReportingYear(year);
                record.setEmissionValue(value);
                record.setSource(source);
            }

            recordRepository.save(record);
        }
    }

    public void updateRecordsAsAdmin(List<Long> recordIds, List<Integer> reportingYears,
                                      List<Double> emissionValues, List<String> sources) {
        for (int i = 0; i < recordIds.size(); i++) {
            Co2EmissionRecord record = findRecord(recordIds.get(i));
            Integer year = reportingYears.get(i);
            Double value = emissionValues.get(i);
            String source = sources.get(i);

            if (matches(year, value, source, record.getReportingYear(),
                    record.getEmissionValue(), record.getSource())) {
                continue;
            }

            record.setReportingYear(year);
            record.setEmissionValue(value);
            record.setSource(source);
            record.clearPendingChange();
            record.setApproved(true);
            recordRepository.save(record);
        }
    }

    private Co2EmissionRecord findRecord(Long recordId) {
        return recordRepository.findById(recordId)
                .orElseThrow(() -> new IllegalArgumentException("Record not found: " + recordId));
    }

    private boolean matches(Integer year, Double value, String source,
                            Integer otherYear, Double otherValue, String otherSource) {
        return Objects.equals(year, otherYear)
                && Objects.equals(value, otherValue)
                && Objects.equals(source, otherSource);
    }

    public void approveRecord(Long recordId) {
        Co2EmissionRecord record = findRecord(recordId);

        if (record.isPendingChange()) {
            record.setReportingYear(record.getPendingReportingYear());
            record.setEmissionValue(record.getPendingEmissionValue());
            record.setSource(record.getPendingSource());
            record.clearPendingChange();
        }

        record.setApproved(true);
        recordRepository.save(record);
    }

    public void rejectRecord(Long recordId) {
        Co2EmissionRecord record = findRecord(recordId);

        if (!record.isApproved()) {
            recordRepository.delete(record);
        } else {
            record.clearPendingChange();
            recordRepository.save(record);
        }
    }

    public void deleteRecord(Long recordId) {
        recordRepository.deleteById(recordId);
    }

    public List<CountryChartData> buildChartData(List<Long> countryIds) {
        List<CountryChartData> result = new ArrayList<>();
        for (Long countryId : countryIds) {
            countryRepository.findById(countryId).ifPresent(country -> {
                List<YearValue> data = recordRepository.findByCountryIdOrderByReportingYearAsc(countryId).stream()
                        .filter(Co2EmissionRecord::isApproved)
                        .map(r -> new YearValue(r.getReportingYear(), r.getEmissionValue()))
                        .toList();
                result.add(new CountryChartData(country.getName(), country.getIsoCode(), data));
            });
        }
        return result;
    }

    public record YearValue(Integer year, Double value) {
    }

    public record CountryChartData(String name, String isoCode, List<YearValue> data) {
    }
}
