package yurii.karpliuk.phoneContacts.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import yurii.karpliuk.phoneContacts.controller.ContactController;
import yurii.karpliuk.phoneContacts.dto.request.ContactAddRequest;
import yurii.karpliuk.phoneContacts.entity.Contact;
import yurii.karpliuk.phoneContacts.security.jwt.JwtService;
import yurii.karpliuk.phoneContacts.service.ContactService;

import java.util.HashSet;
import java.util.Set;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(ContactController.class)
public class ContactControllerTests {
    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ObjectMapper objectMapper;
    @MockBean
    private ContactService contactService;
    @MockBean
    private JwtService jwtService;

    @Test

    public void testAddContactShouldReturn400BadRequest() throws Exception {

        ContactAddRequest contactAddRequest = new ContactAddRequest();

        String email1 = "b@a";
        String email2 = "dad@y.com";

        String phone1 = "+380679876582";
        String phone2 = "+380991234531";

        Set<String> emails = new HashSet<>();
        emails.add(email1);
        emails.add(email2);

        Set<String> phones = new HashSet<>();
        phones.add(phone1);
        phones.add(phone2);

        contactAddRequest.setName("olya");
        contactAddRequest.setEmails(emails);
        contactAddRequest.setPhones(phones);

        String jwtToken = "eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJZdXJhIiwiaWF0IjoxNjg4Njc4ODA3LCJleHAiOjE2ODg2ODAyNDd9.vd7BjaVtKb19hawCAKan7hmxkQCci2Lnz9rwsmZJBJ0";

        mockMvc.perform(post("/contact/add")
                        .header("Authorization", "Bearer " +jwtToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(contactAddRequest)))
                .andExpect(status().isBadRequest());

    }


}
