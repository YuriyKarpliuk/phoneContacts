package yurii.karpliuk.phoneContacts.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import yurii.karpliuk.phoneContacts.dto.response.MessageResponse;
import yurii.karpliuk.phoneContacts.service.CSVService;
import org.springframework.http.HttpHeaders;
import org.springframework.core.io.Resource;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;


@Slf4j
@RestController
@RequestMapping("/csv/contact")
public class CSVContactController {
    @Autowired
    private CSVService csvService;

    @PostMapping("/upload")
    public ResponseEntity<MessageResponse> uploadFile(@RequestParam("file") MultipartFile file, Authentication authentication) {
        String message = "";

        if (csvService.hasCSVFormat(file)) {
            try {
                csvService.save(file, authentication.getName());
                message = "Uploaded the file successfully: " + file.getOriginalFilename();
                return ResponseEntity.status(HttpStatus.OK).body(new MessageResponse(message));
            } catch (Exception e) {
                message = "Could not upload the file: " + file.getOriginalFilename() + "!";
                return ResponseEntity.status(HttpStatus.EXPECTATION_FAILED).body(new MessageResponse(message));
            }
        }

        message = "Please upload a csv file!";
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new MessageResponse(message));
    }

    @GetMapping("/download")
    public ResponseEntity<Resource> getFile(Authentication authentication) throws IOException {
        String filename = "myContacts_" + authentication.getName() + ".csv";
        ByteArrayInputStream inputStream = csvService.loadContacts(authentication.getName());


        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=" + filename)
                .contentType(MediaType.parseMediaType("application/csv"))
                .body(csvService.getSavedFileResource(inputStream,filename));

    }

}
