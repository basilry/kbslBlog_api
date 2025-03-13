package com.kbslblog_api.service;

import com.google.api.client.auth.oauth2.Credential;
import com.google.api.client.extensions.java6.auth.oauth2.AuthorizationCodeInstalledApp;
import com.google.api.client.extensions.jetty.auth.oauth2.LocalServerReceiver;
import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeFlow;
import com.google.api.client.googleapis.auth.oauth2.GoogleClientSecrets;
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.http.FileContent;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.client.util.store.FileDataStoreFactory;
import com.google.api.services.drive.Drive;
import com.google.api.services.drive.DriveScopes;
import com.google.api.services.drive.model.File;
import com.google.api.services.drive.model.Permission;
import java.nio.file.Files;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Optional;

import com.kbslblog_api.entity.ImageFile;
import com.kbslblog_api.repository.ImageFileRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Collections;
import java.util.List;
import java.util.ArrayList;
import java.util.Base64;
import java.util.UUID;
import com.google.api.client.http.ByteArrayContent;

@Service
@RequiredArgsConstructor
public class FileUploadService {
    private final ImageFileRepository imageFileRepository;

    private static final String APPLICATION_NAME = "basilry.kim";
    private static final JsonFactory JSON_FACTORY = JacksonFactory.getDefaultInstance();
    // 토큰을 저장할 디렉터리 (프로젝트 루트 또는 지정한 경로)
    private static final String TOKENS_DIRECTORY_PATH = "tokens";
    // 파일 업로드에 필요한 최소한의 스코프
    private static final List<String> SCOPES = Collections.singletonList(DriveScopes.DRIVE_FILE);
    // 클라이언트 비밀 파일 경로: resources 폴더의 루트에 credentials.json 파일이 있어야 함
    private static final String CREDENTIALS_FILE_PATH = "/credentials.json";


    /**
     * 사용자 Credential을 가져오는 메서드 (최초 인증 시 브라우저를 열어 인증 과정을 진행)
     */
    private Credential getCredentials(final NetHttpTransport HTTP_TRANSPORT) throws Exception {
        // resources 폴더에서 credentials.json 파일 로드 (파일이 클래스패스에 있어야 합니다)
        InputStream in = FileUploadService.class.getResourceAsStream(CREDENTIALS_FILE_PATH);
        if (in == null) {
            throw new Exception("클라이언트 비밀 파일을 찾을 수 없습니다: " + CREDENTIALS_FILE_PATH);
        }
        GoogleClientSecrets clientSecrets = GoogleClientSecrets.load(JSON_FACTORY, new InputStreamReader(in));

        // GoogleAuthorizationCodeFlow 생성 (토큰 저장 위치 지정)
        GoogleAuthorizationCodeFlow flow = new GoogleAuthorizationCodeFlow.Builder(
                HTTP_TRANSPORT, JSON_FACTORY, clientSecrets, SCOPES)
                .setDataStoreFactory(new FileDataStoreFactory(new java.io.File(TOKENS_DIRECTORY_PATH)))
                .setAccessType("offline")
                .build();

        // LocalServerReceiver: 로컬 서버(여기서는 포트 8080)를 통해 인증 응답 수신
        LocalServerReceiver receiver = new LocalServerReceiver.Builder().setPort(8080).build();

        // "user"는 이 Credential이 저장될 사용자 식별자 (여러 사용자 지원 시 고유한 식별자를 사용)
        return new AuthorizationCodeInstalledApp(flow, receiver).authorize("user");
    }

    /**
     * Google Drive API 클라이언트를 생성하는 메서드
     */
    private Drive getDriveService() throws Exception {
        final NetHttpTransport HTTP_TRANSPORT = GoogleNetHttpTransport.newTrustedTransport();
        return new Drive.Builder(HTTP_TRANSPORT, JSON_FACTORY, getCredentials(HTTP_TRANSPORT))
                .setApplicationName(APPLICATION_NAME)
                .build();
    }

    public String uploadFile(java.io.File filePath) throws Exception {
        Drive driveService = getDriveService();

        byte[] fileBytes = Files.readAllBytes(filePath.toPath());
        String fileHash = generateHash(fileBytes);

        Optional<ImageFile> existingImage = imageFileRepository.findByHash(fileHash);
        if (existingImage.isPresent()) {
            return existingImage.get().getUrl();
        }

        return uploadToGoogleDrive(driveService, filePath.getName(), new FileContent("image/jpeg", filePath), fileHash);
    }


    public List<String> uploadMultiFile(List<String> base64Images) throws Exception {
        List<String> uploadedUrls = new ArrayList<>();
        Drive driveService = getDriveService();

        for (String base64Image : base64Images) {
            byte[] decodedBytes = Base64.getDecoder().decode(base64Image);
            String fileHash = generateHash(decodedBytes);

            Optional<ImageFile> existingImage = imageFileRepository.findByHash(fileHash);
            if (existingImage.isPresent()) {
                uploadedUrls.add(existingImage.get().getUrl());
                continue;
            }

            String generatedName = "uploaded-image-" + UUID.randomUUID() + ".jpg";
            uploadedUrls.add(uploadToGoogleDrive(driveService, generatedName, new ByteArrayContent("image/jpeg", decodedBytes), fileHash));
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

    private String uploadToGoogleDrive(Drive driveService, String fileName, com.google.api.client.http.AbstractInputStreamContent mediaContent, String fileHash) throws Exception {
        File fileMetadata = new File();
        fileMetadata.setName(fileName);
        File uploadedFile = driveService.files().create(fileMetadata, mediaContent)
                .setFields("id")
                .execute();

        driveService.permissions().create(uploadedFile.getId(), new Permission()
                .setType("anyone")
                .setRole("reader"))
                .execute();

        String fileUrl = "https://drive.google.com/uc?id=" + uploadedFile.getId();

        ImageFile newImage = new ImageFile();
        newImage.setHash(fileHash);
        newImage.setUrl(fileUrl);
        imageFileRepository.save(newImage);

        return fileUrl;
    }
}
