package com.springProject.Practice.service;

import com.springProject.Practice.dto.FileResponse;
import com.springProject.Practice.dto.ResponseAPI;
import com.springProject.Practice.exception.AccessDeniedException;
import com.springProject.Practice.exception.FileNotFoundException;
import com.springProject.Practice.exception.FileStorageException;
import com.springProject.Practice.model.FileData;
import com.springProject.Practice.model.User;
import com.springProject.Practice.repository.FileRepository;
import com.springProject.Practice.repository.UserRepository;

import io.minio.GetObjectArgs;
import io.minio.GetPresignedObjectUrlArgs;
import io.minio.MinioClient;
import io.minio.PutObjectArgs;
import io.minio.http.Method;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.beans.factory.annotation.Value;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import org.springframework.stereotype.Service;

import org.springframework.web.multipart.MultipartFile;

import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.util.Set;
import java.util.UUID;

@Service
public class FileServiceImpl implements FileService {

    private static final Logger log =
            LoggerFactory.getLogger(FileServiceImpl.class);

    private final FileRepository fileRepository;

    private final MinioClient minioClient;

    private final UserRepository userRepository;

    @Value("${minio.bucket.name}")
    private String bucketName;

    private static final long MAX_FILE_SIZE =
            5 * 1024 * 1024;

    private static final Set<String> ALLOWED_EXTENSIONS =
            Set.of(
                    "jpg",
                    "jpeg",
                    "png",
                    "gif"
            );

    public FileServiceImpl(
            FileRepository fileRepository,
            MinioClient minioClient,
            UserRepository userRepository
    ) {
        this.fileRepository = fileRepository;
        this.minioClient = minioClient;
        this.userRepository = userRepository;
    }

    @Override
    public ResponseEntity<ResponseAPI>
    uploadFile(MultipartFile file) {

        validateFile(file);

        String originalFileName =
                Paths.get(file.getOriginalFilename())
                        .getFileName()
                        .toString();

        String objectFileName =
                UUID.randomUUID()
                        + "_"
                        + originalFileName;

        FileData fileData = new FileData();

        fileData.setOriginalFileName(originalFileName);
        fileData.setObjectFileName(objectFileName);
        fileData.setContentType(file.getContentType());
        fileData.setFileSize(file.getSize());
        fileData.setUser(getCurrentUser());
        fileData.setCretedAt(LocalDateTime.now());

        log.info(
                "Uploading file: {}",
                originalFileName
        );

        try {

            minioClient.putObject(
                    PutObjectArgs.builder()
                            .bucket(bucketName)
                            .object(objectFileName)
                            .stream(
                                    file.getInputStream(),
                                    file.getSize(),
                                    -1
                            )
                            .contentType(file.getContentType())
                            .build()
            );

        } catch (Exception e) {

            throw new FileStorageException(
                    "Failed to upload file",
                    e
            );
        }

        fileRepository.save(fileData);

        log.info(
                "File uploaded successfully: {}",
                originalFileName
        );
        return ResponseEntity.ok(
                new ResponseAPI(
                        "File uploaded successfully",
                        true,
                        fileData.getId()
                )
        );

    }

    @Override
    public ResponseEntity<byte[]>
    downloadFile(long id) {

        FileData fileData =
                getAuthorizedFile(id);

        log.info(
                "Downloading file: {}",
                fileData.getOriginalFileName()
        );

        return ResponseEntity.ok()

                .contentType(
                        MediaType.parseMediaType(
                                fileData.getContentType()
                        )
                )

                .header(
                        HttpHeaders.CONTENT_DISPOSITION,
                        "attachment; filename=\""
                                + fileData.getOriginalFileName()
                                + "\""
                )

                .body(
                        getFileBytes(
                                fileData.getObjectFileName()
                        )
                );
    }

    @Override
    public ResponseEntity<byte[]>
    viewFile(long id) {

        FileData fileData =
                getAuthorizedFile(id);

        log.info(
                "Viewing file: {}",
                fileData.getOriginalFileName()
        );

        return ResponseEntity.ok()

                .contentType(
                        MediaType.parseMediaType(
                                fileData.getContentType()
                        )
                )

                .header(
                        HttpHeaders.CONTENT_DISPOSITION,
                        "inline; filename=\""
                                + fileData.getOriginalFileName()
                                + "\""
                )

                .body(
                        getFileBytes(
                                fileData.getObjectFileName()
                        )
                );
    }

    @Override
    public ResponseEntity<String>
    generatePreSignUrl(long id) {

        FileData fileData =
                getAuthorizedFile(id);

        log.info(
                "Generating URL for file: {}",
                fileData.getOriginalFileName()
        );

        try {

            String url =
                    minioClient.getPresignedObjectUrl(
                            GetPresignedObjectUrlArgs.builder()
                                    .bucket(bucketName)
                                    .object(
                                            fileData.getObjectFileName()
                                    )
                                    .method(Method.GET)
                                    .expiry(60 * 5)
                                    .build()
                    );

            return ResponseEntity.ok(url);

        } catch (Exception e) {

            throw new FileStorageException(
                    "Failed to generate pre-signed URL",
                    e
            );
        }
    }
    private void validateFile(
            MultipartFile file
    ) {

        if (file.isEmpty()) {

            throw new RuntimeException(
                    "File is empty"
            );
        }

        if (file.getSize() > MAX_FILE_SIZE) {

            throw new RuntimeException(
                    "File size should be less than 5MB"
            );
        }

        String contentType =
                file.getContentType();

        if (contentType == null ||
                !contentType.startsWith("image/")) {

            throw new RuntimeException(
                    "Only image files are allowed"
            );
        }

        String fileName =
                file.getOriginalFilename();

        if (fileName == null ||
                fileName.isBlank()) {

            throw new RuntimeException(
                    "File name is missing"
            );
        }

        String extension =
                fileName.substring(
                        fileName.lastIndexOf(".") + 1
                ).toLowerCase();

        if (!ALLOWED_EXTENSIONS.contains(extension)) {

            throw new RuntimeException(
                    "File type not allowed"
            );
        }
    }
    public Page<FileResponse>
    getMyFiles(
            int page,
            int size
    ) {

        Pageable pageable =
                PageRequest.of(
                        page,
                        size,
                        Sort.by("id")
                                .descending()
                );

        User currentUser =
                getCurrentUser();

        Page<FileData> filePage =
                fileRepository.findByUser(
                        currentUser, pageable
                );

        return filePage.map(file ->

                new FileResponse(
                        file.getId(),
                        file.getOriginalFileName(),
                        file.getContentType(),
                        file.getFileSize()
                )
        );
    }

    private FileData getAuthorizedFile(long id) {

        FileData fileData =
                fileRepository.findById(id)
                        .orElseThrow(() ->
                                new FileNotFoundException(
                                        "File not found"
                                )
                        );

        validateFileOwnership(
                fileData,
                getCurrentUser()
        );

        return fileData;
    }

    private byte[] getFileBytes(
            String objectName
    ) {

        try {

            return minioClient.getObject(
                    GetObjectArgs.builder()
                            .bucket(bucketName)
                            .object(objectName)
                            .build()
            ).readAllBytes();

        } catch (Exception e) {

            throw new FileStorageException(
                    "Failed to read file from storage",
                    e
            );
        }
    }
    private User getCurrentUser() {

        Authentication authentication =
                SecurityContextHolder
                        .getContext()
                        .getAuthentication();

        String email =
                authentication.getName();

        return userRepository.findByEmail(email)
                .orElseThrow(() ->
                        new RuntimeException(
                                "User not found"
                        )
                );
    }

    private void validateFileOwnership(
            FileData file,
            User currentUser
    ) {

        if (!file.getUser()
                .getId()
                .equals(currentUser.getId())) {

            throw new AccessDeniedException(
                    "Access denied"
            );
        }
    }

    public Page<FileResponse> searchMyFiles(String search, int page, int size){
        Pageable pageable = PageRequest.of(page,size,Sort.by("id").descending());
        User currentUser= getCurrentUser();
        Page<FileData> filePage = fileRepository.findByUserAndOriginalFileNameContainingIgnoreCase(currentUser,search,pageable);
        return filePage.map(file -> new FileResponse(
                file.getId(),
                file.getOriginalFileName(),
                file.getContentType(),
                file.getFileSize()));
    }
}