package com.projet6.paymybuddy.service;

import com.projet6.paymybuddy.dao.RelationDAO;
import com.projet6.paymybuddy.dao.RoleDAO;
import com.projet6.paymybuddy.dao.UserDAO;
import com.projet6.paymybuddy.dto.BuddyFormDto;
import com.projet6.paymybuddy.dto.UserRegistrationDto;
import com.projet6.paymybuddy.exception.DataAlreadyExistException;
import com.projet6.paymybuddy.exception.DataNotFoundException;
import com.projet6.paymybuddy.model.Relation;
import com.projet6.paymybuddy.model.Role;
import com.projet6.paymybuddy.model.User;
import lombok.AllArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserDAO userDao;

    private final RelationDAO relationDao;

    private final RoleDAO roleDao;

    static BCryptPasswordEncoder encoder = new BCryptPasswordEncoder(12);


    @Override
    public User save(UserRegistrationDto userRegistrationDto) {
        Role role = roleDao.findRoleByName("USER");
        User user = new User(userRegistrationDto.getFirstName(),
                userRegistrationDto.getLastName(),
                userRegistrationDto.getEmail(),
                encoder.encode(userRegistrationDto.getPassword()),
                BigDecimal.ZERO,
                new Date(),
                Collections.singletonList(role));
        return userDao.save(user);
    }

    @Override
    public List<Relation> listEmailRelation(String emailOwner) {
        return relationDao.findAllByOwner_Email(emailOwner);
    }

    @Override
    public Boolean deleteBuddy(Integer id) {
        if (relationDao.existsById(id)) {
            relationDao.deleteById(id);
            return true;
        }
        return false;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = userDao.findByEmail(username);
        if (user == null) {
            throw new UsernameNotFoundException("Invalid username or password.");
        }
        return new org.springframework.security.core.userdetails.User(user.getEmail(), 
                user.getPassword(),
                mapRolesToAuthorities(user.getRoles()));
    }

    /* package-private */ Collection<? extends GrantedAuthority> mapRolesToAuthorities(Collection<Role> roles) {
        return roles.stream()
                .map(role -> new SimpleGrantedAuthority(role.getName()))
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public User addBuddy(BuddyFormDto buddyFormDto) throws DataNotFoundException{
        User owner = findUserByEmail(buddyFormDto.getOwner());
        try {
            checkIfRelationExists(owner, buddyFormDto.getBuddy());
        } catch (DataAlreadyExistException e) {
            throw new RuntimeException(e);
        }
        User buddy = findBuddyByEmail(buddyFormDto.getBuddy());
        Relation relation = createRelation(owner, buddy);
        addRelationToOwner(owner, relation);
        return saveOwner(owner);
    }

    /* package-private */ Relation createRelation(User owner, User buddy) {
        Relation relation = new Relation(owner, buddy);
        return relationDao.save(relation); // Save the relation here
    }

    /* package-private */ User findUserByEmail(String email) {
        return userDao.findByEmail(email);
    }

    /* package-private */ void checkIfRelationExists(User owner, String buddyEmail) throws DataAlreadyExistException {
        if (owner.getRelations() != null && !owner.getRelations().isEmpty() &&
                owner.getRelations().stream()
                        .anyMatch(relation -> relation.getBuddy().getEmail().equals(buddyEmail))) {
            throw new DataAlreadyExistException("buddy already exists");
        }
    }

    /* package-private */ User findBuddyByEmail(String email) {
        User buddy = userDao.findByEmail(email);
        if (buddy == null) {
            throw new DataNotFoundException("buddy does not exist");
        }
        return buddy;
    }

    /* package-private */ void addRelationToOwner(User owner, Relation relation) {
        if (owner.getRelations() == null) {
            owner.setRelations(new ArrayList<>());
        }
        owner.getRelations().add(relation);
    }

    private User saveOwner(User owner) {
        return userDao.save(owner);
    }

}
