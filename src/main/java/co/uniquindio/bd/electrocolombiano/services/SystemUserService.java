package co.uniquindio.bd.electrocolombiano.services;

import co.uniquindio.bd.electrocolombiano.dao.UserDAO;
import co.uniquindio.bd.electrocolombiano.dto.CustomerDTO;
import co.uniquindio.bd.electrocolombiano.dto.LoginDTO;
import co.uniquindio.bd.electrocolombiano.dto.RegisterDTO;
import co.uniquindio.bd.electrocolombiano.dto.UserDTO;
import co.uniquindio.bd.electrocolombiano.model.SystemUser;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@Builder

public class SystemUserService {

    protected UserDAO userDAO;

    public SystemUserService(UserDAO userDAO) {
        this.userDAO = userDAO;
    }

    public UserDTO login(LoginDTO loginDTO) throws Exception {


        UserDTO userDTO = userDAO.findByUserName(loginDTO.getUserName());

        if (userDTO == null) {
            throw new Exception("El usuario no existe");
        }

        SystemUser systemUser = SystemUser.builder()
                .cedula(userDTO.getCedula())
                .fullName(userDTO.getFullName())
                .userName(userDTO.getUserName())
                .password(userDTO.getPassword())
                .role(userDTO.getRole())
                .build();
        if (!systemUser.getPassword().equals(loginDTO.getPassword())) {
            throw new Exception("Contraseña incorrecta");
        }

        return new UserDTO(
                systemUser.getCedula(),
                systemUser.getFullName(),
                systemUser.getUserName(),
                systemUser.getPassword(),
                systemUser.getRole()
        );
    }

    public UserDTO register(RegisterDTO dto) throws Exception {
        UserDTO userExistente = userDAO.findByUserName(dto.getUserName());
        if (userExistente != null) {
            throw new Exception("El nombre de usuario ya existe");
        }
        SystemUser nuevo = SystemUser.builder()
                .cedula(dto.getCedula())
                .userName(dto.getUserName())
                .password(dto.getPassword())
                .fullName(dto.getFullName())
                .role(dto.getRole())
                .build();
        UserDTO user = new UserDTO(nuevo.getCedula(),nuevo.getFullName(), nuevo.getUserName(),
                nuevo.getPassword(),nuevo.getRole());
        userDAO.save(user);
        return user;
    }

    public UserDTO getUser(String cedula) throws Exception {
        UserDTO user = userDAO.findByCedula(cedula);
        if(user == null){
            throw new Exception("El usuario no existe");
        }
        return user;
    }
}
