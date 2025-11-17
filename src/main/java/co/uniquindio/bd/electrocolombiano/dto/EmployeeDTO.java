package co.uniquindio.bd.electrocolombiano.dto;

import co.uniquindio.bd.electrocolombiano.model.Rol;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder(toBuilder = true)
@AllArgsConstructor
public class EmployeeDTO {

    private String cedula;
    private String fullName;
    private Rol role;

    // Constructor personalizado (compatible con tu código existente)
    public EmployeeDTO(String cedula, Rol role, String fullName) {
        this.cedula = cedula;
        this.role = role;
        this.fullName = fullName;
        validateFields();
    }

    private void validateFields() {
        if (cedula == null || cedula.isBlank()) {
            throw new IllegalArgumentException("La cédula es obligatoria");
        }
        if (role == null) {
            throw new IllegalArgumentException("El rol es obligatorio");
        }
        if (fullName == null || fullName.isBlank()) {
            throw new IllegalArgumentException("El nombre completo es obligatorio");
        }
    }
}