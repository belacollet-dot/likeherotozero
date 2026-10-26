package de.likeherotozero.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "co2_emission_records")
public class Co2EmissionRecord {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "country_id", nullable = false)
    private Country country;

    @Column(name = "reporting_year", nullable = false)
    private Integer reportingYear;

    @Column(name = "emission_value", nullable = false)
    private Double emissionValue;

    @Column(nullable = false)
    private String source;

    private Boolean approved = true;

    @Column(name = "pending_reporting_year")
    private Integer pendingReportingYear;

    @Column(name = "pending_emission_value")
    private Double pendingEmissionValue;

    @Column(name = "pending_source")
    private String pendingSource;

    public Co2EmissionRecord() {
    }

    public Co2EmissionRecord(Country country, Integer reportingYear, Double emissionValue, String source) {
        this.country = country;
        this.reportingYear = reportingYear;
        this.emissionValue = emissionValue;
        this.source = source;
    }

    public Long getId() {
        return id;
    }

    public Country getCountry() {
        return country;
    }

    public Integer getReportingYear() {
        return reportingYear;
    }

    public void setReportingYear(Integer reportingYear) {
        this.reportingYear = reportingYear;
    }

    public Double getEmissionValue() {
        return emissionValue;
    }

    public void setEmissionValue(Double emissionValue) {
        this.emissionValue = emissionValue;
    }

    public String getSource() {
        return source;
    }

    public void setSource(String source) {
        this.source = source;
    }

    public boolean isApproved() {
        return approved == null || approved;
    }

    public void setApproved(boolean approved) {
        this.approved = approved;
    }

    public Integer getPendingReportingYear() {
        return pendingReportingYear;
    }

    public void setPendingReportingYear(Integer pendingReportingYear) {
        this.pendingReportingYear = pendingReportingYear;
    }

    public Double getPendingEmissionValue() {
        return pendingEmissionValue;
    }

    public void setPendingEmissionValue(Double pendingEmissionValue) {
        this.pendingEmissionValue = pendingEmissionValue;
    }

    public String getPendingSource() {
        return pendingSource;
    }

    public void setPendingSource(String pendingSource) {
        this.pendingSource = pendingSource;
    }

    public boolean isPendingChange() {
        return pendingReportingYear != null;
    }

    public void clearPendingChange() {
        this.pendingReportingYear = null;
        this.pendingEmissionValue = null;
        this.pendingSource = null;
    }

    public Integer getEffectiveReportingYear() {
        return isPendingChange() ? pendingReportingYear : reportingYear;
    }

    public Double getEffectiveEmissionValue() {
        return isPendingChange() ? pendingEmissionValue : emissionValue;
    }

    public String getEffectiveSource() {
        return isPendingChange() ? pendingSource : source;
    }
}
