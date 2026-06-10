package com.sapms.entity;
public class Student {
    private String studentNo,fullName,email,majorNo,classNo,studentSeq,majorName,className,academicYearLabel;
    private Long userId,academicYearId;
    public String getStudentNo(){return studentNo;} public void setStudentNo(String v){studentNo=v;}
    public String getFullName(){return fullName;} public void setFullName(String v){fullName=v;}
    public String getEmail(){return email;} public void setEmail(String v){email=v;}
    public String getMajorNo(){return majorNo;} public void setMajorNo(String v){majorNo=v;}
    public String getClassNo(){return classNo;} public void setClassNo(String v){classNo=v;}
    public String getStudentSeq(){return studentSeq;} public void setStudentSeq(String v){studentSeq=v;}
    public String getMajorName(){return majorName;} public void setMajorName(String v){majorName=v;}
    public String getClassName(){return className;} public void setClassName(String v){className=v;}
    public String getAcademicYearLabel(){return academicYearLabel;} public void setAcademicYearLabel(String v){academicYearLabel=v;}
    public Long getUserId(){return userId;} public void setUserId(Long v){userId=v;}
    public Long getAcademicYearId(){return academicYearId;} public void setAcademicYearId(Long v){academicYearId=v;}
}