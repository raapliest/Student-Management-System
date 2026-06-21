package com.sapms.service;

import com.sapms.dao.AnnouncementDao;
import com.sapms.entity.Announcement;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class AnnouncementService {
    @Autowired
    private AnnouncementDao announcementDao;

    public List<Announcement> getAll() {
        return announcementDao.findAll();
    }

    public List<Announcement> getForRole(String role) {
        return announcementDao.findForRole(role);
    }

    public void post(String title, String content, String createdBy, String targetRole) {
        Announcement a = new Announcement();
        a.setTitle(title);
        a.setContent(content);
        a.setCreatedBy(createdBy);
        a.setTargetRole(targetRole);
        announcementDao.insert(a);
    }

    public void delete(Long id) {
        announcementDao.delete(id);
    }
}