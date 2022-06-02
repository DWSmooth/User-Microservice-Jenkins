package com.smoothstack.usermicroservice.service;

import com.smoothstack.common.models.User;
import com.smoothstack.common.models.UserInformation;
import com.smoothstack.common.models.UserRole;
import com.smoothstack.common.repositories.UserRepository;
import com.smoothstack.common.services.CommonLibraryTestingService;
import com.smoothstack.usermicroservice.exceptions.InsufficientInformationException;
import com.smoothstack.usermicroservice.exceptions.InsufficientPasswordException;
import com.smoothstack.usermicroservice.exceptions.UserNotFoundException;
import com.smoothstack.usermicroservice.exceptions.UsernameTakenException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class UserService {
    @Autowired
    UserRepository userRepository;

    /**
     * Returns a boolean depending on whether there is a user in the database with said username
     *
     * @param username the username of the user
     * @return true if a user with that username exists otherwise false
     */
    public boolean usernameExists(String username) {
        if (username == null)
            return false;

        return userRepository.findTopByUserName(username).isPresent();
    }

    /**
     * Returns a boolean depending on whether there is a user in the database with a provided id
     *
     * @param id the id of the perspective user
     * @return true if user exists with this id, otherwise false
     */
    public boolean userIdExists(Integer id) {
        if (id == null)
            return false;

        return userRepository.findById(id).isPresent();
    }

    /**
     * Returns the user in the database with the associated username
     *
     * @param username the username of the user to search
     * @return The user whose username matches the provided username
     * @throws UserNotFoundException if a user with the associated username cannot be found
     */
    public User getUserByUsername(String username) throws UserNotFoundException {
        if (usernameExists(username)) {
            return userRepository.findTopByUserName(username).get();
        }
        throw new UserNotFoundException("No user with username:" + username);
    }

    /**
     * Returns the user in the database with the associated user id
     *
     * @param id the users id
     * @return the user associated with an id
     * @throws UserNotFoundException if a user with the associated id is not found
     */
    public User getUserById(Integer id) throws  UserNotFoundException {
        if (userIdExists(id)) {
            return userRepository.findById(id).get();
        }
        throw new UserNotFoundException("No user with id:" + id);
    }

    /**
     * Returns a boolean value based on whether a given password is considered acceptable
     *
     * @param password
     * @return true if password is ok, false if password is not valid
     */
    public boolean validPassword(String password) {
        //TODO
        return true;
    }

    /**
     * Adds a given user to the database
     *
     * @param user the user to be added
     * @return the id of the created user
     */
    public Integer createUser(User user) throws InsufficientInformationException, UsernameTakenException, InsufficientPasswordException {
        if (user == null) throw new InsufficientInformationException("User not provided");
        if (user.getUserName() == null) throw new InsufficientInformationException("Username not provided");
        if (user.getPassword() == null) throw new InsufficientInformationException("Password not provided");
        if (usernameExists(user.getUserName())) throw new UsernameTakenException("Username is taken");
        if (!validPassword(user.getPassword())) throw new InsufficientPasswordException("Password is insufficient");

        UserInformation newInformation = new UserInformation();
        newInformation.setUser(user);
        user.setUserInformation(newInformation);
        return userRepository.save(user).getId();
    }

    /**
     * updates a user based off of a provided userid
     *
     * @param user a user containing the userid of the user to update and the other fields to update
     */
    public void updateUser(User user) throws UserNotFoundException, InsufficientInformationException,
            UsernameTakenException, InsufficientPasswordException{
        if (user == null) throw new InsufficientInformationException("User not provided");
        if (user.getId() == null) throw new InsufficientInformationException("User Id not provided");
        User toUpdate = getUserById(user.getId());
        if (!user.getUserName().equals(toUpdate.getUserName())) {
            if (usernameExists(user.getUserName())) throw new UsernameTakenException("Username is taken");
            toUpdate.setUserName(user.getUserName());
        }
        if (!user.getPassword().equals(toUpdate.getPassword())) {
            if (!validPassword(user.getPassword())) throw new InsufficientPasswordException("Password is invalid");
            toUpdate.setPassword(user.getPassword());
        }

        userRepository.save(toUpdate);
    }


    /**
     * Deletes a user from the database
     *
     * @param userid: integer value of user id
     */
    public void deleteUser(Integer userid) {
        userRepository.deleteById(userid);
    }

    public List<User> getAllUsers() {
        List<User> users = new ArrayList<>();

        for (User user: userRepository.findAll()) {
            users.add(User.builder()
                    .id(user.getId())
                    .userName(user.getUserName())
                    .build()
            );
        }

        return users;
    }

    /**
     * Returns a login pojo so the authentication microservice can authenticate a user
     *
     * @param username
     * @return
     */
    public User getLoginInfo(String username) {
        Optional<User> foundUser = userRepository.findTopByUserName(username);

        if (foundUser.isPresent()) {
            User user = foundUser.get();

            User.UserBuilder loginBuilder = User.builder()
                    .userName(user.getUserName())
                    .password(user.getPassword())
                    .enabled(user.isEnabled());

            if (!user.getUserRoles().isEmpty()) {
                List<UserRole> roles = new ArrayList<>();

                for (UserRole role: user.getUserRoles()) {
                    roles.add(UserRole.builder()
                            .roleName(role.getRoleName())
                            .build()
                    );
                }

                loginBuilder.userRoles(roles);
            }

            return loginBuilder.build();
        }

        return null;
    }
}
