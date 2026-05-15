package com.springProject.Practice.service;
import com.springProject.Practice.dto.ResponseAPI;
import org.springframework.http.ResponseEntity;
import org.springframework.web.multipart.MultipartFile;

public interface FileService {

    ResponseEntity<ResponseAPI> uploadFile(MultipartFile file);
    ResponseEntity<byte[]> downloadFile(long id) ;

    ResponseEntity<byte[]> viewFile(long id);

    ResponseEntity<String>
    generatePreSignUrl(long id) throws Exception;
}
