package com.kbslblog_api.repository;

import com.kbslblog_api.entity.ImageFile;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ImageFileRepository extends JpaRepository<ImageFile, Long> {
    Optional<ImageFile> findByHash(String hash);
}
