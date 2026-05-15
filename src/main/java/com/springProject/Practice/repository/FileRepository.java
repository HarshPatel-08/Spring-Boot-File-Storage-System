package com.springProject.Practice.repository;

import com.springProject.Practice.model.FileData;
import com.springProject.Practice.model.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;



@Repository
public interface FileRepository extends JpaRepository<FileData,Long> {
    Page<FileData>findByUser(User user, Pageable pageable);

    Page<FileData> findByUserAndOriginalFileNameContainingIgnoreCase(User currentUser, String search, Pageable pageable);
}
