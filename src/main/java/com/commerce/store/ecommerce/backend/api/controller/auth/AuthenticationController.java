package com.commerce.store.ecommerce.backend.api.controller.auth;

import com.commerce.store.ecommerce.backend.api.model.LoginBody;
import com.commerce.store.ecommerce.backend.api.model.LoginResponse;
import com.commerce.store.ecommerce.backend.api.model.RegistrationBody;
import com.commerce.store.ecommerce.backend.exception.UserAlreadyExistsException;
import com.commerce.store.ecommerce.backend.service.UserService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/auth")
public class AuthenticationController {

    private UserService userService;

    public AuthenticationController(UserService userService){
        this.userService = userService;
    }
    @PostMapping("/register")
    public ResponseEntity registerUser(@Valid @RequestBody RegistrationBody registrationBody){
//        System.out.println(registrationBody.getUserName());
        try {
            userService.registerUser(registrationBody);
            return ResponseEntity.ok().build();
        }
        catch (UserAlreadyExistsException e){
            return ResponseEntity.status(HttpStatus.CONFLICT).build();
        }
    }

    @PostMapping("/login")
    public ResponseEntity<LoginResponse> loginUser(@Valid @RequestBody LoginBody loginBody){
        String jwt = userService.loginUser(loginBody);
        if(jwt==null){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }else{
            LoginResponse loginResponse = new LoginResponse();
            loginResponse.setJwt(jwt);
            return ResponseEntity.ok(loginResponse);
        }
    }
}
