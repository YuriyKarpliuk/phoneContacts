package yurii.karpliuk.phoneContacts.dto.request;

import lombok.Getter;
import lombok.Setter;

import java.util.Set;
@Getter
@Setter
public class ContactAddRequest {
    private String name;
    private Set<String> phones;
    private Set<String> emails;


}
