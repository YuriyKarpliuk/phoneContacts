package yurii.karpliuk.phoneContacts.service;

import org.springframework.core.io.FileSystemResource;
import org.springframework.security.core.Authentication;
import org.springframework.web.multipart.MultipartFile;
import yurii.karpliuk.phoneContacts.entity.Contact;
import yurii.karpliuk.phoneContacts.entity.User;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Set;

public interface CSVService {
    boolean hasCSVFormat(MultipartFile file);


    FileSystemResource getSavedFileResource(ByteArrayInputStream byteArrayInputStream, String filename) throws IOException;

    ByteArrayInputStream contactsToCSV(List<Contact> contacts);


    ByteArrayInputStream loadContacts(String username);

    Set<Contact> csvToContacts(InputStream is, User user);

    Set<String> parseEmails(String emailData);

    Set<String> parsePhones(String phoneData);

    void save(MultipartFile file, String username);
}
