package com.commerce.store.ecommerce.backend.api.controller.auth;

import com.commerce.store.ecommerce.backend.api.model.LoginBody;
import com.commerce.store.ecommerce.backend.api.model.LoginResponse;
import com.commerce.store.ecommerce.backend.api.model.PasswordResetBody;
import com.commerce.store.ecommerce.backend.api.model.RegistrationBody;
import com.commerce.store.ecommerce.backend.exception.EmailFailureException;
import com.commerce.store.ecommerce.backend.exception.EmailNotFoundException;
import com.commerce.store.ecommerce.backend.exception.UserAlreadyExistsException;
import com.commerce.store.ecommerce.backend.exception.UserNotVerifiedException;
import com.commerce.store.ecommerce.backend.model.LocalUser;
import com.commerce.store.ecommerce.backend.service.UserService;
import jakarta.validation.Valid;
import org.springframework.boot.actuate.autoconfigure.observation.ObservationProperties;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

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
        catch (EmailFailureException e){
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @PostMapping("/login")
    public ResponseEntity<LoginResponse> loginUser(@Valid @RequestBody LoginBody loginBody){
        String jwt = null;
        try {
            jwt = userService.loginUser(loginBody);
        }
        catch (UserNotVerifiedException e){
            LoginResponse loginResponse = new LoginResponse();
            loginResponse.setSuccess(false);
            String reason = "USER_NOT_VERIFIED";
            if(e.isNewEmailSent()){
                reason+="_EMAIL_RESENT";
            }
            loginResponse.setFailureReason(reason);
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(loginResponse);
        }
        catch (EmailFailureException e){
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
        if(jwt==null){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }else{
            LoginResponse loginResponse = new LoginResponse();
            loginResponse.setJwt(jwt);
            loginResponse.setSuccess(true);
            return ResponseEntity.ok(loginResponse);
        }
    }

    @PostMapping("/verify")
    public ResponseEntity verifyEmail(@RequestParam String token){
        if(userService.verifyUser(token)){
            return ResponseEntity.ok().build();
        }else {
            return ResponseEntity.status(HttpStatus.CONFLICT).build();
        }
    }

    @GetMapping("/me")
    public LocalUser getLoggedInUserProfile(@AuthenticationPrincipal LocalUser user){
//        SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        return user;
    }
    
	@PostMapping("/forgot")
	public ResponseEntity forgotPassword(@RequestParam String email) {
		try {
			userService.forgotPassword(email);
			return ResponseEntity.ok().build();
		} catch (EmailNotFoundException e) {
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
		} catch (EmailFailureException e) {
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
		}
	}
	
	@PostMapping("/reset")
	public ResponseEntity resetPassword(@Valid @RequestBody PasswordResetBody body) {
		userService.resetPassword(body);
		return ResponseEntity.ok().build();
	}
}
