package com.springProject.Practice.controller;

import com.springProject.Practice.dto.FileResponse;
import com.springProject.Practice.dto.ResponseAPI;
import com.springProject.Practice.service.FileServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;



@RestController
@RequestMapping("/file")
public class FileController {

    @Autowired
    private FileServiceImpl fileService;

    @PostMapping("/upload")
    public  ResponseEntity<ResponseAPI> uploadFile(@RequestParam("file") MultipartFile file) {
        return fileService.uploadFile(file);
    }
    @GetMapping("/download{id}")
    public ResponseEntity<byte[]> downloadFile(@RequestParam long id) {
        return fileService.downloadFile(id);
    }

    @GetMapping("/view")
    public ResponseEntity<byte[]> viewFile(@RequestParam long id){
        return fileService.viewFile(id);
    }

    @GetMapping("/url/{id}")
    public ResponseEntity<String> getFile(@PathVariable long id){
        return fileService.generatePreSignUrl(id);
    }
    @GetMapping("/my-files")
    public ResponseEntity<Page<FileResponse>>
    getMyFiles(@RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "5") int size) {
        return ResponseEntity.ok(
                fileService.getMyFiles(page, size));
    }
    @GetMapping("/my-files/{search}")
    public ResponseEntity<Page<FileResponse>> searchMyFiles(@PathVariable String search,@RequestParam (defaultValue = "0")int page,@RequestParam (defaultValue = "5")int size){
        return ResponseEntity.ok(fileService.searchMyFiles(search, page, size));
    }
    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/admin/test")
    public String adminOnly() {

        return "Admin access granted";
    }
}
