package co.uniquindio.bd.electrocolombiano.dao;

import co.uniquindio.bd.electrocolombiano.dto.UserDTO;
import java.util.LinkedList;

public interface UserDAO {
    void save(UserDTO user);

    UserDTO findByUserName(String username);

    UserDTO findByCedula(String cedula);

    LinkedList<UserDTO> findAll();

    void update(UserDTO user);

    void delete(String id);
}
