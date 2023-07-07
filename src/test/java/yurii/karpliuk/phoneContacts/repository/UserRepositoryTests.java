package yurii.karpliuk.phoneContacts.repository;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import yurii.karpliuk.phoneContacts.entity.User;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class UserRepositoryTests {
    @Mock
    private UserRepository userRepository;

    @Test
    public void testFindByUsername_UserExists() {
        String username = "ira";
        User user = new User();
        user.setUsername(username);
        Optional<User> expectedUser = Optional.of(user);
        when(userRepository.findByUsername(username)).thenReturn(expectedUser);

        Optional<User> result = userRepository.findByUsername(username);

        assertTrue(result.isPresent());
        assertEquals(username, result.get().getUsername());
    }

    @Test
    public void testFindByUsername_UserDoesNotExist() {
        String username = "yulya";
        Optional<User> expectedUser = Optional.empty();
        when(userRepository.findByUsername(username)).thenReturn(expectedUser);

        Optional<User> result = userRepository.findByUsername(username);

        assertFalse(result.isPresent());
    }

}
