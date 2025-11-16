package co.uniquindio.bd.electrocolombiano.dao;

import co.uniquindio.bd.electrocolombiano.dto.UserDTO;
import java.util.LinkedList;

public interface UserDAO {
    void save(UserDTO user);
    UserDTO findById(int id);
    UserDTO findByEmail(String email);
    UserDTO findByUserName(String username);
    LinkedList<UserDTO> findAll();
    void update(UserDTO user);
    void delete(int id);
}
