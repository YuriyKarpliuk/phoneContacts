package yurii.karpliuk.phoneContacts.repository;


import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import yurii.karpliuk.phoneContacts.dto.request.ContactAddRequest;
import yurii.karpliuk.phoneContacts.dto.response.MessageResponse;
import yurii.karpliuk.phoneContacts.entity.Contact;
import yurii.karpliuk.phoneContacts.entity.User;
import yurii.karpliuk.phoneContacts.service.ContactService;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ContactRepositoryTests {

    @Mock
    private ContactRepository contactRepository;

    @Mock
    private ContactService contactService;

    @Test
    public void testExistsContactByEmails_ContactExists() {
        String email = "xxx@xxx.com";
        when(contactRepository.existsContactByEmails(email)).thenReturn(true);

        boolean exists = contactRepository.existsContactByEmails(email);

        assertTrue(exists);
    }

    @Test
    public void testExistsContactByEmails_ContactDoesNotExist() {
        String email = "test@example.com";
        when(contactRepository.existsContactByEmails(email)).thenReturn(false);


        boolean exists = contactRepository.existsContactByEmails(email);

        assertFalse(exists);
    }

    @Test
    public void testExistsContactByPhones_ContactExists() {
        String phone = "+380939333333";
        when(contactRepository.existsContactByPhones(phone)).thenReturn(true);

        boolean exists = contactRepository.existsContactByPhones(phone);

        assertTrue(exists);
    }

    @Test
    public void testExistsContactByPhones_ContactDoesNotExist() {
        String phone = "123456789";
        when(contactRepository.existsContactByPhones(phone)).thenReturn(false);

        boolean exists = contactRepository.existsContactByPhones(phone);

        assertFalse(exists);
    }

    @Test
    public void testFindByName_ContactExists() {
        String name = "yura";
        Contact contact = new Contact();
        contact.setName(name);
        Optional<Contact> expectedContact = Optional.of(contact);
        when(contactRepository.findByName(name)).thenReturn(expectedContact);

        Optional<Contact> result = contactRepository.findByName(name);

        assertTrue(result.isPresent());
        assertEquals(name, result.get().getName());
    }

    @Test
    public void testFindByName_ContactDoesNotExist() {
        String name = "Bogdan";
        Optional<Contact> expectedContact = Optional.empty();
        when(contactRepository.findByName(name)).thenReturn(expectedContact);

        Optional<Contact> result = contactRepository.findByName(name);

        assertFalse(result.isPresent());
    }

    @Test
    public void testFindAllByUser_ContactsExist() {
        User user = new User();
        user.setId(1L);
        Contact contact1 = new Contact();
        contact1.setUser(user);
        Contact contact2 = new Contact();
        contact2.setUser(user);
        Set<Contact> expectedContacts = new HashSet<>();
        expectedContacts.add(contact1);
        expectedContacts.add(contact2);
        when(contactRepository.findAllByUser(user)).thenReturn(expectedContacts);

        Set<Contact> result = contactRepository.findAllByUser(user);

        assertEquals(expectedContacts.size(), result.size());
        assertTrue(result.contains(contact1));
        assertTrue(result.contains(contact2));
    }

    @Test
    public void testFindAllByUser_NoContactsExist() {
        User user = new User();
        user.setId(1L);
        Set<Contact> expectedContacts = new HashSet<>();
        when(contactRepository.findAllByUser(user)).thenReturn(expectedContacts);

        Set<Contact> result = contactRepository.findAllByUser(user);

        assertTrue(result.isEmpty());
    }



}