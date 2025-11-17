package co.uniquindio.bd.electrocolombiano.dto;

import co.uniquindio.bd.electrocolombiano.model.Rol;
import lombok.Builder;
import lombok.Getter;


@Getter
@Builder(toBuilder = true)

public class UserDTO{

    public String cedula;
    public String fullName;
    public String userName;
    public String password;
    public Rol role;

    public UserDTO (String cedula, String fullName, String userName, String password, Rol role) {
        this.cedula = cedula;
        this.fullName = fullName;
        this.userName = userName;
        this.password = password;
        this.role = role;

        validate();
    }

    public void validate (){

        if (userName == null || userName.isBlank())
            throw new IllegalArgumentException("El email es obligatorio");

        if (cedula == null || cedula.isBlank())
            throw new IllegalArgumentException("La cedula es obligatoria");

        if (role == null)
            throw new IllegalArgumentException("El role es obligatoria");

        if (fullName == null || fullName.isBlank())
            throw new IllegalArgumentException("El fullname es obligatoria");

        if (password == null || password.isBlank())
            throw new IllegalArgumentException("El password es obligatoria");
    }

}
