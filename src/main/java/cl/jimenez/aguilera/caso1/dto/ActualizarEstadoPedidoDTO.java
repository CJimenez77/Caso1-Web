package cl.jimenez.aguilera.caso1.dto;

import cl.jimenez.aguilera.caso1.model.EstadoPedido;
import jakarta.validation.constraints.NotNull;

public class ActualizarEstadoPedidoDTO {

    @NotNull(message = "El nuevo estado es obligatorio")
    private EstadoPedido estado;

    public ActualizarEstadoPedidoDTO() {
    }

    public ActualizarEstadoPedidoDTO(EstadoPedido estado) {
        this.estado = estado;
    }

    public EstadoPedido getEstado() {
        return estado;
    }

    public void setEstado(EstadoPedido estado) {
        this.estado = estado;
    }
}
