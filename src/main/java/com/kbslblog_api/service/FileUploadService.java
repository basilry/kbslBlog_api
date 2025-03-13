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

@Service
@RequiredArgsConstructor
public class FileUploadService {
    private final ImageFileRepository imageFileRepository;

    @Value("${google.drive.folder.id}")
    private String FOLDER_ID;

    private static final String APPLICATION_NAME = "basilry.kim";
    private static final JsonFactory JSON_FACTORY = JacksonFactory.getDefaultInstance();
    private static final String TOKENS_DIRECTORY_PATH = "tokens";
    private static final List<String> SCOPES = Collections.singletonList(DriveScopes.DRIVE_FILE);
    private static final String CREDENTIALS_FILE_PATH = "/credentials.json";
    private static final String SERVICE_ACCOUNT_KEY_PATH = "/service-account-key.json";

    private static Drive driveService;

    private Credential getCredentials(final NetHttpTransport HTTP_TRANSPORT) throws Exception {
        InputStream in = FileUploadService.class.getResourceAsStream(CREDENTIALS_FILE_PATH);
        if (in == null) {
            throw new Exception("클라이언트 비밀 파일을 찾을 수 없습니다: " + CREDENTIALS_FILE_PATH);
        }
        GoogleClientSecrets clientSecrets = GoogleClientSecrets.load(JSON_FACTORY, new InputStreamReader(in));

        GoogleAuthorizationCodeFlow flow = new GoogleAuthorizationCodeFlow.Builder(
                HTTP_TRANSPORT, JSON_FACTORY, clientSecrets, SCOPES)
                .setDataStoreFactory(new FileDataStoreFactory(new java.io.File(TOKENS_DIRECTORY_PATH)))
                .setAccessType("offline")
                .build();

        // 리디렉션 URI를 Google Cloud Console에 등록된 것과 정확히 일치시킴
        LocalServerReceiver receiver = new LocalServerReceiver.Builder()
            .setPort(18080)
            .setCallbackPath("/auth/callback")  // 콜백 경로 추가
            .build();

        // "user"는 이 Credential이 저장될 사용자 식별자 (여러 사용자 지원 시 고유한 식별자를 사용)
        return new AuthorizationCodeInstalledApp(flow, receiver).authorize("user");
    }

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

    public String uploadFile(java.io.File filePath) throws Exception {
        Drive driveService = getDriveService();

        byte[] fileBytes = Files.readAllBytes(filePath.toPath());
        String fileHash = generateOptimizedHash(fileBytes);

        Optional<ImageFile> existingImage = imageFileRepository.findByHash(fileHash);
        if (existingImage.isPresent()) {
            System.out.println("중복 파일 발견: " + fileHash + ", 기존 URL 반환: " + existingImage.get().getUrl());
            return existingImage.get().getUrl();
        }

        // 파일 크기 정보 명시적으로 전달
        FileContent mediaContent = new FileContent("image/jpeg", filePath);
        return uploadToGoogleDrive(driveService, filePath.getName(), mediaContent, fileHash, filePath.length());
    }


    public List<String> uploadMultiFile(List<String> base64Images) throws Exception {
        List<String> uploadedUrls = new ArrayList<>();
        Drive driveService = getDriveService();

        for (String base64Image : base64Images) {
            byte[] decodedBytes = Base64.getDecoder().decode(base64Image);
            String fileHash = generateOptimizedHash(decodedBytes);

            Optional<ImageFile> existingImage = imageFileRepository.findByHash(fileHash);
            if (existingImage.isPresent()) {
                uploadedUrls.add(existingImage.get().getUrl());
                continue;
            }

            String generatedName = "uploaded-image-" + UUID.randomUUID() + ".jpg";
            
            // ByteArrayContent 생성 시 파일 크기 정보 전달
            ByteArrayContent mediaContent = new ByteArrayContent("image/jpeg", decodedBytes);
            
            // uploadToGoogleDrive 메서드 호출 시 파일 크기 정보 전달
            uploadedUrls.add(uploadToGoogleDrive(
                driveService, 
                generatedName, 
                mediaContent, 
                fileHash, 
                (long) decodedBytes.length  // 파일 크기 전달
            ));
        }

        return uploadedUrls;
    }

    /**
     * 외부 URL에서 이미지를 다운로드하여 Google Drive에 업로드
     * @param externalUrls 외부 이미지 URL 목록
     * @return 업로드된 이미지 URL 목록
     */
    public List<String> uploadExternalImages(List<String> externalUrls) throws Exception {
        List<String> uploadedUrls = new ArrayList<>();
        Drive driveService = getDriveService();

        for (String externalUrl : externalUrls) {
            try {
                // URL에서 이미지 다운로드
                java.net.URL url = new java.net.URL(externalUrl);
                try (InputStream inputStream = url.openStream()) {
                    // 이미지 데이터를 바이트 배열로 변환
                    byte[] imageBytes = inputStream.readAllBytes();
                    
                    // 해시 생성
                    String fileHash = generateOptimizedHash(imageBytes);
                    
                    // 중복 확인
                    Optional<ImageFile> existingImage = imageFileRepository.findByHash(fileHash);
                    if (existingImage.isPresent()) {
                        uploadedUrls.add(existingImage.get().getUrl());
                        continue;
                    }
                    
                    // 파일 이름 생성 (URL에서 파일 이름 추출 또는 UUID 생성)
                    String fileName = "external-image-" + UUID.randomUUID() + ".jpg";
                    
                    // MIME 타입 추정 (URL 경로에서 확장자 추출)
                    String mimeType = "image/jpeg"; // 기본값
                    if (externalUrl.toLowerCase().endsWith(".png")) {
                        mimeType = "image/png";
                    } else if (externalUrl.toLowerCase().endsWith(".gif")) {
                        mimeType = "image/gif";
                    } else if (externalUrl.toLowerCase().endsWith(".webp")) {
                        mimeType = "image/webp";
                    }
                    
                    // ByteArrayContent 생성
                    ByteArrayContent mediaContent = new ByteArrayContent(mimeType, imageBytes);
                    
                    // Google Drive에 업로드 (파일 크기 정보 전달)
                    String uploadedUrl = uploadToGoogleDrive(
                            driveService, 
                            fileName, 
                            mediaContent, 
                            fileHash,
                            (long) imageBytes.length  // 파일 크기 전달
                    );
                    
                    uploadedUrls.add(uploadedUrl);
                }
            } catch (Exception e) {
                System.err.println("외부 이미지 다운로드 중 오류 발생: " + e.getMessage());
                // 오류 발생 시 원본 URL 반환
                uploadedUrls.add(externalUrl);
            }
        }
        
        return uploadedUrls;
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

    private String uploadToGoogleDrive(Drive driveService, String fileName, AbstractInputStreamContent mediaContent, String fileHash, Long fileSize) throws Exception {
        System.out.println("파일 업로드 시작: " + fileName);
        
        // 1. 먼저 서비스 계정의 드라이브에 파일 업로드
        File fileMetadata = new File();
        fileMetadata.setName(fileName);
        
        try {
            // 임시로 서비스 계정 드라이브에 업로드
            File uploadedFile = driveService.files().create(fileMetadata, mediaContent)
                    .setFields("id, name")
                    .execute();
            
            // 2. 개인 드라이브의 지정된 폴더로 파일 복사
            File copyMetadata = new File();
            copyMetadata.setName(fileName);
            copyMetadata.setParents(Collections.singletonList(FOLDER_ID));
            
            File copiedFile = driveService.files().copy(uploadedFile.getId(), copyMetadata)
                    .setFields("id, webContentLink")
                    .execute();
            
            // 3. 파일 URL 생성 및 DB에 저장
            String fileUrl = "https://drive.google.com/uc?id=" + copiedFile.getId();
            
            ImageFile newImage = new ImageFile();
            newImage.setHash(fileHash);
            newImage.setUrl(fileUrl);
            
            // 파일 크기 설정 (전달받은 값 사용)
            if (fileSize != null) {
                newImage.setFileSize(fileSize);
            } else {
                // mediaContent에서 파일 크기와 타입 정보 추출 (기존 로직 유지)
                if (mediaContent instanceof FileContent) {
                    java.io.File file = ((FileContent) mediaContent).getFile();
                    newImage.setFileSize(file.length());
                } else if (mediaContent instanceof ByteArrayContent) {
                    // ByteArrayContent의 경우 크기를 직접 가져올 수 없으므로 
                    // 호출 시점에서 크기 정보를 전달받아야 함
                    // 여기서는 간단히 0으로 설정
                    newImage.setFileSize(0L);
                }
            }
            
            newImage.setMimeType(mediaContent.getType());
            imageFileRepository.save(newImage);
            
            // 4. 권한 설정 및 원본 파일 삭제를 비동기적으로 처리 (응답 반환 후)
            CompletableFuture.runAsync(() -> {
                try {
                    // 공개 접근 권한 설정 (복사된 파일에)
                    driveService.permissions().create(copiedFile.getId(), new Permission()
                            .setType("anyone")
                            .setRole("reader"))
                            .execute();
                    
                    // 원본 파일 삭제 (서비스 계정 드라이브에서)
                    driveService.files().delete(uploadedFile.getId()).execute();
                } catch (Exception e) {
                    System.err.println("파일 권한 설정 또는 삭제 중 오류 발생: " + e.getMessage());
                }
            });
            
            // 즉시 URL 반환 (권한 설정 완료 전)
            return fileUrl;
            
        } catch (Exception e) {
            System.err.println("파일 업로드 중 오류 발생: " + e.getMessage());
            throw e;
        }
    }
    
    // 기존 메서드 오버로드 (하위 호환성 유지)
    private String uploadToGoogleDrive(Drive driveService, String fileName, AbstractInputStreamContent mediaContent, String fileHash) throws Exception {
        return uploadToGoogleDrive(driveService, fileName, mediaContent, fileHash, null);
    }
}
