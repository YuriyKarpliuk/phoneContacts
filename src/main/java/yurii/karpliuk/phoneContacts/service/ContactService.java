package yurii.karpliuk.phoneContacts.service;

import org.springframework.http.ResponseEntity;
import org.springframework.web.multipart.MultipartFile;
import yurii.karpliuk.phoneContacts.dto.request.ContactAddRequest;
import yurii.karpliuk.phoneContacts.dto.response.ContactResponse;
import yurii.karpliuk.phoneContacts.entity.Contact;
import yurii.karpliuk.phoneContacts.exception.CouldNotStoreImageException;

import java.util.List;
import java.util.Set;

public interface ContactService {
    ResponseEntity<?> addContact(ContactAddRequest contactAddRequest, String username);

    ResponseEntity<?> addContactImage(String name,String username, MultipartFile image) throws CouldNotStoreImageException;

    ResponseEntity<?> updateContact(ContactAddRequest contactAddRequest, String name);

    ResponseEntity<?> deleteContact(String name);

    ResponseEntity<Set<ContactResponse>> getAllContacts(String username);

    ContactResponse buildContactResponse(Contact contact);

    boolean isContactValid(ContactAddRequest contactAddRequest);
}
