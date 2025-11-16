package co.uniquindio.bd.electrocolombiano.dto;


import co.uniquindio.bd.electrocolombiano.model.Rol;

public record UserDTO(
        String cedula,
        String fullName,
        Rol role
) {}
