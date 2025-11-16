package co.uniquindio.bd.electrocolombiano.services;

import co.uniquindio.bd.electrocolombiano.dao.UserDAO;
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

        if (loginDTO.userName() == null || loginDTO.userName().isEmpty()) {
            throw new Exception("El usuario no puede estar vacio");
        }
        if (loginDTO.password() == null || loginDTO.password().isBlank()) {
            throw new Exception("El usuario no puede estar vacio");
        }

        UserDTO userDTO = userDAO.findByUserName(loginDTO.userName());

        if (userDTO == null) {
            throw new Exception("El usuario no existe");
        }

        SystemUser systemUser = SystemUser.builder()
                .cedula(userDTO.cedula())
                .fullName(userDTO.fullName())
                .userName(userDTO.userName())
                .password(userDTO.password())
                .role(userDTO.role())
                .build();
        if (!systemUser.getPassword().equals(loginDTO.password())) {
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
        UserDTO userExistente = userDAO.findByUserName(dto.userName());
        if (userExistente != null) {
            throw new Exception("El nombre de usuario ya existe");
        }
        SystemUser nuevo = SystemUser.builder()
                .cedula(dto.cedula())
                .userName(dto.userName())
                .password(dto.password())
                .fullName(dto.fullName())
                .role(dto.role())
                .build();

        return new UserDTO(nuevo.getCedula(),nuevo.getFullName(), nuevo.getUserName(),
                nuevo.getPassword(),nuevo.getRole());

    }
}
