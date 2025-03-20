package com.kbslblog_api.service;

import com.google.api.client.auth.oauth2.Credential;
import com.google.api.client.extensions.java6.auth.oauth2.AuthorizationCodeInstalledApp;
import com.google.api.client.extensions.jetty.auth.oauth2.LocalServerReceiver;
import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeFlow;
import com.google.api.client.googleapis.auth.oauth2.GoogleClientSecrets;
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.http.AbstractInputStreamContent;
import com.google.api.client.http.FileContent;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.client.util.store.FileDataStoreFactory;
import com.google.api.services.drive.Drive;
import com.google.api.services.drive.DriveScopes;
import com.google.api.services.drive.model.File;
import com.google.api.services.drive.model.Permission;

import java.io.FileInputStream;
import java.nio.file.Files;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Optional;

import com.kbslblog_api.entity.ImageFile;
import com.kbslblog_api.repository.ImageFileRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Collections;
import java.util.List;
import java.util.ArrayList;
import java.util.Base64;
import java.util.UUID;
import com.google.api.client.http.ByteArrayContent;
import com.google.auth.http.HttpCredentialsAdapter;
import com.google.auth.oauth2.GoogleCredentials;
import com.google.auth.oauth2.ServiceAccountCredentials;
import java.util.concurrent.CompletableFuture;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.BatchPreparedStatementSetter;

@Service
@RequiredArgsConstructor
public class FileUploadService {
    private final ImageFileRepository imageFileRepository;
    private final JdbcTemplate jdbcTemplate;

    @Value("${google.drive.folder.id}")
    private String FOLDER_ID;

    private static final String APPLICATION_NAME = "basilry.kim";
    private static final JsonFactory JSON_FACTORY = JacksonFactory.getDefaultInstance();
    private static final String TOKENS_DIRECTORY_PATH = "tokens";
    private static final List<String> SCOPES = Collections.singletonList(DriveScopes.DRIVE_FILE);
    private static final String CREDENTIALS_FILE_PATH = "/credentials.json";
    private static final String SERVICE_ACCOUNT_KEY_PATH = "/service-account-key.json";

    private static Drive driveService;
    
    // 병렬 처리를 위한 스레드 풀 (최대 5개 스레드로 제한)
    private static final java.util.concurrent.ExecutorService executorService = 
            java.util.concurrent.Executors.newFixedThreadPool(5);


    /**
     * 서비스 계정을 사용하여 Google Drive API 클라이언트 생성
     */
    private synchronized Drive getDriveService() throws Exception {
        if (driveService != null) {
            return driveService;
        }
        
        // 클래스패스에서 파일 로드 시도
        InputStream serviceAccountStream = FileUploadService.class.getResourceAsStream(SERVICE_ACCOUNT_KEY_PATH);
        
        // 파일을 찾을 수 없는 경우 파일 시스템에서 직접 로드 시도
        if (serviceAccountStream == null) {
            try {
                java.io.File keyFile = new java.io.File("src/main/resources" + SERVICE_ACCOUNT_KEY_PATH);
                if (keyFile.exists()) {
                    serviceAccountStream = new FileInputStream(keyFile);
                } else {
                    throw new Exception("서비스 계정 키 파일을 찾을 수 없습니다: " + SERVICE_ACCOUNT_KEY_PATH);
                }
            } catch (Exception e) {
                throw new Exception("서비스 계정 키 파일 로드 중 오류 발생: " + e.getMessage());
            }
        }
        
        GoogleCredentials credentials = ServiceAccountCredentials.fromStream(serviceAccountStream)
                .createScoped(SCOPES);
        
        final NetHttpTransport HTTP_TRANSPORT = GoogleNetHttpTransport.newTrustedTransport();
        driveService = new Drive.Builder(HTTP_TRANSPORT, JSON_FACTORY, new HttpCredentialsAdapter(credentials))
                .setApplicationName(APPLICATION_NAME)
                .build();
        
        return driveService;
    }

    /**
     * MultipartFile을 직접 처리하여 임시 파일 생성 과정을 생략
     * @param file MultipartFile 객체
     * @return 업로드된 파일의 URL
     */
    public String uploadFileDirectly(org.springframework.web.multipart.MultipartFile file) throws Exception {
        Drive driveService = getDriveService();
        
        // 파일 데이터를 바이트 배열로 직접 추출
        byte[] fileBytes = file.getBytes();
        
        // 파일 해시 생성
        String fileHash = generateOptimizedHash(fileBytes);
        
        // 중복 확인
        Optional<ImageFile> existingImage = imageFileRepository.findByHash(fileHash);
        if (existingImage.isPresent()) {
            System.out.println("중복 파일 발견: " + fileHash + ", 기존 URL 반환: " + existingImage.get().getUrl());
            return existingImage.get().getUrl();
        }
        
        // 파일 크기가 큰 경우 최적화 (2MB 이상)
        byte[] processedBytes = fileBytes;
        if (fileBytes.length > 2 * 1024 * 1024) {
            try {
                processedBytes = optimizeImage(fileBytes, file.getOriginalFilename());
                System.out.println("이미지 최적화 완료: " + file.getOriginalFilename() + 
                                  " (" + (fileBytes.length / 1024 / 1024) + "MB -> " + 
                                  (processedBytes.length / 1024 / 1024) + "MB)");
            } catch (Exception e) {
                System.err.println("이미지 최적화 실패, 원본 사용: " + e.getMessage());
            }
        }
        
        // MIME 타입 결정
        String mimeType = file.getContentType();
        if (mimeType == null || mimeType.isEmpty()) {
            mimeType = getMimeTypeFromFilename(file.getOriginalFilename());
        }
        
        // ByteArrayContent 생성
        ByteArrayContent mediaContent = new ByteArrayContent(mimeType, processedBytes);
        
        // 파일 이름 생성 (원본 파일명 유지)
        String fileName = file.getOriginalFilename();
        if (fileName == null || fileName.isEmpty()) {
            fileName = "uploaded-file-" + UUID.randomUUID() + getExtensionFromMimeType(mimeType);
        }
        
        // 파일 메타데이터 생성
        File fileMetadata = new File();
        fileMetadata.setName(fileName);
        
        // 임시로 서비스 계정 드라이브에 업로드
        File uploadedFile = driveService.files().create(fileMetadata, mediaContent)
                .setFields("id, name")
                .execute();
        
        // 개인 드라이브의 지정된 폴더로 파일 복사
        File copyMetadata = new File();
        copyMetadata.setName(fileName);
        copyMetadata.setParents(Collections.singletonList(FOLDER_ID));
        
        File copiedFile = driveService.files().copy(uploadedFile.getId(), copyMetadata)
                .setFields("id, webContentLink")
                .execute();
        
        // 파일 URL 생성
        String fileUrl = "https://drive.google.com/uc?id=" + copiedFile.getId();
        String proxyUrl = "/proxy/image/" + copiedFile.getId();
        
        // ImageFile 객체 생성 및 저장
        ImageFile newImage = new ImageFile();
        newImage.setHash(fileHash);
        newImage.setUrl(fileUrl);
        newImage.setFileSize((long) processedBytes.length);
        newImage.setMimeType(mimeType);
        
        // 데이터베이스에 저장
        imageFileRepository.save(newImage);
        
        // 권한 설정 및 원본 파일 삭제를 비동기적으로 처리
        final String uploadedFileId = uploadedFile.getId();
        final String copiedFileId = copiedFile.getId();
        CompletableFuture.runAsync(() -> {
            try {
                // 공개 접근 권한 설정 (복사된 파일에)
                driveService.permissions().create(copiedFileId, new Permission()
                        .setType("anyone")
                        .setRole("reader"))
                        .execute();
                
                // 원본 파일 삭제 (서비스 계정 드라이브에서)
                driveService.files().delete(uploadedFileId).execute();
            } catch (Exception e) {
                System.err.println("파일 권한 설정 또는 삭제 중 오류 발생: " + e.getMessage());
            }
        });
        
        return proxyUrl;
    }
    
    /**
     * MIME 타입에서 파일 확장자 추출
     */
    private String getExtensionFromMimeType(String mimeType) {
        if (mimeType == null) return ".jpg";
        
        switch (mimeType.toLowerCase()) {
            case "image/png": return ".png";
            case "image/gif": return ".gif";
            case "image/webp": return ".webp";
            case "image/jpeg":
            case "image/jpg":
            default: return ".jpg";
        }
    }
    
    /**
     * 파일명에서 MIME 타입 추정
     */
    private String getMimeTypeFromFilename(String filename) {
        if (filename == null) return "image/jpeg";
        
        String lowerFilename = filename.toLowerCase();
        if (lowerFilename.endsWith(".png")) return "image/png";
        if (lowerFilename.endsWith(".gif")) return "image/gif";
        if (lowerFilename.endsWith(".webp")) return "image/webp";
        return "image/jpeg"; // 기본값
    }

    @Transactional
    public List<String> uploadMultiFile(List<String> base64Images) throws Exception {
        List<String> uploadedUrls = new ArrayList<>();
        Drive driveService = getDriveService();
        
        // 일괄 삽입을 위한 ImageFile 리스트
        List<ImageFile> imagesToSave = new ArrayList<>();

        for (String base64Image : base64Images) {
            byte[] decodedBytes = Base64.getDecoder().decode(base64Image);
            String fileHash = generateOptimizedHash(decodedBytes);

            Optional<ImageFile> existingImage = imageFileRepository.findByHash(fileHash);
            if (existingImage.isPresent()) {
                uploadedUrls.add(existingImage.get().getUrl());
                continue;
            }

            String generatedName = "uploaded-image-" + UUID.randomUUID() + ".jpg";
            
            // ByteArrayContent 생성
            ByteArrayContent mediaContent = new ByteArrayContent("image/jpeg", decodedBytes);
            
            // 파일 메타데이터 생성
            File fileMetadata = new File();
            fileMetadata.setName(generatedName);
            
            // 임시로 서비스 계정 드라이브에 업로드
            File uploadedFile = driveService.files().create(fileMetadata, mediaContent)
                    .setFields("id, name")
                    .execute();
            
            // 개인 드라이브의 지정된 폴더로 파일 복사
            File copyMetadata = new File();
            copyMetadata.setName(generatedName);
            copyMetadata.setParents(Collections.singletonList(FOLDER_ID));
            
            File copiedFile = driveService.files().copy(uploadedFile.getId(), copyMetadata)
                    .setFields("id, webContentLink")
                    .execute();
            
            // 파일 URL 생성
            String fileUrl = "https://drive.google.com/uc?id=" + copiedFile.getId();
            String proxyUrl = "/proxy/image/" + copiedFile.getId();
            
            // ImageFile 객체 생성 (아직 저장하지 않음)
            ImageFile newImage = new ImageFile();
            newImage.setHash(fileHash);
            newImage.setUrl(fileUrl);
            newImage.setFileSize((long) decodedBytes.length);
            newImage.setMimeType("image/jpeg");
            
            // 일괄 삽입을 위해 리스트에 추가
            imagesToSave.add(newImage);
            
            // 결과 URL 추가
            uploadedUrls.add(proxyUrl);
            
            // 권한 설정 및 원본 파일 삭제를 비동기적으로 처리
            final String uploadedFileId = uploadedFile.getId();
            final String copiedFileId = copiedFile.getId();
            CompletableFuture.runAsync(() -> {
                try {
                    // 공개 접근 권한 설정 (복사된 파일에)
                    driveService.permissions().create(copiedFileId, new Permission()
                            .setType("anyone")
                            .setRole("reader"))
                            .execute();
                    
                    // 원본 파일 삭제 (서비스 계정 드라이브에서)
                    driveService.files().delete(uploadedFileId).execute();
                } catch (Exception e) {
                    System.err.println("파일 권한 설정 또는 삭제 중 오류 발생: " + e.getMessage());
                }
            });
        }
        
        // 모든 이미지 파일을 일괄 저장
        if (!imagesToSave.isEmpty()) {
            try {
                System.out.println("일괄 삽입 실행: " + imagesToSave.size() + "개 이미지");
                // JDBC 배치 처리 사용
                batchInsertWithJdbc(imagesToSave);
            } catch (Exception e) {
                System.err.println("일괄 삽입 중 오류 발생: " + e.getMessage());
                // 오류 발생 시 기존 방식으로 저장
                imageFileRepository.saveAll(imagesToSave);
            }
        }

        return uploadedUrls;
    }

    /**
     * 외부 URL에서 이미지를 다운로드하여 Google Drive에 업로드
     * @param externalUrls 외부 이미지 URL 목록
     * @return 업로드된 이미지 URL 목록
     */
    @Transactional
    public List<String> uploadExternalImages(List<String> externalUrls) throws Exception {
        List<String> uploadedUrls = new ArrayList<>(Collections.nCopies(externalUrls.size(), ""));
        Drive driveService = getDriveService();
        
        // 일괄 저장을 위한 ImageFile 리스트 (스레드 안전한 컬렉션 사용)
        List<ImageFile> imagesToSave = Collections.synchronizedList(new ArrayList<>());
        
        // 동기화를 위한 객체
        Object lock = new Object();
        
        // 병렬 처리를 위한 CompletableFuture 리스트
        List<CompletableFuture<Void>> futures = new ArrayList<>();
        
        for (int i = 0; i < externalUrls.size(); i++) {
            final int index = i;
            final String externalUrl = externalUrls.get(i);
            
            // 각 URL 처리를 별도 스레드에서 비동기적으로 실행
            futures.add(CompletableFuture.runAsync(() -> {
                try {
                    // URL에서 이미지 다운로드 (타임아웃 설정)
                    java.net.URL url = new java.net.URL(externalUrl);
                    java.net.HttpURLConnection connection = (java.net.HttpURLConnection) url.openConnection();
                    connection.setConnectTimeout(5000); // 연결 타임아웃 5초
                    connection.setReadTimeout(10000);   // 읽기 타임아웃 10초
                    
                    try (InputStream inputStream = connection.getInputStream()) {
                        // 이미지 데이터를 바이트 배열로 변환
                        byte[] imageBytes = inputStream.readAllBytes();
                        
                        // 이미지 크기가 너무 크면 최적화 (선택적)
                        byte[] optimizedBytes = optimizeImageIfNeeded(imageBytes, externalUrl);
                        
                        // 해시 생성
                        String fileHash = generateOptimizedHash(optimizedBytes);
                        
                        // 중복 확인 (동기화 필요)
                        Optional<ImageFile> existingImage = imageFileRepository.findByHash(fileHash);
                        if (existingImage.isPresent()) {
                            uploadedUrls.set(index, existingImage.get().getUrl());
                            return;
                        }
                        
                        // 파일 이름 생성
                        String fileName = "external-image-" + UUID.randomUUID() + getExtensionFromUrl(externalUrl);
                        
                        // MIME 타입 추정
                        String mimeType = getMimeTypeFromUrl(externalUrl);
                        
                        // ByteArrayContent 생성
                        ByteArrayContent mediaContent = new ByteArrayContent(mimeType, optimizedBytes);
                        
                        // 파일 메타데이터 생성
                        File fileMetadata = new File();
                        fileMetadata.setName(fileName);
                        
                        // 임시로 서비스 계정 드라이브에 업로드
                        File uploadedFile = driveService.files().create(fileMetadata, mediaContent)
                                .setFields("id, name")
                                .execute();
                        
                        // 개인 드라이브의 지정된 폴더로 파일 복사
                        File copyMetadata = new File();
                        copyMetadata.setName(fileName);
                        copyMetadata.setParents(Collections.singletonList(FOLDER_ID));
                        
                        File copiedFile = driveService.files().copy(uploadedFile.getId(), copyMetadata)
                                .setFields("id, webContentLink")
                                .execute();
                        
                        // 파일 URL 생성
                        String fileUrl = "https://drive.google.com/uc?id=" + copiedFile.getId();
                        String proxyUrl = "/proxy/image/" + copiedFile.getId();
                        
                        // ImageFile 객체 생성 (아직 저장하지 않음)
                        ImageFile newImage = new ImageFile();
                        newImage.setHash(fileHash);
                        newImage.setUrl(fileUrl);
                        newImage.setFileSize((long) optimizedBytes.length);
                        newImage.setMimeType(mimeType);
                        
                        // 일괄 저장을 위해 리스트에 추가
                        imagesToSave.add(newImage);
                        
                        // 결과 URL 설정
                        uploadedUrls.set(index, proxyUrl);
                        
                        // 권한 설정 및 원본 파일 삭제를 비동기적으로 처리
                        final String uploadedFileId = uploadedFile.getId();
                        final String copiedFileId = copiedFile.getId();
                        CompletableFuture.runAsync(() -> {
                            try {
                                // 공개 접근 권한 설정 (복사된 파일에)
                                driveService.permissions().create(copiedFileId, new Permission()
                                        .setType("anyone")
                                        .setRole("reader"))
                                        .execute();
                                
                                // 원본 파일 삭제 (서비스 계정 드라이브에서)
                                driveService.files().delete(uploadedFileId).execute();
                            } catch (Exception e) {
                                System.err.println("파일 권한 설정 또는 삭제 중 오류 발생: " + e.getMessage());
                            }
                        });
                    }
                } catch (Exception e) {
                    System.err.println("외부 이미지 다운로드 중 오류 발생: " + e.getMessage());
                    // 오류 발생 시 원본 URL 반환
                    uploadedUrls.set(index, externalUrl);
                }
            }, executorService));
        }
        
        // 모든 비동기 작업이 완료될 때까지 대기
        CompletableFuture.allOf(futures.toArray(new CompletableFuture[0])).join();
        
        // 모든 이미지 파일을 일괄 저장
        if (!imagesToSave.isEmpty()) {
            try {
                System.out.println("일괄 삽입 실행: " + imagesToSave.size() + "개 이미지");
                // JDBC 배치 처리 사용
                batchInsertWithJdbc(imagesToSave);
            } catch (Exception e) {
                System.err.println("일괄 삽입 중 오류 발생: " + e.getMessage());
                // 오류 발생 시 기존 방식으로 저장
                imageFileRepository.saveAll(imagesToSave);
            }
        }
        
        return uploadedUrls;
    }
    
    /**
     * URL에서 파일 확장자 추출
     */
    private String getExtensionFromUrl(String url) {
        String lowerUrl = url.toLowerCase();
        if (lowerUrl.endsWith(".png")) return ".png";
        if (lowerUrl.endsWith(".jpg") || lowerUrl.endsWith(".jpeg")) return ".jpg";
        if (lowerUrl.endsWith(".gif")) return ".gif";
        if (lowerUrl.endsWith(".webp")) return ".webp";
        return ".jpg"; // 기본값
    }
    
    /**
     * URL에서 MIME 타입 추정
     */
    private String getMimeTypeFromUrl(String url) {
        String lowerUrl = url.toLowerCase();
        if (lowerUrl.endsWith(".png")) return "image/png";
        if (lowerUrl.endsWith(".gif")) return "image/gif";
        if (lowerUrl.endsWith(".webp")) return "image/webp";
        return "image/jpeg"; // 기본값
    }
    
    /**
     * 이미지 최적화 (필요한 경우)
     * 현재는 단순히 원본을 반환하지만, 실제 구현에서는 이미지 크기 조정 등의 최적화 수행 가능
     */
    private byte[] optimizeImageIfNeeded(byte[] imageBytes, String url) {
        // 이미지 크기가 특정 임계값을 초과하는 경우에만 최적화
        // 예: 2MB 이상인 경우 최적화
        if (imageBytes.length > 2 * 1024 * 1024) {
            System.out.println("큰 이미지 감지: " + url + " (" + (imageBytes.length / 1024 / 1024) + "MB) - 최적화 시도");
            try {
                return optimizeImage(imageBytes, url);
            } catch (Exception e) {
                System.err.println("이미지 최적화 중 오류 발생: " + e.getMessage());
                return imageBytes; // 최적화 실패 시 원본 반환
            }
        }
        return imageBytes;
    }
    
    /**
     * 이미지 최적화 - 크기 조정 및 품질 감소
     */
    private byte[] optimizeImage(byte[] imageBytes, String url) throws Exception {
        // 이미지 포맷 결정
        String format = "jpeg"; // 기본값
        if (url.toLowerCase().endsWith(".png")) {
            format = "png";
        } else if (url.toLowerCase().endsWith(".gif")) {
            // GIF는 최적화하지 않고 원본 반환 (애니메이션 유지)
            return imageBytes;
        } else if (url.toLowerCase().endsWith(".webp")) {
            // WebP는 이미 최적화된 형식이므로 원본 반환
            return imageBytes;
        }
        
        // 바이트 배열에서 이미지 읽기
        java.io.ByteArrayInputStream bis = new java.io.ByteArrayInputStream(imageBytes);
        java.awt.image.BufferedImage originalImage = javax.imageio.ImageIO.read(bis);
        
        if (originalImage == null) {
            System.err.println("이미지를 읽을 수 없음: " + url);
            return imageBytes;
        }
        
        // 원본 크기
        int originalWidth = originalImage.getWidth();
        int originalHeight = originalImage.getHeight();
        
        // 최대 크기 설정 (예: 1920px)
        int maxDimension = 1920;
        
        // 크기 조정 필요 여부 확인
        if (originalWidth <= maxDimension && originalHeight <= maxDimension) {
            // 이미지가 이미 충분히 작은 경우, 품질만 조정
            return compressImage(originalImage, format, 0.8f);
        }
        
        // 비율 유지하며 크기 조정
        float ratio = (float) originalWidth / originalHeight;
        int newWidth, newHeight;
        
        if (originalWidth > originalHeight) {
            newWidth = maxDimension;
            newHeight = Math.round(maxDimension / ratio);
        } else {
            newHeight = maxDimension;
            newWidth = Math.round(maxDimension * ratio);
        }
        
        // 이미지 크기 조정
        java.awt.image.BufferedImage resizedImage = new java.awt.image.BufferedImage(
                newWidth, newHeight, java.awt.image.BufferedImage.TYPE_INT_RGB);
        
        java.awt.Graphics2D g = resizedImage.createGraphics();
        g.setRenderingHint(java.awt.RenderingHints.KEY_INTERPOLATION, 
                          java.awt.RenderingHints.VALUE_INTERPOLATION_BILINEAR);
        g.drawImage(originalImage, 0, 0, newWidth, newHeight, null);
        g.dispose();
        
        // 압축 품질 설정 및 바이트 배열로 변환
        return compressImage(resizedImage, format, 0.8f);
    }
    
    /**
     * 이미지 압축 - 품질 조정
     */
    private byte[] compressImage(java.awt.image.BufferedImage image, String format, float quality) throws Exception {
        java.io.ByteArrayOutputStream bos = new java.io.ByteArrayOutputStream();
        
        // JPEG 포맷인 경우 압축 품질 설정
        if (format.equals("jpeg")) {
            javax.imageio.ImageWriter jpegWriter = javax.imageio.ImageIO.getImageWritersByFormatName("jpeg").next();
            javax.imageio.ImageWriteParam jpegParams = jpegWriter.getDefaultWriteParam();
            jpegParams.setCompressionMode(javax.imageio.ImageWriteParam.MODE_EXPLICIT);
            jpegParams.setCompressionQuality(quality);
            
            javax.imageio.IIOImage iioImage = new javax.imageio.IIOImage(image, null, null);
            javax.imageio.stream.ImageOutputStream ios = javax.imageio.ImageIO.createImageOutputStream(bos);
            jpegWriter.setOutput(ios);
            jpegWriter.write(null, iioImage, jpegParams);
            jpegWriter.dispose();
            ios.close();
        } else {
            // 다른 포맷은 기본 설정으로 저장
            javax.imageio.ImageIO.write(image, format, bos);
        }
        
        byte[] optimizedBytes = bos.toByteArray();
        bos.close();
        
        System.out.println("이미지 최적화 완료: " + (image.getWidth() + "x" + image.getHeight()) + 
                          ", 크기 감소: 원본 -> " + (optimizedBytes.length / 1024) + "KB");
        
        return optimizedBytes;
    }

    private String generateHash(byte[] data) throws NoSuchAlgorithmException {
        MessageDigest digest = MessageDigest.getInstance("SHA-256");
        byte[] hashBytes = digest.digest(data);
        StringBuilder hexString = new StringBuilder();
        for (byte hashByte : hashBytes) {
            hexString.append(String.format("%02x", hashByte));
        }
        return hexString.toString();
    }

    private String generateOptimizedHash(byte[] data) throws NoSuchAlgorithmException {
        // 파일이 너무 크면 처음 1MB와 마지막 1MB만 해시
        if (data.length > 2 * 1024 * 1024) {
            byte[] sample = new byte[2 * 1024 * 1024];
            System.arraycopy(data, 0, sample, 0, 1024 * 1024);
            System.arraycopy(data, data.length - 1024 * 1024, sample, 1024 * 1024, 1024 * 1024);
            return generateHash(sample);
        }
        return generateHash(data);
    }

    /**
     * JDBC 배치 처리를 사용하여 이미지 파일을 일괄 삽입합니다.
     * @param images 삽입할 이미지 파일 목록
     */
    private void batchInsertWithJdbc(List<ImageFile> images) {
        String sql = "INSERT INTO image_files (hash, url, file_size, mime_type, created_at) VALUES (?, ?, ?, ?, ?)";
        
        jdbcTemplate.batchUpdate(sql, new BatchPreparedStatementSetter() {
            @Override
            public void setValues(java.sql.PreparedStatement ps, int i) throws java.sql.SQLException {
                ImageFile image = images.get(i);
                ps.setString(1, image.getHash());
                ps.setString(2, image.getUrl());
                ps.setLong(3, image.getFileSize() != null ? image.getFileSize() : 0);
                ps.setString(4, image.getMimeType());
                ps.setObject(5, image.getCreatedAt() != null ? image.getCreatedAt() : 
                   java.time.LocalDateTime.now(java.time.ZoneId.of("Asia/Seoul")));
            }
            
            @Override
            public int getBatchSize() {
                return images.size();
            }
        });
    }
}
