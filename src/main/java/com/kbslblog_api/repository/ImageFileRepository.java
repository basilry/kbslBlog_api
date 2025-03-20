package com.kbslblog_api.repository;

import com.kbslblog_api.entity.ImageFile;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ImageFileRepository extends JpaRepository<ImageFile, Long> {
    Optional<ImageFile> findByHash(String hash);
    
    Optional<ImageFile> findByUrl(String url);
    
    @Modifying
    @Query(value = "INSERT INTO image_files (hash, url, file_size, mime_type, created_at) VALUES (:hash, :url, :fileSize, :mimeType, :createdAt)", nativeQuery = true)
    void batchInsert(@Param("hash") String hash, @Param("url") String url, 
                    @Param("fileSize") Long fileSize, @Param("mimeType") String mimeType, 
                    @Param("createdAt") java.time.LocalDateTime createdAt);
                    
    @Modifying
    @Query(value = "INSERT INTO image_files (hash, url, file_size, mime_type, created_at) VALUES ?1", nativeQuery = true)
    void batchInsertMultiple(@Param("values") String values);
}
