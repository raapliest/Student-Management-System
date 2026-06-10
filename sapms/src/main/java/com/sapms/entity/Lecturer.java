package com.sapms.entity;
public class Lecturer {
    private Long id,userId;
    private String lecturerNo,fullName,email;
    public Long getId(){return id;} public void setId(Long v){id=v;}
    public Long getUserId(){return userId;} public void setUserId(Long v){userId=v;}
    public String getLecturerNo(){return lecturerNo;} public void setLecturerNo(String v){lecturerNo=v;}
    public String getFullName(){return fullName;} public void setFullName(String v){fullName=v;}
    public String getEmail(){return email;} public void setEmail(String v){email=v;}
}