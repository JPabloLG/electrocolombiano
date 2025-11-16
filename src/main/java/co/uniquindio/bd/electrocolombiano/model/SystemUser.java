package co.uniquindio.bd.electrocolombiano.model;
import  co.uniquindio.bd.electrocolombiano.model.Rol;

import lombok.*;

@Setter
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class SystemUser {

    private String cedula;
    private String userName;
    private String password;
    private String fullName;
    private Rol role;

}
