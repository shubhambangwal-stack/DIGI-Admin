package com.vunum.SocietyAdmin.Utilities;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.security.SecureRandom;

@Slf4j
@Component
public class FileUploadUtil {

    private static final long MAX_FILE_SIZE = 25242880; // 5 MB in bytes
    private static final String FILE_UPLOAD_PATH = "uploads";
    SecureRandom random = new SecureRandom();
    @Autowired
    private CloudinaryService cloudinaryService;

    public void saveFile(String fileName, MultipartFile file) throws IOException {
        if (fileName == null || fileName.isEmpty()) {
            throw new IOException("Invalid file name");
        }

        if (file == null || file.isEmpty()) {
            throw new IOException("Invalid file");
        }

        File dir = new File(FILE_UPLOAD_PATH);
        if (!dir.exists()) {
            if (!dir.mkdirs()) {
                log.warn("Error creating directories for " + (dir).toString());
            }
        }

        String filePath = FILE_UPLOAD_PATH + File.separator + fileName;
        Files.copy(file.getInputStream(), Paths.get(filePath), StandardCopyOption.REPLACE_EXISTING);
    }

    // Method to validate and store the image
    public String validateAndConvertImage(MultipartFile file) throws IOException {
        if (file.isEmpty()) {
            throw new IllegalArgumentException("No file uploaded.");
        }

        if (file.getSize() > MAX_FILE_SIZE) {
            throw new IllegalArgumentException("File size exceeds 15 MB.");
        }

        String fileType = file.getContentType();
        assert fileType != null;
        if (!fileType.equalsIgnoreCase("image/jpeg") &&
                !fileType.equalsIgnoreCase("image/png") &&
                !fileType.equalsIgnoreCase("image/jpg")) {
            throw new IllegalArgumentException("Invalid file type. Only JPG, PNG, JPEG are allowed.");
        }
        String photo = String.valueOf(random.nextInt(10000, 999999));
        cloudinaryService.uploadFile(file, file.getName() + photo);
        return cloudinaryService.getPhotoUrl(file.getName() + photo);
    }
}
