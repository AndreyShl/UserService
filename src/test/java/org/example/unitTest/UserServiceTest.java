package org.example.unitTest;

import org.example.DTO.UserDTO;
import org.example.Mapper.UserMapper;
import org.example.exception.UserNotFoundException;
import org.example.model.entity.User;
import org.example.model.repository.UsersRepository;
import org.example.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentMatchers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;


import java.util.List;
import java.util.Optional;

import static org.hibernate.validator.internal.util.Contracts.assertNotNull;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class UserServiceTest {
    @Mock
    private UsersRepository usersRepository;

    @Mock
    private UserMapper mapper;

    @InjectMocks
    private UserService userService;

    private User user;
    private UserDTO userDTO;

    @BeforeEach
    void setUp() {
        user = new User();
        user.setId(1);
        user.setName("Andrei");
        user.setSurname("Andreev");
        user.setActive(true);

        userDTO = new UserDTO();
        userDTO.setId(1);
        userDTO.setName("Andrei");
        userDTO.setSurname("Andreev");
        userDTO.setActive(true);
    }


    @Test
    void createUser_success() {
        when(mapper.toEntity(userDTO)).thenReturn(user);
        when(usersRepository.save(user)).thenReturn(user);
        when(mapper.toDTO(user)).thenReturn(userDTO);

        UserDTO result = userService.createUser(userDTO);

        assertEquals(userDTO.getName(), result.getName());
        assertEquals(userDTO.getSurname(), result.getSurname());
        verify(usersRepository, times(1)).save(user);
    }


    @Test
    void getUserById_success() {
        when(usersRepository.findById(1)).thenReturn(Optional.of(user));
        when(mapper.toDTO(user)).thenReturn(userDTO);

        UserDTO result = userService.getUserById(1);

        assertNotNull(result);
        assertEquals("Andrei", result.getName());
    }

    @Test
    void getUserById_notFound() {
        when(usersRepository.findById(1)).thenReturn(Optional.empty());

        assertThrows(UserNotFoundException.class, () -> userService.getUserById(1));
    }

    @Test
    void updateUserByID_success() {
        UserDTO updatedDTO = new UserDTO();
        updatedDTO.setName("NewName");
        updatedDTO.setSurname("NewSurname");
        updatedDTO.setActive(false);

        when(usersRepository.findById(1)).thenReturn(Optional.of(user));
        when(mapper.toDTO(user)).thenReturn(updatedDTO);

        UserDTO result = userService.updateUserByID(1, updatedDTO);

        assertEquals("NewName", result.getName());
        assertEquals("NewSurname", result.getSurname());
        assertFalse(result.isActive());
    }


    @Test
    void activateUser_success() {
        user.setActive(false);
        when(usersRepository.findById(1)).thenReturn(Optional.of(user));

        userService.activateUser(1);

        assertTrue(user.isActive());
    }

    @Test
    void deactivateUser_success() {
        user.setActive(true);
        when(usersRepository.findById(1)).thenReturn(Optional.of(user));

        userService.deactivateUser(1);

        assertFalse(user.isActive());
    }


    @Test
    void getUsers_success() {
        Page<User> page = new PageImpl<>(List.of(user));
        when(usersRepository.findAll(ArgumentMatchers.<Specification<User>>any(), any(Pageable.class))).thenReturn(page);
        when(mapper.toDTO(user)).thenReturn(userDTO);

        Page<UserDTO> result = userService.getUsers("Andrei", "Andreev", Pageable.unpaged());

        assertEquals(1, result.getTotalElements());
        assertEquals("Andrei", result.getContent().get(0).getName());
    }
}
