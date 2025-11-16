package org.example.service;


import lombok.RequiredArgsConstructor;
import org.example.dto.Userdto;
import org.example.mapper.UserMapper;
import org.example.exception.UserNotFoundException;
import org.example.model.entity.User;
import org.example.model.repository.UsersRepository;

import org.example.specification.UserSpecification;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


@Service
@RequiredArgsConstructor
public class UserService {
    private final UsersRepository usersRepository;
    private final UserMapper mapper;


    @Transactional
    @Caching(put = {
            @CachePut(value = "users", key = "#result.id"),

    }, evict = {
            @CacheEvict(value = "usersWithCards", allEntries = true)
    })
    public Userdto createUser(Userdto userDTO) {
        User user = mapper.toEntity(userDTO);
        usersRepository.save(user);
        return mapper.toDTO(user);
    }

    @Cacheable(value = "users", key = "#id")
    public Userdto getUserById(Integer id) {
        User user = usersRepository.findById(id)
                .orElseThrow(() -> new UserNotFoundException(id));
        return mapper.toDTO(user);
    }

    public Page<Userdto> getAllUsers(Pageable pageable, Specification<User> spec) {
        return usersRepository.findAll(spec, pageable).map(mapper::toDTO);
    }

    @Transactional
    @Caching(
            put = { @CachePut(value = "users", key = "#id") },
            evict = { @CacheEvict(value = "usersWithCards", allEntries = true) }
    )
    public Userdto updateUserByID(Integer id, Userdto updatedDTO) {
        User user = usersRepository.findById(id)
                .orElseThrow(() ->  new UserNotFoundException(id));

        mapper.updateUserFromDto(updatedDTO, user);
        User updatedUser = usersRepository.save(user);

        return mapper.toDTO(updatedUser);
    }
    public Page<Userdto> getUsers(String firstName, String lastName, Pageable pageable) {
        Specification<User> spec = Specification
                .where(UserSpecification.firstNameContains(firstName))
                .and(UserSpecification.lastNameContains(lastName));

        Page<User> usersPage = usersRepository.findAll(spec, pageable);


        return usersPage.map(mapper::toDTO);
    }



    @Transactional
    @Caching(
            put = { @CachePut(value = "users", key = "#id") },
            evict = { @CacheEvict(value = "usersWithCards", allEntries = true) }
    )
    public void activateUser(Integer id) {
        User user = usersRepository.findById(id)
                .orElseThrow(() -> new UserNotFoundException(id));
        user.setActive(true);
        usersRepository.save(user);
    }

    @Transactional
    @Caching(
            put = { @CachePut(value = "users", key = "#id") },
            evict = { @CacheEvict(value = "usersWithCards", allEntries = true) }
    )
    public void deactivateUser(Integer id) {
        User user = usersRepository.findById(id)
                .orElseThrow(() -> new UserNotFoundException(id));
        user.setActive(false);
        usersRepository.save(user);
    }
}
