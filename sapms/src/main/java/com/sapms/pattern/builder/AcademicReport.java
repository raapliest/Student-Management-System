package com.sapms.pattern.builder;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

/**
 * DESIGN PATTERN: Builder — Product
 * Complex report object built step by step via AcademicReportBuilder.
 */
public class AcademicReport {

    private final String title;
    private final String generatedBy;
    private final LocalDate generatedDate;
    private final String academicYear;
    private final Map<String, Object> summary;
    private final List<Map<String, Object>> courseStats;
    private final List<Map<String, Object>> studentDetails;
    private final String notes;
    private final boolean includeCharts;

    // Private constructor — only Builder can create this
    private AcademicReport(Builder builder) {
        this.title          = builder.title;
        this.generatedBy    = builder.generatedBy;
        this.generatedDate  = builder.generatedDate;
        this.academicYear   = builder.academicYear;
        this.summary        = builder.summary;
        this.courseStats    = builder.courseStats;
        this.studentDetails = builder.studentDetails;
        this.notes          = builder.notes;
        this.includeCharts  = builder.includeCharts;
    }

    // Getters
    public String getTitle()                           { return title; }
    public String getGeneratedBy()                     { return generatedBy; }
    public LocalDate getGeneratedDate()                { return generatedDate; }
    public String getAcademicYear()                    { return academicYear; }
    public Map<String, Object> getSummary()            { return summary; }
    public List<Map<String, Object>> getCourseStats()  { return courseStats; }
    public List<Map<String, Object>> getStudentDetails(){ return studentDetails; }
    public String getNotes()                           { return notes; }
    public boolean isIncludeCharts()                   { return includeCharts; }

    /**
     * BUILDER — Inner static class
     * Allows constructing AcademicReport step by step with only needed fields.
     */
    public static class Builder {
        // Required
        private final String title;
        private final String generatedBy;

        // Optional — have defaults
        private LocalDate generatedDate  = LocalDate.now();
        private String academicYear      = "";
        private Map<String, Object> summary           = Map.of();
        private List<Map<String, Object>> courseStats  = List.of();
        private List<Map<String, Object>> studentDetails = List.of();
        private String notes             = "";
        private boolean includeCharts    = false;

        public Builder(String title, String generatedBy) {
            this.title       = title;
            this.generatedBy = generatedBy;
        }

        public Builder academicYear(String year)                          { this.academicYear = year;      return this; }
        public Builder summary(Map<String, Object> summary)               { this.summary = summary;        return this; }
        public Builder courseStats(List<Map<String, Object>> stats)       { this.courseStats = stats;      return this; }
        public Builder studentDetails(List<Map<String, Object>> details)  { this.studentDetails = details; return this; }
        public Builder notes(String notes)                                 { this.notes = notes;            return this; }
        public Builder includeCharts(boolean include)                      { this.includeCharts = include;  return this; }
        public Builder generatedDate(LocalDate date)                       { this.generatedDate = date;     return this; }

        /** Final build step — returns the complete AcademicReport */
        public AcademicReport build() {
            return new AcademicReport(this);
        }
    }

    @Override
    public String toString() {
        return String.format("AcademicReport{title='%s', year='%s', by='%s', date=%s}",
            title, academicYear, generatedBy, generatedDate);
    }
}
