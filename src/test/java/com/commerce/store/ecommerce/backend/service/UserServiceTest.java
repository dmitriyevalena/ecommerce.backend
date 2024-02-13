package com.commerce.store.ecommerce.backend.service;

import java.util.List;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.RegisterExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import com.commerce.store.ecommerce.backend.api.model.LoginBody;
import com.commerce.store.ecommerce.backend.api.model.RegistrationBody;
import com.commerce.store.ecommerce.backend.exception.EmailFailureException;
import com.commerce.store.ecommerce.backend.exception.UserAlreadyExistsException;
import com.commerce.store.ecommerce.backend.exception.UserNotVerifiedException;
import com.commerce.store.ecommerce.backend.model.VerificationToken;
import com.commerce.store.ecommerce.backend.model.dao.VerificationTokenDAO;
import com.icegreen.greenmail.configuration.GreenMailConfiguration;
import com.icegreen.greenmail.junit5.GreenMailExtension;
import com.icegreen.greenmail.util.ServerSetupTest;

import jakarta.mail.Message;
import jakarta.mail.MessagingException;
import jakarta.transaction.Transactional;

@SpringBootTest
public class UserServiceTest {

    @RegisterExtension
    private static GreenMailExtension greenMailExtension = new GreenMailExtension(ServerSetupTest.SMTP)
            .withConfiguration(GreenMailConfiguration.aConfig().withUser("springboot", "secret"))
            .withPerMethodLifecycle(true);
//    Last string to wipe box after each method. because in next test we expect only 1 email in box

    @Autowired
    private UserService userService;
    
    @Autowired
    private VerificationTokenDAO verificationTokenDAO;

    @Test
//    Any data that we store we don't want to be kept
    @Transactional
    public void testRegisterUser() throws MessagingException {
        RegistrationBody body = new RegistrationBody();
        body.setUserName("UserA");
        body.setEmail("UserServiceTest$testRegisterUser@junit.com");
        body.setFirstName("FirstName");
        body.setLastName("LastName");
        body.setPassword("MySecretPassword123");
        Assertions.assertThrows(UserAlreadyExistsException.class,
                () -> userService.registerUser(body), "Username should already be in use.");
        body.setUserName("UserServiceTest$testRegisterUser");
        body.setEmail("UserA@junit.com");
        Assertions.assertThrows(UserAlreadyExistsException.class,
                () -> userService.registerUser(body), "Email should already be in use.");
        body.setEmail("UserServiceTest$testRegisterUser@junit.com");
        Assertions.assertDoesNotThrow(()->userService.registerUser(body),
                "User should register successfully.");
        Assertions.assertEquals(body.getEmail(), greenMailExtension.getReceivedMessages()[0].getRecipients(Message.RecipientType.TO)[0].toString());
    }

	@Test
	@Transactional
	public void testLoginUser() throws UserNotVerifiedException, EmailFailureException {
		LoginBody body = new LoginBody();
		body.setUserName("UserA-Not exists");
		body.setPassword("PasswordA123-Bad Password");
		Assertions.assertNull(userService.loginUser(body), "The user should not exist");
		body.setUserName("UserA");
		Assertions.assertNull(userService.loginUser(body),
				"The user should not exist because of incorrect password");
		body.setPassword("PasswordA123");
		Assertions.assertNotNull(userService.loginUser(body),
				"The user should login succesfully");
		body.setUserName("UserB");
		body.setPassword("PasswordB123");
		try {
			userService.loginUser(body);
			Assertions.assertTrue(false, "User should not have email verified");
		}catch (UserNotVerifiedException e) {
			Assertions.assertTrue(e.isNewEmailSent(), "Email verification should be sent.");
			Assertions.assertEquals(1, greenMailExtension.getReceivedMessages().length);
		}
		
		try {
			userService.loginUser(body);
			Assertions.assertTrue(false, "User should not have email verified");
		}catch (UserNotVerifiedException e) {
			Assertions.assertFalse(e.isNewEmailSent(), "Email verification should not be resent.");
			Assertions.assertEquals(1, greenMailExtension.getReceivedMessages().length);
		}
//        Assertions.assertDoesNotThrow(()->userService.loginUser(loginBody), "User should login successfully.");
	}
	
	@Test
	@Transactional
	public void verifyUser() throws EmailFailureException {
		Assertions.assertFalse(userService.verifyUser("Bad token"), "Token that is bad or doesn't exist should return false.");
		LoginBody body = new LoginBody();
		body.setUserName("UserB");
		body.setPassword("PasswordB123");
		try {
			userService.loginUser(body);
			Assertions.assertTrue(false, "User should not have email verified");
		}catch (UserNotVerifiedException e) {
			List<VerificationToken> tokens = verificationTokenDAO.findByUser_IdOrderByIdDesc(2L);
			Assertions.assertTrue(userService.verifyUser(tokens.get(0).getToken()), "Token should be valid.");
		}
		
	}
	
}
