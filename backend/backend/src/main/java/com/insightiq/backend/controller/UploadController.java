package com.insightiq.backend.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.*;

/**
 * Handles file uploads and communicates with Python microservice for data analysis.
 */
@RestController
@CrossOrigin(origins = {"http://localhost:5500", "http://127.0.0.1:5500"})
public class UploadController {

    private static final String UPLOAD_DIR = "D:/InsightIQ/uploads/";


    @Autowired
    private RestTemplate restTemplate;

    /**
     * Endpoint to handle file upload and trigger analysis.
     */
    @PostMapping("/upload")
    public ResponseEntity<String> uploadFiles(@RequestParam("file1") MultipartFile file1,
                                              @RequestParam("file2") MultipartFile file2) {
        try {
            // ❗ Validate file presence
            if (file1.isEmpty() || file2.isEmpty()) {
                return ResponseEntity.badRequest().body("❌ Both files must be provided.");
            }

            // 📁 Create uploads directory if missing
            Files.createDirectories(Paths.get(UPLOAD_DIR));

            // 💾 Save both files
            saveFile(file1);
            saveFile(file2);

            // 🔁 Trigger Python analysis
            String pythonUrl = "http://localhost:5000/analyze";
            ResponseEntity<String> pythonResponse = restTemplate.getForEntity(pythonUrl, String.class);

            if (pythonResponse.getStatusCode().is2xxSuccessful()) {
                return ResponseEntity.ok(pythonResponse.getBody());
            } else {
                return ResponseEntity.status(pythonResponse.getStatusCode())
                        .body("❌ Python service responded with error.");
            }

        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("❌ Failed to save files: " + e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("❌ Error during processing: " + e.getMessage());
        }
    }

    /**
     * Saves uploaded file to the uploads directory.
     */
    private void saveFile(MultipartFile file) throws IOException {
        String originalName = file.getOriginalFilename();
        String filename = StringUtils.cleanPath(originalName != null ? originalName : "uploaded_file");

        Path path = Paths.get(UPLOAD_DIR).resolve(filename);
        Files.copy(file.getInputStream(), path, StandardCopyOption.REPLACE_EXISTING);
        System.out.println("📁 Saved: " + path.toAbsolutePath());
    }

}
