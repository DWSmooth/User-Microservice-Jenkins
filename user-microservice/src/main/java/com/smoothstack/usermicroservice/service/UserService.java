package com.smoothstack.usermicroservice.service;

import com.smoothstack.common.models.User;
import com.smoothstack.common.models.UserInformation;
import com.smoothstack.common.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.Optional;

@Service
public class UserService {
    @Autowired
    UserRepository userRepository;

    /**
     * Returns a boolean value based on whether there is already a user under a given username
     *
     * @param userName
     * @return true if username is claimed within the database, otherwise returns false
     */
    @Transactional
    public boolean userNameExists(String userName) {
        if (userRepository.findTopByUserName(userName).isPresent())
            return true;

        return false;
    }

    /**
     * Returns a boolean value based on whether a user exists in the database under a given userid
     *
     * @param id the integer value of a user id
     * @return true if user exists with this id, otherwise false
     */
    @Transactional
    public boolean userIdExists(Integer id) {
        if (id == null)
            return false;

        if (userRepository.findById(id).isEmpty())
            return false;

        return true;
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
    @Transactional
    public Integer createUser(User user) {
        UserInformation newInformation = new UserInformation();
        newInformation.setUser(user);
        user.setUserInformation(newInformation);
        return userRepository.save(user).getId();
    }


    /**
     * Deletes a user from the database
     *
     * @param userid: integer value of user id
     */
    @Transactional
    public void deleteUser(Integer userid) {
        userRepository.deleteById(userid);
    }
}
