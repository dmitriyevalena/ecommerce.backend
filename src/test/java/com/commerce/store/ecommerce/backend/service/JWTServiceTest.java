package com.commerce.store.ecommerce.backend.service;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import com.commerce.store.ecommerce.backend.model.LocalUser;
import com.commerce.store.ecommerce.backend.model.dao.LocalUserDAO;

@SpringBootTest
public class JWTServiceTest {
	
	@Autowired
	private JWTService jwtService;
	
	@Autowired
	private LocalUserDAO localUserDAO;
	 
	@Test
	public void testVerificationTokenNotUsableForLogin() {
		LocalUser user = localUserDAO.findByUserNameIgnoreCase("UserA").get();
		String token = jwtService.generateVerificationJWT(user);
		Assertions.assertNull(jwtService.getUserName(token), "Verification token should not contain username.");
	}
	
	@Test
	public void testAuthTokenReturnsUsername() {
		LocalUser user = localUserDAO.findByUserNameIgnoreCase("UserA").get();
		String token = jwtService.generateJWT(user);
		Assertions.assertEquals(user.getUserName(), jwtService.getUserName(token), "Token for auth should contain user's username.");
	}

}
