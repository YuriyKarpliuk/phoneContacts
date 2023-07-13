package yurii.karpliuk.phoneContacts.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Objects;
import java.util.Set;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class Contact extends IdHolder {
    private String name;

    @ManyToOne(fetch = FetchType.LAZY)
    private User user;

    @ElementCollection(targetClass = String.class, fetch = FetchType.EAGER)
    @CollectionTable(name = "contact_emails", joinColumns = @JoinColumn(name = "contact_id"))

    private Set<String> emails;

    @ElementCollection(targetClass = String.class, fetch = FetchType.EAGER)
    @CollectionTable(name = "contact_phones", joinColumns = @JoinColumn(name = "contact_id"))
    private Set<String> phones;

    private String imageUrl;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Contact contact = (Contact) o;
        return Objects.equals(name, contact.name) && Objects.equals(user, contact.user) && Objects.equals(emails, contact.emails) && Objects.equals(phones, contact.phones);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, user, emails, phones);
    }

    @Override
    public String toString() {
        return "Contact{" +
                "name='" + name + '\'' +
                ", user=" + user +
                ", emails=" + emails +
                ", phones=" + phones +
                '}';
    }
}
