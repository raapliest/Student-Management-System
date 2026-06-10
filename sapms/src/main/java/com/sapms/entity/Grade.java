package com.sapms.entity;
import java.math.BigDecimal;
public class Grade {
    private Long id,courseId;
    private String studentNo,gradeLetter,status,studentName,courseCode,courseName;
    private BigDecimal assignmentScore,midtermScore,finalScore,totalScore;
    public Long getId(){return id;} public void setId(Long v){id=v;}
    public Long getCourseId(){return courseId;} public void setCourseId(Long v){courseId=v;}
    public String getStudentNo(){return studentNo;} public void setStudentNo(String v){studentNo=v;}
    public String getGradeLetter(){return gradeLetter;} public void setGradeLetter(String v){gradeLetter=v;}
    public String getStatus(){return status;} public void setStatus(String v){status=v;}
    public String getStudentName(){return studentName;} public void setStudentName(String v){studentName=v;}
    public String getCourseCode(){return courseCode;} public void setCourseCode(String v){courseCode=v;}
    public String getCourseName(){return courseName;} public void setCourseName(String v){courseName=v;}
    public BigDecimal getAssignmentScore(){return assignmentScore;} public void setAssignmentScore(BigDecimal v){assignmentScore=v;}
    public BigDecimal getMidtermScore(){return midtermScore;} public void setMidtermScore(BigDecimal v){midtermScore=v;}
    public BigDecimal getFinalScore(){return finalScore;} public void setFinalScore(BigDecimal v){finalScore=v;}
    public BigDecimal getTotalScore(){return totalScore;} public void setTotalScore(BigDecimal v){totalScore=v;}
}