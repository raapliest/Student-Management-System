package com.sapms.entity;

public class ClassInfo {
    private String classNo, majorNo, className, currentSeq;
    private int maxCount, studentCount;

    public String getClassNo() {
        return classNo;
    }

    public void setClassNo(String v) {
        classNo = v;
    }

    public String getMajorNo() {
        return majorNo;
    }

    public void setMajorNo(String v) {
        majorNo = v;
    }

    public String getClassName() {
        return className;
    }

    public void setClassName(String v) {
        className = v;
    }

    public String getCurrentSeq() {
        return currentSeq;
    }

    public void setCurrentSeq(String v) {
        currentSeq = v;
    }

    public int getMaxCount() {
        return maxCount;
    }

    public void setMaxCount(int v) {
        maxCount = v;
    }

    public int getStudentCount() {
        return studentCount;
    }

    public void setStudentCount(int v) {
        studentCount = v;
    }
}