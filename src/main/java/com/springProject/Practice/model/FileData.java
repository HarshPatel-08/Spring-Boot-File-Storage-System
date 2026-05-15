package com.springProject.Practice.model;

import jakarta.persistence.*;

import java.time.LocalDateTime;

@Entity
public class FileData {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String originalFileName;
    private String objectFileName;
    private String contentType;
    private long fileSize;
    private LocalDateTime cretedAt;
    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    public FileData() {
    }

    public FileData(Long id, String originalFileName, String objectFileName, String contentType, long fileSize, User user) {
        this.id = id;
        this.originalFileName = originalFileName;
        this.objectFileName = objectFileName;
        this.contentType = contentType;
        this.fileSize = fileSize;
        this.user = user;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getOriginalFileName() {
        return originalFileName;
    }

    public void setOriginalFileName(String originalFileName) {
        this.originalFileName = originalFileName;
    }

    public String getObjectFileName() {
        return objectFileName;
    }

    public void setObjectFileName(String objectFileName) {
        this.objectFileName = objectFileName;
    }

    public String getContentType() {
        return contentType;
    }

    public void setContentType(String contentType) {
        this.contentType = contentType;
    }

    public long getFileSize() {
        return fileSize;
    }

    public void setFileSize(long fileSize) {
        this.fileSize = fileSize;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public LocalDateTime getCretedAt() {
        return cretedAt;
    }

    public void setCretedAt(LocalDateTime cretedAt) {
        this.cretedAt = cretedAt;
    }
}
