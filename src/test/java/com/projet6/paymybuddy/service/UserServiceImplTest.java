package com.projet6.paymybuddy.service;

import com.projet6.paymybuddy.dao.RoleDAO;
import com.projet6.paymybuddy.dao.UserDAO;
import com.projet6.paymybuddy.dto.UserRegistrationDto;
import com.projet6.paymybuddy.model.Role;
import com.projet6.paymybuddy.model.User;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.Date;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
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
}
