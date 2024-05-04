package com.commerce.store.ecommerce.backend.service;

import com.commerce.store.ecommerce.backend.api.model.LoginBody;
import com.commerce.store.ecommerce.backend.api.model.PasswordResetBody;
import com.commerce.store.ecommerce.backend.api.model.RegistrationBody;
import com.commerce.store.ecommerce.backend.exception.EmailFailureException;
import com.commerce.store.ecommerce.backend.exception.EmailNotFoundException;
import com.commerce.store.ecommerce.backend.exception.UserAlreadyExistsException;
import com.commerce.store.ecommerce.backend.exception.UserNotVerifiedException;
import com.commerce.store.ecommerce.backend.model.LocalUser;
import com.commerce.store.ecommerce.backend.model.VerificationToken;
import com.commerce.store.ecommerce.backend.model.dao.LocalUserDAO;
import com.commerce.store.ecommerce.backend.model.dao.VerificationTokenDAO;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.util.List;
import java.util.Optional;

@Service
public class UserService {

    private LocalUserDAO localUserDAO;

    private VerificationTokenDAO verificationTokenDAO;
    private EncryptionService encryptionService;

    private JWTService jwtService;

    private EmailService emailService;

    public UserService(LocalUserDAO localUserDAO, EncryptionService encryptionService, JWTService jwtService,
                       EmailService emailService, VerificationTokenDAO verificationTokenDAO) {
        this.localUserDAO = localUserDAO;
        this.encryptionService = encryptionService;
        this.jwtService = jwtService;
        this.emailService = emailService;
        this.verificationTokenDAO = verificationTokenDAO;
    }

    public LocalUser registerUser(RegistrationBody registrationBody) throws UserAlreadyExistsException, EmailFailureException {
        if(localUserDAO.findByUserNameIgnoreCase(registrationBody.getUserName()).isPresent()
                || localUserDAO.findByEmailIgnoreCase(registrationBody.getEmail()).isPresent()){
            throw new UserAlreadyExistsException();
        }
        LocalUser user = new LocalUser();
        user.setUserName(registrationBody.getUserName());
        user.setFirstName(registrationBody.getFirstName());
        user.setLastName(registrationBody.getLastName());
        user.setEmail(registrationBody.getEmail());
        //TODO encrypte passwords!!!
        user.setPassword(encryptionService.encryptPassword(registrationBody.getPassword()));
        VerificationToken verificationToken = createVerificationToken(user);
        emailService.sendVerificationEmail(verificationToken);
//        verificationTokenDAO.save(verificationToken);
//        new version of user with id inside of it
        return localUserDAO.save(user);
        }

        private VerificationToken createVerificationToken(LocalUser user){
            VerificationToken verificationToken = new VerificationToken();
            verificationToken.setToken(jwtService.generateVerificationJWT(user));
            verificationToken.setCreatedTimestamp(new Timestamp(System.currentTimeMillis()));
            verificationToken.setUser(user);
            user.getVerificationTokens().add(verificationToken);
            return verificationToken;
        }
        
        public String loginUser(LoginBody loginBody) throws UserNotVerifiedException, EmailFailureException{
            Optional<LocalUser> optionalUser = localUserDAO.findByUserNameIgnoreCase(loginBody.getUserName());
            if(optionalUser.isPresent()){
                LocalUser user = optionalUser.get();
                if(encryptionService.verifyPassword(loginBody.getPassword(),user.getPassword())){
                    if(user.isEmailVerified()) {
                        return jwtService.generateJWT(user);
                    }else {
                        List<VerificationToken> verificationTokens = user.getVerificationTokens();
                        boolean resend  = verificationTokens.size()==0 ||
                                verificationTokens.get(0).getCreatedTimestamp().before(new Timestamp(System.currentTimeMillis()-(60*60*1000)));
                        if(resend){
                            VerificationToken verificationToken = createVerificationToken(user);
                            verificationTokenDAO.save(verificationToken);
                            emailService.sendVerificationEmail(verificationToken);
                        }
                        throw new UserNotVerifiedException(resend);
                    }
                }

            }
            return null;
        }

//        we need create proper session, if we change data
        @Transactional
        public boolean verifyUser(String token){
            Optional<VerificationToken> opToken = verificationTokenDAO.findByToken(token);
            if(opToken.isPresent()){
                VerificationToken verificationToken = opToken.get();
                LocalUser user = verificationToken.getUser();
                if(!user.isEmailVerified()){
                    user.setEmailVerified(true);
                    localUserDAO.save(user);
                    verificationTokenDAO.deleteByUser(user);
                    return true;
                }
            }
            return false;
        }
        
        public void forgotPassword(String email) throws EmailNotFoundException, EmailFailureException {
        	Optional<LocalUser> opUser = localUserDAO.findByEmailIgnoreCase(email);
        	if(opUser.isPresent()) {
        		LocalUser user = opUser.get();
        		String token = jwtService.generatePasswordResetJWT(user);
        		emailService.sendPasswordResetEmail(user, token);
        	}else {
        		throw new EmailNotFoundException();
        	}
        }
        
		public void resetPassword(PasswordResetBody body) {
			String email = jwtService.getResetPasswordEmail(body.getToken());
			Optional<LocalUser> opUser = localUserDAO.findByEmailIgnoreCase(email);
			if (opUser.isPresent()) {
				LocalUser user = opUser.get();
				user.setPassword(encryptionService.encryptPassword(body.getPassword()));
				localUserDAO.save(user);
			}
		}
	}
