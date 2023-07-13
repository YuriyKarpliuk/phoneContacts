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
    boolean existsContactByEmailsAndUserId(String email, Long userId);
    boolean existsContactByPhonesAndUserId(String phone,Long userId);

    Optional<Contact> findByName(String name);
    Optional<Contact> findByNameAndUserId(String name,Long userId);
    Set<Contact> findAllByUser(User user);
}
