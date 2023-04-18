package com.projet6.paymybuddy.service;

import com.projet6.paymybuddy.dao.RelationDAO;
import com.projet6.paymybuddy.dao.RoleDAO;
import com.projet6.paymybuddy.dao.UserDAO;
import com.projet6.paymybuddy.dto.UserRegistrationDto;
import com.projet6.paymybuddy.model.Relation;
import com.projet6.paymybuddy.model.Role;
import com.projet6.paymybuddy.model.User;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class UserServiceImplTest {

    @InjectMocks
    private UserServiceImpl userService;

    @Mock
    private UserDAO userDao;

    @Mock
    private RoleDAO roleDao;

    @Mock
    private RelationDAO relationDao;

    @Test
    public void itShouldSaveUser() {
        // GIVEN
        UserRegistrationDto userRegistrationDto = new UserRegistrationDto("John", "Doe", "password123", "john.doe@example.com");
        Role userRole = new Role("USER");
        userRole.setId(1); // Setting the id manually
        User savedUser = new User("John", "Doe", "john.doe@example.com", "encoded_password", BigDecimal.ZERO, new Date(), Collections.singletonList(userRole));

        when(roleDao.findRoleByName("USER")).thenReturn(userRole);
        when(userDao.save(any(User.class))).thenReturn(savedUser);

        // WHEN
        User result = userService.save(userRegistrationDto);

        // THEN
        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(savedUser.getId());
        assertThat(result.getFirstName()).isEqualTo(savedUser.getFirstName());
        assertThat(result.getLastName()).isEqualTo(savedUser.getLastName());
        assertThat(result.getEmail()).isEqualTo(savedUser.getEmail());
        assertThat(result.getRoles()).isEqualTo(savedUser.getRoles());
        assertThat(result.getBalance()).isEqualByComparingTo(savedUser.getBalance());

        verify(roleDao, times(1)).findRoleByName("USER");
        verify(userDao, times(1)).save(any(User.class));
    }

    @Test
    public void itShouldListEmailRelation() {
        // GIVEN
        String emailOwner = "owner@example.com";
        User owner = new User();
        owner.setEmail(emailOwner);

        User buddy1 = new User();
        buddy1.setEmail("buddy1@example.com");

        User buddy2 = new User();
        buddy2.setEmail("buddy2@example.com");

        List<Relation> relations = Arrays.asList(
                new Relation(owner, buddy1),
                new Relation(owner, buddy2)
        );

        when(relationDao.findAllByOwner_Email(emailOwner)).thenReturn(relations);

        // WHEN
        List<Relation> result = userService.listEmailRelation(emailOwner);

        // THEN
        assertThat(result).isNotNull();
        assertThat(result.size()).isEqualTo(2);
        assertThat(result).isEqualTo(relations);

        verify(relationDao, times(1)).findAllByOwner_Email(emailOwner);
    }

    @Test
    public void itShouldDeleteBuddyWhenRelationExists() {
        // GIVEN
        Integer relationId = 1;

        when(relationDao.existsById(relationId)).thenReturn(true);

        // WHEN
        Boolean result = userService.deleteBuddy(relationId);

        // THEN
        assertThat(result).isTrue();
        verify(relationDao, times(1)).existsById(relationId);
        verify(relationDao, times(1)).deleteById(relationId);
    }

    @Test
    public void itShouldNotDeleteBuddyWhenRelationDoesNotExist() {
        // GIVEN
        Integer relationId = 1;

        when(relationDao.existsById(relationId)).thenReturn(false);

        // WHEN
        Boolean result = userService.deleteBuddy(relationId);

        // THEN
        assertThat(result).isFalse();
        verify(relationDao, times(1)).existsById(relationId);
        verify(relationDao, never()).deleteById(relationId);
    }

    @Test
    public void itShouldLoadUserByUsername() {
        // GIVEN
        String email = "john.doe@example.com";
        String password = "encoded_password";
        User user = new User("John", "Doe", email, password, BigDecimal.ZERO, new Date(), Collections.singletonList(new Role("USER")));
        when(userDao.findByEmail(email)).thenReturn(user);

        // WHEN
        UserDetails result = userService.loadUserByUsername(email);

        // THEN
        assertThat(result).isNotNull();
        assertThat(result.getUsername()).isEqualTo(email);
        assertThat(result.getPassword()).isEqualTo(password);

        verify(userDao, times(1)).findByEmail(email);
    }

    @Test
    public void itShouldThrowUsernameNotFoundExceptionWhenUserNotFound() {
        // GIVEN
        String email = "john.doe@example.com";
        when(userDao.findByEmail(email)).thenReturn(null);

        // WHEN & THEN
        assertThatThrownBy(() -> userService.loadUserByUsername(email))
                .isInstanceOf(UsernameNotFoundException.class)
                .hasMessage("Invalid username or password.");

        verify(userDao, times(1)).findByEmail(email);
    }

    // Add a buddy test method:

}
