package com.springProject.Practice.dto;

public class FileResponse {

    private Long id;

    private String originalFileName;

    private String contentType;

    private Long fileSize;

    public FileResponse(
            Long id,
            String originalFileName,
            String contentType,
            Long fileSize
    ) {
        this.id = id;
        this.originalFileName = originalFileName;
        this.contentType = contentType;
        this.fileSize = fileSize;
    }

    public Long getId() {
        return id;
    }

    public String getOriginalFileName() {
        return originalFileName;
    }

    public String getContentType() {
        return contentType;
    }

    public Long getFileSize() {
        return fileSize;
    }
}