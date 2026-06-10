package com.sapms.entity;
import java.time.LocalDateTime;
public class Announcement {
    private Long id;
    private String title, content, createdBy, targetRole;
    private LocalDateTime createdAt;
    public Long getId(){return id;} public void setId(Long v){id=v;}
    public String getTitle(){return title;} public void setTitle(String v){title=v;}
    public String getContent(){return content;} public void setContent(String v){content=v;}
    public String getCreatedBy(){return createdBy;} public void setCreatedBy(String v){createdBy=v;}
    public String getTargetRole(){return targetRole;} public void setTargetRole(String v){targetRole=v;}
    public LocalDateTime getCreatedAt(){return createdAt;} public void setCreatedAt(LocalDateTime v){createdAt=v;}
}