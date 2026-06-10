package com.sapms.entity;
import java.time.LocalDate;
public class Attendance {
    private Long id,courseId;
    private String studentNo,status,note,studentName,courseCode,courseName;
    private LocalDate sessionDate;
    public Long getId(){return id;} public void setId(Long v){id=v;}
    public Long getCourseId(){return courseId;} public void setCourseId(Long v){courseId=v;}
    public String getStudentNo(){return studentNo;} public void setStudentNo(String v){studentNo=v;}
    public String getStatus(){return status;} public void setStatus(String v){status=v;}
    public String getNote(){return note;} public void setNote(String v){note=v;}
    public String getStudentName(){return studentName;} public void setStudentName(String v){studentName=v;}
    public String getCourseCode(){return courseCode;} public void setCourseCode(String v){courseCode=v;}
    public String getCourseName(){return courseName;} public void setCourseName(String v){courseName=v;}
    public LocalDate getSessionDate(){return sessionDate;} public void setSessionDate(LocalDate v){sessionDate=v;}
}