package com.kbslblog_api.util;

import java.sql.Blob;
import javax.sql.rowset.serial.SerialBlob;
import org.springframework.web.multipart.MultipartFile;

public class FileUtil {
    public static Blob convertMultipartFileToBlob(MultipartFile file) throws Exception {
        byte[] bytes = file.getBytes();
        return new SerialBlob(bytes);
    }
}
