package yurii.karpliuk.phoneContacts.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import yurii.karpliuk.phoneContacts.dto.request.ContactAddRequest;
import yurii.karpliuk.phoneContacts.dto.response.ContactResponse;
import yurii.karpliuk.phoneContacts.exception.CouldNotStoreImageException;
import yurii.karpliuk.phoneContacts.service.ContactService;

import java.util.Set;

@Slf4j
@RestController
@RequestMapping("/contact")
public class ContactController {
    @Autowired
    private ContactService contactService;


    @PostMapping("/add")
    public ResponseEntity<?> addContact(@RequestBody ContactAddRequest contactAddRequest, Authentication authentication)  {
        log.info("In addContact ContactController - contact: added");
        return contactService.addContact(contactAddRequest,authentication.getName());
    }

    @PostMapping("/addImage/{name}")
    public ResponseEntity<?> addContactImage(@PathVariable String name, Authentication authentication, @RequestParam(required = false) MultipartFile image) throws CouldNotStoreImageException {
        log.info("In addContactImage ContactController - contact image: added");
        return contactService.addContactImage(name,authentication.getName(),image);
    }



    @PutMapping("/update/{name}")
    public ResponseEntity<?> updateContact(@RequestBody ContactAddRequest contactAddRequest, @PathVariable String name) {
        log.info("In updateContact ContactController - contact: updated");
        return contactService.updateContact(contactAddRequest, name);
    }

    @PutMapping("/updateImage/{name}")
    public ResponseEntity<?> updateContactImage(@PathVariable String name, Authentication authentication, @RequestParam(required = false) MultipartFile image) throws CouldNotStoreImageException {
        log.info("In updateContactImage ContactController - contact image: updated");
        return contactService.addContactImage(name,authentication.getName(),image);
    }

    @DeleteMapping("/delete/{name}")
    public ResponseEntity<?> deleteContact(@PathVariable String name) {
        log.info("In deleteContact ContactController - contact: deleted");
        return contactService.deleteContact(name);
    }

    @GetMapping("/all")
    public ResponseEntity<Set<ContactResponse>> getAllContactsOfUser(Authentication authentication) {
        log.info("In getAllContactsOfUser ContactController - get all contacts of user");
        return contactService.getAllContacts(authentication.getName());
    }
}
