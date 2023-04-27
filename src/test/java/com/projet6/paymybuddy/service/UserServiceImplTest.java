package com.projet6.paymybuddy.service;

import com.projet6.paymybuddy.dao.RelationDAO;
import com.projet6.paymybuddy.dao.RoleDAO;
import com.projet6.paymybuddy.dao.UserDAO;
import com.projet6.paymybuddy.dto.BuddyFormDto;
import com.projet6.paymybuddy.dto.UserRegistrationDto;
import com.projet6.paymybuddy.exception.DataNotFoundException;
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

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;
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

    // IMPORTANT: test the addBuddy method in UserServiceImpl:
    @Test
    public void itShouldAddBuddy() throws DataNotFoundException {
        // GIVEN
        String ownerEmail = "owner@example.com";
        String buddyEmail = "buddy@example.com";
        User owner = new User("Owner", "OwnerLastName", ownerEmail, "owner_password", BigDecimal.ZERO, new Date(), null);
        User buddy = new User("Buddy", "BuddyLastName", buddyEmail, "buddy_password", BigDecimal.ZERO, new Date(), null);
        Relation relation = new Relation(owner, buddy);

        when(userDao.findByEmail(ownerEmail)).thenReturn(owner);
        when(userDao.findByEmail(buddyEmail)).thenReturn(buddy);
        when(relationDao.save(any(Relation.class))).thenReturn(relation);
        when(userDao.save(any(User.class))).thenReturn(owner);

        BuddyFormDto buddyFormDto = new BuddyFormDto();
        buddyFormDto.setOwner(ownerEmail);
        buddyFormDto.setBuddy(buddyEmail);

        // WHEN
        User result = userService.addBuddy(buddyFormDto);

        // THEN
        assertThat(result).isNotNull();
        assertThat(result).isEqualTo(owner);
        assertThat(result.getRelations().get(0)).isEqualTo(relation);
        assertThat(result.getRelations().get(0).getBuddy()).isEqualTo(buddy);

        verify(userDao, times(1)).findByEmail(ownerEmail);
        verify(userDao, times(1)).findByEmail(buddyEmail);
        verify(relationDao, times(1)).save(any(Relation.class));
        verify(userDao, times(1)).save(any(User.class));
    }

    // Unit tests for the private helper methods:
    @Test
    public void itShouldCreateRelation() {
        // Arrange
        User owner = new User(1, "John", "Doe", "johndoe@example.com", "password", BigDecimal.ZERO, new Date());
        User buddy = new User(2, "Jane", "Doe", "janedoe@example.com", "password", BigDecimal.ZERO, new Date());
        Relation expectedRelation = new Relation(owner, buddy);

        when(relationDao.save(any(Relation.class))).thenReturn(expectedRelation);

        // Act
        Relation createdRelation = userService.createRelation(owner, buddy);

        // Assert
        assertThat(createdRelation).isEqualTo(expectedRelation);
        verify(relationDao).save(any(Relation.class));
    }

    @Test
    public void itShouldFindUserByEmail() {
        // Arrange
        String email = "johndoe@example.com";
        User expectedUser = new User(1, "John", "Doe", email, "password", BigDecimal.ZERO, new Date());

        when(userDao.findByEmail(anyString())).thenReturn(expectedUser);

        // Act
        User foundUser = userService.findUserByEmail(email);

        // Assert
        assertThat(foundUser).isEqualTo(expectedUser);
        verify(userDao).findByEmail(email);
    }

    @Test
    void findBuddyByEmail_shouldReturnUserWhenUserExists() {
        // Arrange
        String email = "buddy@example.com";
        User expectedUser = new User();
        expectedUser.setEmail(email);
        when(userDao.findByEmail(email)).thenReturn(expectedUser);

        // Act
        User actualUser = userService.findBuddyByEmail(email);

        // Assert
        assertThat(actualUser).isEqualTo(expectedUser);
        verify(userDao, times(1)).findByEmail(email);
    }

    @Test
    void findBuddyByEmail_shouldThrowDataNotFoundExceptionWhenUserDoesNotExist() {
        // Arrange
        String email = "nonexistent@example.com";
        when(userDao.findByEmail(email)).thenReturn(null);

        // Act and Assert
        assertThatExceptionOfType(DataNotFoundException.class)
                .isThrownBy(() -> userService.findBuddyByEmail(email))
                .withMessage("buddy does not exist");

        verify(userDao, times(1)).findByEmail(email);
    }

}
