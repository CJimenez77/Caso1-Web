package cl.jimenez.aguilera.caso1.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

import java.util.ArrayList;
import java.util.List;

public class CrearPedidoDTO {

    @NotNull(message = "El identificador del cliente es obligatorio")
    private Long clienteId;

    private String observacion;

    @NotEmpty(message = "El pedido debe contener al menos un item")
    @Valid
    private List<CrearItemPedidoDTO> items = new ArrayList<>();

    public CrearPedidoDTO() {
    }

    public CrearPedidoDTO(Long clienteId, String observacion, List<CrearItemPedidoDTO> items) {
        this.clienteId = clienteId;
        this.observacion = observacion;
        this.items = items;
    }

    public Long getClienteId() {
        return clienteId;
    }

    public void setClienteId(Long clienteId) {
        this.clienteId = clienteId;
    }

    public String getObservacion() {
        return observacion;
    }

    public void setObservacion(String observacion) {
        this.observacion = observacion;
    }

    public List<CrearItemPedidoDTO> getItems() {
        return items;
    }

    public void setItems(List<CrearItemPedidoDTO> items) {
        this.items = items;
    }
}
