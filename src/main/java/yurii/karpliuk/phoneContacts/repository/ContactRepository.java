package yurii.karpliuk.phoneContacts.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import yurii.karpliuk.phoneContacts.entity.Contact;
import yurii.karpliuk.phoneContacts.entity.User;

import java.util.List;
import java.util.Optional;
import java.util.Set;


@Repository
public interface ContactRepository extends JpaRepository<Contact,Long> {
    boolean existsContactByEmails(String email);
    boolean existsContactByPhones(String phone);

    Optional<Contact> findByName(String name);
    Set<Contact> findAllByUser(User user);
}
