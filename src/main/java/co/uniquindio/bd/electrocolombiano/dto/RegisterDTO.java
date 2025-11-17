package co.uniquindio.bd.electrocolombiano.dto;


import co.uniquindio.bd.electrocolombiano.model.Rol;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder

public class RegisterDTO{

    public String cedula;
    public String userName;
    public String fullName;
    public Rol role;
    public String password;


    public RegisterDTO (String userName, String password, String cedula, Rol role, String fullName) {
        this.userName = userName;
        this.password = password;
        this.cedula = cedula;
        this.role = role;
        this.fullName = fullName;

        validate();
    }

    public void validate (){
        if (userName == null || userName.isBlank())
            throw new IllegalArgumentException("El email es obligatorio");

        if (password == null || password.isBlank())
            throw new IllegalArgumentException("La contraseña es obligatoria");

        if (cedula == null || cedula.isBlank())
            throw new IllegalArgumentException("El cedula es obligatoria");

        if (role == null)
            throw new IllegalArgumentException("El role es obligatoria");

        if (fullName == null || fullName.isBlank())
            throw new IllegalArgumentException("El fullname es obligatoria");
    }
}
