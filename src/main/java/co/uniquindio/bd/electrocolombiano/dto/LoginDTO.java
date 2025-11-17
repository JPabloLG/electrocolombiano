package co.uniquindio.bd.electrocolombiano.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Builder(toBuilder = true)

public class LoginDTO {

    private String userName;
    private String password;

    public LoginDTO (String userName, String password){
        if (userName == null || userName.isBlank())
            throw new IllegalArgumentException("El correo electronico es obligatorio");

        if (password == null || password.isBlank())
            throw new IllegalArgumentException("La contraseña es obligatoria");

        this.userName = userName;
        this.password = password;
    }
}
