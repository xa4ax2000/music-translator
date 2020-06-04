package org.hyun.music.translator.api.account;

import org.hyun.music.translator.model.account.exception.UserExistsException;
import org.hyun.music.translator.model.account.exception.UserNotFoundException;
import org.hyun.music.translator.model.auth.User;
import org.hyun.music.translator.model.auth.UserType;
import org.hyun.music.translator.model.payload.inbound.DeleteUserRequest;
import org.hyun.music.translator.model.payload.inbound.SignUpRequest;
import org.hyun.music.translator.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.Optional;

@Service
public class UserServiceImpl implements UserService {

    @Autowired
    private UserRepository userRepository;

    public void registerNewUserAccount(SignUpRequest signUpRequest) throws UserExistsException {
        if (userExists(signUpRequest.getUsername())) {
            throw new UserExistsException(
                    "There is an account with the username:" + signUpRequest.getUsername());
        }
        User user = new User();
        user.setUsername(signUpRequest.getUsername());
        user.setFirstName(signUpRequest.getFirstName());
        user.setLastName(signUpRequest.getLastName());

        PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
        user.setPassword(passwordEncoder.encode(signUpRequest.getPassword()));

        user.setEmail(signUpRequest.getEmail());
        user.setUserType(UserType.USER);
        user.setCreated(Timestamp.valueOf(LocalDateTime.now()));

        // Todo: Email verification?
        userRepository.save(user);
    }

    private boolean userExists(String username){
        Optional<User> user= userRepository.findByUsername(username);
        return user.isPresent();
    }

    @Override
    public void deleteUserAccount(DeleteUserRequest deleteUserRequest) throws UserNotFoundException {
        if (!userExists(deleteUserRequest.getUsername())){
            throw new UserNotFoundException(
                    "There is no account with the username: " + deleteUserRequest.getUsername());
        }
        userRepository.deleteByUsername(deleteUserRequest.getUsername());
    }
}

