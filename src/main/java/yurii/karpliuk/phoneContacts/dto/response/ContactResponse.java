package yurii.karpliuk.phoneContacts.dto.response;

import lombok.*;

import java.util.Set;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ContactResponse {
    private String name;
    private Set<String> emails;
    private Set<String> phones;
}

