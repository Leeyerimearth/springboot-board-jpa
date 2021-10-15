package com.example.boardbackend.service;

import com.example.boardbackend.common.error.exception.NotFoundException;
import com.example.boardbackend.domain.User;
import com.example.boardbackend.dto.UserDto;
import com.example.boardbackend.common.converter.DtoConverter;
import com.example.boardbackend.repository.UserRepository;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Slf4j
@Transactional
@SpringBootTest
class UserServiceTest {

    @Autowired
    UserService userService;
    @Autowired
    UserRepository userRepository;
    @Autowired
    DtoConverter dtoConverter;

    private UserDto createUser(){
        UserDto userDto = UserDto.builder()
                .email("test@mail.com")
                .password("1234")
                .name("test")
                .age(20)
                .hobby("코딩")
                .createdAt(LocalDateTime.now())
                .build();
        return dtoConverter.convertToUserDto(userRepository.save(dtoConverter.convertToUserEntity(userDto)));
    }

    @AfterEach
    void tearDown(){
        userRepository.deleteAll();
    }

//    ------------------------------------------------------------------------------------

    @Test
    @DisplayName("회원 정보를 저장할 수 있다")
    void saveUser_test() {
        // Given
        createUser();
        UserDto newUserDto = UserDto.builder()
                .id(2L)
                .email("test2@mail.com")
                .password("1234")
                .name("test2")
                .age(30)
                .hobby("개발")
                .createdAt(LocalDateTime.now())
                .build();

        // When
        userService.saveUser(newUserDto);

        // Then
        long count = userRepository.count();
        assertThat(count, is(2L));
        Optional<User> byId = userRepository.findById(newUserDto.getId());
        assertThat(byId.isPresent(), is(true));
    }

    @Test
    @DisplayName("모든 회원의 정보를 조회할 수 있다")
    void findUsersAll_test() {
        // Given
        createUser();

        // When
        List<UserDto> usersAll = userService.findUsersAll();

        // Then
        assertThat(usersAll, hasSize(1));
        assertThat(usersAll, iterableWithSize(1));
    }

    @Test
    @DisplayName("Email로 회원을 조회할 수 있다")
    void findUserByEmail_test() {
        // Given
        UserDto userDto = createUser();

        // When
        UserDto userByEmail = userService.findUserByEmail(userDto.getEmail());

        // Then
        assertThat(userByEmail.getEmail(), is(userDto.getEmail()));
        assertThat(userByEmail.getId(), is(userDto.getId()));
    }

    @Test
    @DisplayName("ID로 회원을 조회할 수 있다")
    void findUserById_test(){
        // Given
        UserDto userDto = createUser();

        // When
        UserDto userById = userService.findUserById(userDto.getId());

        // Then
        assertThat(userById.getEmail(), is(userDto.getEmail()));
        assertThat(userById.getId(), is(userDto.getId()));
    }

    @Test
    @DisplayName("ID로 회원을 삭제할 수 있다.")
    void deleteUserById() {
        // Given
        UserDto userDto = createUser();

        // When
        userService.deleteUserById(userDto.getId());

        // Then
        assertThrows(NotFoundException.class, () -> userService.findUserById(userDto.getId()));
    }
}