package yurii.karpliuk.phoneContacts.service.impl;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import yurii.karpliuk.phoneContacts.dto.request.ContactAddRequest;
import yurii.karpliuk.phoneContacts.dto.response.ContactResponse;
import yurii.karpliuk.phoneContacts.dto.response.MessageResponse;
import yurii.karpliuk.phoneContacts.entity.Contact;
import yurii.karpliuk.phoneContacts.entity.User;
import yurii.karpliuk.phoneContacts.repository.ContactRepository;
import yurii.karpliuk.phoneContacts.repository.UserRepository;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)

public class ContactServiceImplTests {
    @Mock
    private ContactRepository contactRepository;

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private ContactServiceImpl contactService;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void isContactValid_ValidContact_ReturnsTrue() {
        ContactAddRequest contactAddRequest = new ContactAddRequest();
        contactAddRequest.setEmails(new HashSet<>(Arrays.asList("b@b.com", "xxx@gmail.com")));
        contactAddRequest.setPhones(new HashSet<>(Arrays.asList("+380939333333", "+380939333334")));

        boolean result = contactService.isContactValid(contactAddRequest);

        assertTrue(result);
    }

    @Test
    void isContactValid_InvalidEmail_ReturnsFalse() {
        ContactAddRequest contactAddRequest = new ContactAddRequest();
        contactAddRequest.setEmails( new HashSet<>(Arrays.asList("adsa")));
        contactAddRequest.setPhones(new HashSet<>(Arrays.asList("+380939333333")));

        boolean result = contactService.isContactValid(contactAddRequest);

        assertFalse(result);
    }

    @Test
    void addContact_ValidContact_ReturnsSuccessResponse() {
        ContactAddRequest contactAddRequest = new ContactAddRequest();
        contactAddRequest.setName("Mykola");
        contactAddRequest.setEmails(new HashSet<>(Arrays.asList("mykola@gmail.com")));
        contactAddRequest.setPhones(new HashSet<>(Arrays.asList("+380123456789")));

        String username = "mykola1234";

        User user = new User();
        user.setUsername(username);
        when(userRepository.findByUsername(username)).thenReturn(Optional.of(user));

        when(contactRepository.existsContactByEmails(any())).thenReturn(false);
        when(contactRepository.save(any())).thenReturn(new Contact());

        ResponseEntity<?> response = contactService.addContact(contactAddRequest, username,);

        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("Contact added successfully!", ((MessageResponse) response.getBody()).getMessage());

        Mockito.verify(contactRepository, times(1)).save(any());
    }

    @Test
    void deleteContact_ExistingContact_ReturnsSuccessResponse() {
        String name = "ira";

        Contact contact = new Contact();
        contact.setName(name);
        when(contactRepository.findByName(name)).thenReturn(Optional.of(contact));

        ResponseEntity<?> response = contactService.deleteContact(name);

        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("Contact deleted successfully!", ((MessageResponse) response.getBody()).getMessage());

        Mockito.verify(contactRepository, times(1)).delete(contact);
    }

    @Test
    void getAllContacts_ReturnsAllContactsForUser() {
        String username = "Yura";

        User user = new User();
        user.setUsername(username);
        when(userRepository.findByUsername(username)).thenReturn(Optional.of(user));

        Set<Contact> contacts = new HashSet<>();
        Contact contact1 = new Contact();
        contact1.setName("Nazar");
        Contact contact2 = new Contact();
        contact2.setName("Hrystyna");
        contacts.add(contact1);
        contacts.add(contact2);
        when(contactRepository.findAllByUser(user)).thenReturn(contacts);

        ResponseEntity<Set<ContactResponse>> response = contactService.getAllContacts(username);

        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(2, response.getBody().size());

       Mockito.verify(userRepository, times(1)).findByUsername(username);
        Mockito.verify(contactRepository, times(1)).findAllByUser(user);
    }

}
