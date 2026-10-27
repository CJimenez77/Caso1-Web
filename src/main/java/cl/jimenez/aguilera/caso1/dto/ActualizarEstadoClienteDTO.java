package cl.jimenez.aguilera.caso1.dto;

import jakarta.validation.constraints.NotBlank;

public class ActualizarEstadoClienteDTO {

    @NotBlank(message = "El estado del cliente es obligatorio")
    private String estado;

    public ActualizarEstadoClienteDTO() {
    }

    public ActualizarEstadoClienteDTO(String estado) {
        this.estado = estado;
    }

    public String getEstado() {
        return estado;
    }

    public void setEstado(String estado) {
        this.estado = estado;
    }
}
