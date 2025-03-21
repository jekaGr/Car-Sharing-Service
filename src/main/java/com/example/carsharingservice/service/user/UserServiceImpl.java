package com.example.carsharingservice.service.user;

import com.example.carsharingservice.dto.role.RoleNameDto;
import com.example.carsharingservice.dto.user.UserRegistrationRequestDto;
import com.example.carsharingservice.dto.user.UserResponseDto;
import com.example.carsharingservice.exception.EntityNotFoundException;
import com.example.carsharingservice.exception.RegistrationException;
import com.example.carsharingservice.mapper.UserMapper;
import com.example.carsharingservice.model.Role;
import com.example.carsharingservice.model.User;
import com.example.carsharingservice.repository.RoleRepository;
import com.example.carsharingservice.repository.UserRepository;
import java.util.HashSet;
import java.util.Set;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;
    private final UserMapper userMapper;
    private final PasswordEncoder passwordEncoder;
    private final RoleRepository roleRepository;

    @Override
    public UserResponseDto register(UserRegistrationRequestDto requestDto)
            throws RegistrationException {
        String email = requestDto.getEmail();
        if (userRepository.existsByEmail(email)) {
            throw new RegistrationException("Can't register user by the email - " + email);
        }
        User user = userMapper.toUser(requestDto);
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        userRepository.save(user);
        return userMapper.toUserResponse(user);
    }

    @Override
    public UserResponseDto updateRole(Long id, RoleNameDto requestDto) {
        User user = userRepository.findById(id).orElseThrow(() ->
                new EntityNotFoundException("User with id " + id + " not found"));

        Role role = roleRepository.findByName(requestDto.getRoleName())
                .orElseThrow(() -> new EntityNotFoundException("Role with name "
                        + requestDto.getRoleName() + " not found"));

        Set<Role> roles = new HashSet<>();
        roles.add(role);
        user.setRoles(roles);
        return userMapper.toUserResponse(userRepository.save(user));
    }

    @Override
    public User getMe() {
        return getCurrentUser();
    }

    @Override
    public UserResponseDto updateMe(UserRegistrationRequestDto requestDto) {
        User user = getCurrentUser();
        userMapper.updateUserFromDto(requestDto, user);
        user.setPassword(passwordEncoder.encode(requestDto.getPassword()));
        userRepository.save(user);
        return userMapper.toUserResponse(user);
    }

    private User getCurrentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null) {
            throw new EntityNotFoundException("Authentication context is null");
        }
        Object principal = authentication.getPrincipal();
        if (principal instanceof User user) {
            return user;
        } else if (principal instanceof UserDetails userDetails) {
            return userRepository.findByEmailWithRoles(userDetails.getUsername())
                    .orElseThrow(() -> new EntityNotFoundException("User not found"));
        } else {
            throw new EntityNotFoundException("Error when getting current user");
        }
    }
}
