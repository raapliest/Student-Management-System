package com.sapms.entity;

public class Course {
    private Long id, academicYearId, lecturerId;
    private String courseCode, courseName, majorNo, majorName, lecturerName, academicYearLabel;
    private int creditHours;

    public Long getId() {
        return id;
    }

    public void setId(Long v) {
        id = v;
    }

    public Long getAcademicYearId() {
        return academicYearId;
    }

    public void setAcademicYearId(Long v) {
        academicYearId = v;
    }

    public Long getLecturerId() {
        return lecturerId;
    }

    public void setLecturerId(Long v) {
        lecturerId = v;
    }

    public String getCourseCode() {
        return courseCode;
    }

    public void setCourseCode(String v) {
        courseCode = v;
    }

    public String getCourseName() {
        return courseName;
    }

    public void setCourseName(String v) {
        courseName = v;
    }

    public String getMajorNo() {
        return majorNo;
    }

    public void setMajorNo(String v) {
        majorNo = v;
    }

    public String getMajorName() {
        return majorName;
    }

    public void setMajorName(String v) {
        majorName = v;
    }

    public String getLecturerName() {
        return lecturerName;
    }

    public void setLecturerName(String v) {
        lecturerName = v;
    }

    public String getAcademicYearLabel() {
        return academicYearLabel;
    }

    public void setAcademicYearLabel(String v) {
        academicYearLabel = v;
    }

    public int getCreditHours() {
        return creditHours;
    }

    public void setCreditHours(int v) {
        creditHours = v;
    }
}