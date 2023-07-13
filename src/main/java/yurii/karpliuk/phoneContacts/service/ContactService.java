package yurii.karpliuk.phoneContacts.service;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.multipart.MultipartFile;
import yurii.karpliuk.phoneContacts.dto.request.ContactAddRequest;
import yurii.karpliuk.phoneContacts.dto.response.ContactResponse;
import yurii.karpliuk.phoneContacts.entity.Contact;
import yurii.karpliuk.phoneContacts.exception.CouldNotStoreImageException;

import java.util.Set;

public interface ContactService {


    boolean isContactValid(ContactAddRequest contactAddRequest, Long userId);

    boolean contactExistByEmails(Set<String> emails, Long userId);

    boolean contactExistByPhones(Set<String> phones, Long userId);

    Long getUserIdFromAuthentication(Authentication authentication);


    ResponseEntity<?> addContact(ContactAddRequest contactAddRequest, Authentication authentication);

    ResponseEntity<?> addContactImage(String name, String username, MultipartFile image) throws CouldNotStoreImageException;


    ResponseEntity<?> updateContact(ContactAddRequest contactAddRequest, String username, String name);


    ResponseEntity<?> deleteContact(String username, String name);

    ResponseEntity<Set<ContactResponse>> getAllContacts(String username);

    ContactResponse buildContactResponse(Contact contact);

}
