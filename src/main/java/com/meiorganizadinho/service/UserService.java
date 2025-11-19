package com.meiorganizadinho.service;

import com.meiorganizadinho.config.SecurityConfiguration;
import com.meiorganizadinho.dto.userdto.CreateUserDTO;
import com.meiorganizadinho.dto.userdto.LoginUserDto;
import com.meiorganizadinho.dto.userdto.RecoveryJwtTokenDto;
import com.meiorganizadinho.entity.Role;
import com.meiorganizadinho.entity.User;
import com.meiorganizadinho.entity.UserDetailsImpl;
import com.meiorganizadinho.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserService {

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private JwtTokenService jwtTokenService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private SecurityConfiguration securityConfiguration;

    public RecoveryJwtTokenDto authenticateUser(LoginUserDto loginUserDto) {
        UsernamePasswordAuthenticationToken usernamePasswordAuthenticationToken =
                new UsernamePasswordAuthenticationToken(loginUserDto.email(), loginUserDto.password());
        Authentication authentication = authenticationManager.authenticate(usernamePasswordAuthenticationToken);

        UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();

        return new RecoveryJwtTokenDto(jwtTokenService.generateToken(userDetails));
    }

    public void createUser(CreateUserDTO createUserDto) {

        User newUser = User.builder()
                .email(createUserDto.email())
                .password(securityConfiguration.passwordEncoder().encode(createUserDto.password()))
                .roles(List.of(Role.builder().name(createUserDto.role()).build()))
                .build();

        userRepository.save(newUser);
    }
}
