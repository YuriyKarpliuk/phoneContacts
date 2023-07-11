package yurii.karpliuk.phoneContacts.service.impl;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import yurii.karpliuk.phoneContacts.dto.request.ContactAddRequest;
import yurii.karpliuk.phoneContacts.dto.response.ContactResponse;
import yurii.karpliuk.phoneContacts.dto.response.MessageResponse;
import yurii.karpliuk.phoneContacts.entity.Contact;
import yurii.karpliuk.phoneContacts.entity.User;
import yurii.karpliuk.phoneContacts.exception.CouldNotStoreImageException;
import yurii.karpliuk.phoneContacts.repository.ContactRepository;
import yurii.karpliuk.phoneContacts.repository.UserRepository;
import yurii.karpliuk.phoneContacts.service.ContactService;
import yurii.karpliuk.phoneContacts.validator.EmailValidator;
import yurii.karpliuk.phoneContacts.validator.PhoneNumberValidator;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.*;

@Slf4j

@Service
public class ContactServiceImpl implements ContactService {
    private final Path root = Paths.get("D:\\JavaProject\\phoneContacts\\src\\main\\resources\\images");

    @Autowired
    private ContactRepository contactRepository;
    @Autowired
    private UserRepository userRepository;

    public boolean isContactValid(ContactAddRequest contactAddRequest) {
        boolean allEmailsValid = false;
        boolean allPhonesValid = false;

        Set<String> emails = contactAddRequest.getEmails();
        Set<String> phoneNumbers = contactAddRequest.getPhones();

        for (String email : emails) {
            if (EmailValidator.isValid(email) && emails != null && !emails.stream().anyMatch(contactRepository::existsContactByEmails)) {
                allEmailsValid = true;
                break;
            }
        }
        for (String phone : phoneNumbers) {
            if (PhoneNumberValidator.isValid(phone) &&phoneNumbers != null && !phoneNumbers.stream().anyMatch(contactRepository::existsContactByPhones) ) {
                allPhonesValid = true;
                break;
            }
        }


        return allEmailsValid&&allPhonesValid;
    }

    @Override
    public ResponseEntity<?> addContact(ContactAddRequest contactAddRequest, String username) {
        if (isContactValid(contactAddRequest)) {
            Contact contact = new Contact();
            contact.setName(contactAddRequest.getName());
            contact.setEmails(contactAddRequest.getEmails());
            contact.setPhones(contactAddRequest.getPhones());
            contact.setUser(userRepository.findByUsername(username).get());
            contactRepository.save(contact);
            log.info("In addContact ContactService - added contact: '{}'", contact);
            return ResponseEntity.ok(new MessageResponse("Contact added successfully!"));
        } else {
            log.info("In addContact ContactService - contact: '{}' already exists", contactAddRequest);
            return ResponseEntity
                    .badRequest()
                    .body(new MessageResponse("Error: Contact is already created or wrong email or phone number!"));
        }
    }

    @Override
    public ResponseEntity<?> addContactImage(String name, String username, MultipartFile image) throws CouldNotStoreImageException {
        boolean isContactCreated = contactRepository.findAll().stream().anyMatch(c -> c.getName().equalsIgnoreCase(name));
        if (isContactCreated) {
            Contact contact = contactRepository.findByName(name).get();
            try {
                Path path = this.root.resolve(Objects.requireNonNull(image.getOriginalFilename()));
                Files.copy(image.getInputStream(), path, StandardCopyOption.REPLACE_EXISTING);
                contact.setImageUrl(path.toAbsolutePath().toString());

            } catch (IOException e) {
                throw new CouldNotStoreImageException("Could not store the image");
            }
            contactRepository.save(contact);
            log.info("In addContactImage ContactService - added contact image: '{}'", contact);
            return ResponseEntity.ok(new MessageResponse("Contact image added successfully!"));
        } else {
            log.info("In addContactImage ContactService - contact image isn't correct");
            return ResponseEntity
                    .badRequest()
                    .body(new MessageResponse("Error: Contact image isn't correct"));
        }
    }


    @Override
    public ResponseEntity<?> updateContact(ContactAddRequest contactAddRequest, String name) {
        boolean isContactCreated = contactRepository.findAll().stream().anyMatch(c -> c.getName().equalsIgnoreCase(name));
        if (isContactCreated && isContactValid(contactAddRequest)) {
            Contact updatedContact = contactRepository.findByName(name).get();
            updatedContact.setName(contactAddRequest.getName());
            updatedContact.setEmails(contactAddRequest.getEmails());
            updatedContact.setPhones(contactAddRequest.getPhones());
            log.info("Update contact in ContactService with name: {}, emails{}, " +
                    "phones: {} ", updatedContact.getName(), updatedContact.getEmails(), updatedContact.getPhones());
            contactRepository.save(updatedContact);
            return ResponseEntity.ok(new MessageResponse("Contact updated successfully!"));
        } else {
            log.info("In updateContact ContactService - contact isn't exist");
            return ResponseEntity
                    .badRequest()
                    .body(new MessageResponse("Error: Contact  isn't exist"));
        }
    }

    @Override
    public ResponseEntity<?> deleteContact(String name) {
        Contact contact = contactRepository.findByName(name).get();
        log.info("In deleteContact ContactService - deleted contact with name: '{}'", name);
        contactRepository.delete(contact);
        return ResponseEntity.ok(new MessageResponse("Contact deleted successfully!"));
    }

    @Override
    public ResponseEntity<Set<ContactResponse>> getAllContacts(String username) {
        Set<ContactResponse> contactResponses = new HashSet<>();
        User user = userRepository.findByUsername(username).get();
        log.info("In getAllContacts ContactService - get all contacts of user with username: '{}'", username);
        Set<Contact> contacts = contactRepository.findAllByUser(user);
        for (Contact contact : contacts) {
            contactResponses.add(buildContactResponse(contact));
        }
        return ResponseEntity.ok(contactResponses);
    }


    @Override
    public ContactResponse buildContactResponse(Contact contact) {
        ContactResponse response = new ContactResponse();
        response.setName(contact.getName());
        response.setEmails(contact.getEmails());
        response.setPhones(contact.getPhones());
        return response;
    }


}
