package de.likeherotozero.repository;

import de.likeherotozero.entity.Co2EmissionRecord;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface Co2EmissionRecordRepository extends JpaRepository<Co2EmissionRecord, Long> {

    List<Co2EmissionRecord> findByCountryIdOrderByReportingYearAsc(Long countryId);

    @Query("select r from Co2EmissionRecord r join fetch r.country "
            + "where r.approved = false or r.pendingReportingYear is not null "
            + "order by r.country.name asc, r.reportingYear asc")
    List<Co2EmissionRecord> findAllPending();
}
