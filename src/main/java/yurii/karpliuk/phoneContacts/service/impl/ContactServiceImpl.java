package yurii.karpliuk.phoneContacts.service.impl;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
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

    @Override
    public boolean isContactValid(ContactAddRequest contactAddRequest, Long userId) {
        boolean allEmailsValid = false;
        boolean allPhonesValid = false;

        Set<String> emails = contactAddRequest.getEmails();
        Set<String> phoneNumbers = contactAddRequest.getPhones();

        for (String email : emails) {
            if (EmailValidator.isValid(email) && emails != null) {
                allEmailsValid = true;
                break;
            }
        }
        for (String phone : phoneNumbers) {
            if (PhoneNumberValidator.isValid(phone) && phoneNumbers != null) {
                allPhonesValid = true;
                break;
            }
        }


        return allEmailsValid && allPhonesValid;
    }

    @Override
    public boolean contactExistByEmails(Set<String> emails, Long userId) {
        return emails.stream().anyMatch(email -> contactRepository.existsContactByEmailsAndUserId(email, userId));
    }

    @Override
    public boolean contactExistByPhones(Set<String> phones, Long userId) {
        return phones.stream().anyMatch(email -> contactRepository.existsContactByPhonesAndUserId(email, userId));
    }

    @Override
    public Long getUserIdFromAuthentication(Authentication authentication) {
        if (authentication.getPrincipal() instanceof User) {
            User userDetails = (User) authentication.getPrincipal();
            return userDetails.getId();
        }
        return null;
    }

    @Override
    public ResponseEntity<?> addContact(ContactAddRequest contactAddRequest, Authentication authentication) {
        Long userId = getUserIdFromAuthentication(authentication);
        if (isContactValid(contactAddRequest, userId)) {
            if (contactExistByEmails(contactAddRequest.getEmails(), userId)) {
                return ResponseEntity
                        .badRequest()
                        .body(new MessageResponse("Error: Contact is already created with such emails"));
            }
            if (contactExistByPhones(contactAddRequest.getPhones(), userId)) {
                return ResponseEntity
                        .badRequest()
                        .body(new MessageResponse("Error: Contact is already created with such phones"));
            } else {
                Contact contact = new Contact();
                contact.setName(contactAddRequest.getName());
                contact.setEmails(contactAddRequest.getEmails());
                contact.setPhones(contactAddRequest.getPhones());
                contact.setUser(userRepository.findById(userId).get());
                contactRepository.save(contact);
                log.info("In addContact ContactService - added contact: '{}'", contact);
                return ResponseEntity.ok(new MessageResponse("Contact added successfully!"));
            }
        } else {
            log.info("In addContact ContactService - contact: '{}' wrong email or phone number", contactAddRequest);
            return ResponseEntity
                    .badRequest()
                    .body(new MessageResponse("Error: Wrong email or phone number!"));
        }
    }

    @Override
    public ResponseEntity<?> addContactImage(String name, String username, MultipartFile image) throws
            CouldNotStoreImageException {
        Long userId = userRepository.findByUsername(username).get().getId();
        boolean isContactCreated = contactRepository.findAll()
                .stream()
                .anyMatch(c -> c.getName().equalsIgnoreCase(name) && c.getUser().getId().equals(userId));
        if (isContactCreated) {
            Contact contact = contactRepository.findByNameAndUserId(name, userId).get();
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
    public ResponseEntity<?> updateContact(ContactAddRequest contactAddRequest, String username, String name) {
        Long userId = userRepository.findByUsername(username).get().getId();
        boolean isContactCreated = contactRepository.findAll()
                .stream()
                .anyMatch(c -> c.getName().equalsIgnoreCase(name) && c.getUser().getId().equals(userId));
        if (isContactCreated) {
            Contact updatedContact = contactRepository.findByNameAndUserId(name, userId).get();
            if (!isContactValid(contactAddRequest, userId)) {
                log.info("In addContact ContactService - contact: '{}' wrong email or phone number", contactAddRequest);
                return ResponseEntity
                        .badRequest()
                        .body(new MessageResponse("Error: Wrong email or phone number!"));
            } else {
                updatedContact.setName(contactAddRequest.getName());
                updatedContact.setEmails(contactAddRequest.getEmails());
                updatedContact.setPhones(contactAddRequest.getPhones());
                log.info("Update contact in ContactService with name: {}, emails{}, " +
                        "phones: {} ", updatedContact.getName(), updatedContact.getEmails(), updatedContact.getPhones());
                contactRepository.save(updatedContact);
                return ResponseEntity.ok(new MessageResponse("Contact updated successfully!"));
            }
        } else {
            log.info("In updateContact ContactService - contact isn't exist");
            return ResponseEntity
                    .badRequest()
                    .body(new MessageResponse("Error: Contact  isn't exist"));
        }
    }

    @Override
    public ResponseEntity<?> deleteContact(String username,String name) {
        Long userId = userRepository.findByUsername(username).get().getId();
        boolean isContactCreated = contactRepository.findAll()
                .stream()
                .anyMatch(c -> c.getName().equalsIgnoreCase(name) && c.getUser().getId().equals(userId));
        if (isContactCreated) {
            Contact contact = contactRepository.findByNameAndUserId(name,userId).get();
            log.info("In deleteContact ContactService - deleted contact with name: '{}'", name);
            contactRepository.delete(contact);
            return ResponseEntity.ok(new MessageResponse("Contact deleted successfully!"));
        }else {
            log.info("In deleteContact ContactService - contact isn't exist");
            return ResponseEntity
                    .badRequest()
                    .body(new MessageResponse("Error: Contact  isn't exist"));
        }
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
