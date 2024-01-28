package com.commerce.store.ecommerce.backend.service;

import com.commerce.store.ecommerce.backend.api.model.LoginBody;
import com.commerce.store.ecommerce.backend.api.model.RegistrationBody;
import com.commerce.store.ecommerce.backend.exception.UserAlreadyExistsException;
import com.commerce.store.ecommerce.backend.model.LocalUser;
import com.commerce.store.ecommerce.backend.model.dao.LocalUserDAO;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class UserService {

    private LocalUserDAO localUserDAO;
    private EncryptionService encryptionService;

    private JWTService jwtService;

    public UserService(LocalUserDAO localUserDAO, EncryptionService encryptionService, JWTService jwtService) {
        this.localUserDAO = localUserDAO;
        this.encryptionService = encryptionService;
        this.jwtService = jwtService;
    }

    public LocalUser registerUser(RegistrationBody registrationBody) throws UserAlreadyExistsException {
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
//        new version of user with id inside of it
//        user = localUserDAO.save(user);
//        return user;
        return localUserDAO.save(user);
        }

        public String loginUser(LoginBody loginBody){
            Optional<LocalUser> optionalUser = localUserDAO.findByUserNameIgnoreCase(loginBody.getUserName());
            if(optionalUser.isPresent()){
                LocalUser user = optionalUser.get();
                if(encryptionService.verifyPassword(loginBody.getPassword(),user.getPassword())){
                   return jwtService.generateJWT(user);
                }

            }
            return null;
        }
}
