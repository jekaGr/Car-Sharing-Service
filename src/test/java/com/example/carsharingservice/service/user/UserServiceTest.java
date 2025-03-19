package com.example.carsharingservice.service.user;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.example.carsharingservice.dto.role.RoleNameDto;
import com.example.carsharingservice.dto.user.UserRegistrationRequestDto;
import com.example.carsharingservice.dto.user.UserResponseDto;
import com.example.carsharingservice.exception.EntityNotFoundException;
import com.example.carsharingservice.mapper.UserMapper;
import com.example.carsharingservice.model.Role;
import com.example.carsharingservice.model.User;
import com.example.carsharingservice.repository.RoleRepository;
import com.example.carsharingservice.repository.UserRepository;
import com.example.carsharingservice.util.user.TestUserUtil;
import java.util.Collections;
import java.util.Optional;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {
    private PasswordEncoder passwordEncoder = mock(PasswordEncoder.class);
    @Mock
    private UserRepository userRepository;
    @Mock
    private RoleRepository roleRepository;
    @Mock
    private UserMapper userMapper;
    @InjectMocks
    private UserServiceImpl userService;

    @Mock
    private Authentication authentication;

    @Mock
    private SecurityContext securityContext;

    private MockedStatic<SecurityContextHolder> securityContextHolderMockedStatic;

    @BeforeEach
    void setUp() {
        securityContextHolderMockedStatic = Mockito.mockStatic(SecurityContextHolder.class);
        securityContextHolderMockedStatic.when(SecurityContextHolder::getContext)
                .thenReturn(securityContext);
    }

    @AfterEach
    void tearDown() {
        securityContextHolderMockedStatic.close();
        SecurityContextHolder.clearContext();
    }

    @Test
    void register_should_return_UserResponseDto() {
        User user = TestUserUtil.getUser();
        UserRegistrationRequestDto userRegistration =
                TestUserUtil.getUserRegistrationRequestDto();
        when(userMapper.toUser(userRegistration)).thenReturn(user);
        when(userRepository.existsByEmail("email@email.com")).thenReturn(Boolean.FALSE);
        when(userRepository.save(any())).thenReturn(user);
        when(userMapper.toUserResponse(user)).thenReturn(TestUserUtil.getUserResponseDto());

        UserResponseDto actual = userService.register(userRegistration);
        assertNotNull(actual);
        assertEquals(user.getEmail(), actual.getEmail());
    }

    @Test
    void updateRole_should_return_UserResponseDto_when_user_and_role_exist() {
        // Given
        Long userId = 1L;
        RoleNameDto roleNameDto = new RoleNameDto();
        roleNameDto.setRoleName(Role.RoleName.MANAGER);

        User user = new User();
        user.setId(userId);
        Role role = new Role();
        role.setName(Role.RoleName.MANAGER);
        user.setRoles(Collections.singleton(role));

        User updatedUser = new User();
        updatedUser.setId(userId);
        updatedUser.setRoles(Collections.singleton(role));

        UserResponseDto expectedResponseDto = new UserResponseDto();
        expectedResponseDto.setId(userId);

        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(roleRepository.findByName(roleNameDto.getRoleName())).thenReturn(Optional.of(role));
        when(userRepository.save(user)).thenReturn(updatedUser);
        when(userMapper.toUserResponse(updatedUser)).thenReturn(expectedResponseDto);

        // When
        UserResponseDto actualResponseDto = userService.updateRole(userId, roleNameDto);

        // Then
        assertNotNull(actualResponseDto);
        assertEquals(expectedResponseDto.getId(), actualResponseDto.getId());
        verify(userRepository).save(user);
    }

    @Test
    void getMe_should_return_user_when_principal_is_user() {
        // Given
        User user = new User();
        when(securityContext.getAuthentication()).thenReturn(authentication);
        when(authentication.getPrincipal()).thenReturn(user);

        // When
        User actualUser = userService.getMe();

        // Then
        assertEquals(user, actualUser);

    }

    @Test
    void updateMe_should_return_UserResponseDto_when_principal_is_user() {
        // Given
        UserRegistrationRequestDto requestDto = new UserRegistrationRequestDto();
        requestDto.setEmail("new@email.com");
        requestDto.setFirstName("NewFirstName");
        requestDto.setLastName("NewLastName");
        requestDto.setPassword("NewPassword");

        UserResponseDto expectedResponseDto = new UserResponseDto();
        expectedResponseDto.setEmail(requestDto.getEmail());
        expectedResponseDto.setFirstName(requestDto.getFirstName());
        expectedResponseDto.setLastName(requestDto.getLastName());
        User user = new User();
        user.setEmail("old@email.com");
        user.setFirstName("OldFirstName");
        user.setLastName("OldLastName");
        user.setPassword("OldPassword");

        when(securityContext.getAuthentication()).thenReturn(authentication);
        when(authentication.getPrincipal()).thenReturn(user);
        when(passwordEncoder.encode(requestDto.getPassword())).thenReturn("EncodedNewPassword");
        when(userMapper.toUserResponse(user)).thenReturn(expectedResponseDto);

        // When
        UserResponseDto actualResponseDto = userService.updateMe(requestDto);

        // Then
        assertEquals(expectedResponseDto, actualResponseDto);
        verify(userMapper).updateUserFromDto(requestDto, user);
        verify(passwordEncoder).encode(requestDto.getPassword());
        assertEquals("new@email.com", actualResponseDto.getEmail());
        assertEquals("NewFirstName", actualResponseDto.getFirstName());
        assertEquals("NewLastName", actualResponseDto.getLastName());
        verify(userRepository).save(user);
    }

    @Test
    void updateMe_should_throw_EntityNotFoundException_when_principal_is_unknown() {
        // Given
        Object principal = new Object();
        UserRegistrationRequestDto requestDto = new UserRegistrationRequestDto();

        when(securityContext.getAuthentication()).thenReturn(authentication);
        when(authentication.getPrincipal()).thenReturn(principal);

        // When & Then
        assertThrows(EntityNotFoundException.class, () -> userService.updateMe(requestDto));
        verify(userRepository, never()).save(any());
    }
}
