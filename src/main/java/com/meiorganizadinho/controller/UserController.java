package com.meiorganizadinho.controller;

import com.meiorganizadinho.dto.userdto.CreateUserDTO;
import com.meiorganizadinho.dto.userdto.LoginUserDto;
import com.meiorganizadinho.dto.userdto.RecoveryJwtTokenDto;
import com.meiorganizadinho.service.UserService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/users")
public class UserController {

    @Autowired
    private UserService userService;

    @PostMapping("/login")
    public ResponseEntity<RecoveryJwtTokenDto> authenticateUser(@RequestBody @Valid LoginUserDto loginUserDto) {
        RecoveryJwtTokenDto token = userService.authenticateUser(loginUserDto);
        return new ResponseEntity<>(token, HttpStatus.OK);
    }

    @PostMapping
    public ResponseEntity<Void> createUser(@RequestBody CreateUserDTO createUserDto) {
        userService.createUser(createUserDto);
        return new ResponseEntity<>(HttpStatus.CREATED);
    }

}
