package co.uniquindio.bd.electrocolombiano.dto;


import co.uniquindio.bd.electrocolombiano.model.Rol;

public record RegisterDTO(String cedula , String userName, String fullName, Rol role, String password ) {

    public RegisterDTO {
        if (userName == null || userName.isBlank())
            throw new IllegalArgumentException("El usuario es obligatorio");

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
