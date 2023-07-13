package yurii.karpliuk.phoneContacts.service.impl;

import org.apache.commons.csv.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.FileSystemResource;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import yurii.karpliuk.phoneContacts.entity.Contact;
import yurii.karpliuk.phoneContacts.entity.User;
import yurii.karpliuk.phoneContacts.exception.EmailAlreadyExistsException;
import yurii.karpliuk.phoneContacts.exception.EmailIsNotValidException;
import yurii.karpliuk.phoneContacts.exception.PhoneAlreadyExistsException;
import yurii.karpliuk.phoneContacts.exception.PhoneIsNotValidException;
import yurii.karpliuk.phoneContacts.repository.ContactRepository;
import yurii.karpliuk.phoneContacts.repository.UserRepository;
import yurii.karpliuk.phoneContacts.service.CSVService;
import yurii.karpliuk.phoneContacts.validator.EmailValidator;
import yurii.karpliuk.phoneContacts.validator.PhoneNumberValidator;


import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.*;

@Service
public class CSVServiceImpl implements CSVService {
    @Autowired
    private ContactRepository contactRepository;


    @Autowired
    private UserRepository userRepository;


    public static final String TYPE = "text/csv";
    public static final String directory = "D:\\JavaProject\\phoneContacts\\src\\main\\resources\\csv\\";


    @Override
    public boolean hasCSVFormat(MultipartFile file) {
        if (!TYPE.equals(file.getContentType())) {
            return false;
        }

        return true;
    }

    @Override
    public FileSystemResource getSavedFileResource(ByteArrayInputStream byteArrayInputStream, String filename) throws IOException {
        String filePath = directory + filename;

        Files.copy(byteArrayInputStream, Paths.get(filePath), StandardCopyOption.REPLACE_EXISTING);

        return new FileSystemResource(filePath);
    }


    @Override
    public ByteArrayInputStream contactsToCSV(List<Contact> contacts) {
        final CSVFormat format = CSVFormat.DEFAULT.withQuoteMode(QuoteMode.MINIMAL);

        try (ByteArrayOutputStream out = new ByteArrayOutputStream();
             CSVPrinter csvPrinter = new CSVPrinter(new PrintWriter(out), format)) {
            for (Contact contact : contacts) {
                List<String> data = Arrays.asList(
                        contact.getName(),
                        contact.getEmails().toString(),
                        contact.getPhones().toString()
                );

                csvPrinter.printRecord(data);
            }

            csvPrinter.flush();
            return new ByteArrayInputStream(out.toByteArray());
        } catch (IOException e) {
            throw new RuntimeException("fail to import data to CSV file: " + e.getMessage());
        }
    }

    @Override
    public ByteArrayInputStream loadContacts(String username) {
        User user = userRepository.findByUsername(username).get();
        List<Contact> contacts = new ArrayList<>(contactRepository.findAllByUser(user));
        ByteArrayInputStream in = contactsToCSV(contacts);
        return in;
    }


    @Override
    public Set<Contact> csvToContacts(InputStream is, User user) {
        try (BufferedReader fileReader = new BufferedReader(new InputStreamReader(is, "UTF-8"));
             CSVParser csvParser = new CSVParser(fileReader, CSVFormat.DEFAULT.withFirstRecordAsHeader().withIgnoreHeaderCase().withTrim())) {

            Set<Contact> contacts = new HashSet<>();

            for (CSVRecord csvRecord : csvParser) {
                Contact contact = new Contact();
                contact.setUser(user);

                contact.setName(csvRecord.get("Name"));

                Set<String> emails = parseEmails(csvRecord.get("Emails"));

                for (String email : emails) {
                    if (!EmailValidator.isValid(email) || email == null) {
                        throw new EmailIsNotValidException("Email is not valid");
                    }
                    if (emails.stream().anyMatch(e->contactRepository.existsContactByEmailsAndUserId(e,user.getId()))) {
                        throw new EmailAlreadyExistsException("Email already exists");

                    }

                }

                contact.setEmails(emails);

                Set<String> phones = parsePhones(csvRecord.get("Phones"));
                for (String phone : phones) {
                    if (!PhoneNumberValidator.isValid(phone) || phone == null) {
                        throw new PhoneIsNotValidException("Phone is not valid");
                    }
                    if (phones.stream().anyMatch(p->contactRepository.existsContactByPhonesAndUserId(p,user.getId()))) {
                        throw new PhoneAlreadyExistsException("Phone already exists");

                    }

                }
                contact.setPhones(phones);

                contacts.add(contact);
            }
            return contacts;
        } catch (IOException e) {
            throw new RuntimeException("Failed to parse CSV file: " + e.getMessage());
        } catch (EmailIsNotValidException e) {
            throw new RuntimeException(e);
        } catch (PhoneIsNotValidException e) {
            throw new RuntimeException(e);
        } catch (EmailAlreadyExistsException e) {
            throw new RuntimeException(e);
        } catch (PhoneAlreadyExistsException e) {
            throw new RuntimeException(e);
        }

    }


    @Override
    public Set<String> parseEmails(String emailData) {
        String[] emailsArray = emailData.split("/");
        return new HashSet<>(Arrays.asList(emailsArray));
    }

    @Override
    public Set<String> parsePhones(String phoneData) {
        String[] phonesArray = phoneData.split("/");
        return new HashSet<>(Arrays.asList(phonesArray));
    }

    @Override
    public void save(MultipartFile file, String username) {
        User user = userRepository.findByUsername(username).get();
        try {
            Set<Contact> contacts = csvToContacts(file.getInputStream(), user);
            contactRepository.saveAll(contacts);
        } catch (IOException e) {
            throw new RuntimeException("fail to store csv data: " + e.getMessage());
        }
    }


}
