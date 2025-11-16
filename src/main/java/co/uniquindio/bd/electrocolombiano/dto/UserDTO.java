package co.uniquindio.bd.electrocolombiano.dto;


import co.uniquindio.bd.electrocolombiano.model.Rol;

public record UserDTO(
        String cedula,
        String fullName,
        String userName,
        String password,
        Rol role
) {}
