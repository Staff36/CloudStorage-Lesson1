package DAO;

import Data.User;

import java.sql.SQLException;

public interface UserDAO {
    User getUserByLogin(String userName) throws SQLException;
    void createNewUser(User user);
    void updateUser(User oldUser, User newUser);
    boolean deleteUser(User user);
}
