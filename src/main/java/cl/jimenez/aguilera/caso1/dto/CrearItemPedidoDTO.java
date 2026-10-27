package cl.jimenez.aguilera.caso1.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

public class CrearItemPedidoDTO {

    @NotNull(message = "El identificador de producto es obligatorio")
    private Long productoId;

    @NotNull(message = "La cantidad es obligatoria")
    @Min(value = 1, message = "La cantidad debe ser de al menos 1 unidad")
    private Integer cantidad;

    public CrearItemPedidoDTO() {
    }

    public CrearItemPedidoDTO(Long productoId, Integer cantidad) {
        this.productoId = productoId;
        this.cantidad = cantidad;
    }

    public Long getProductoId() {
        return productoId;
    }

    public void setProductoId(Long productoId) {
        this.productoId = productoId;
    }

    public Integer getCantidad() {
        return cantidad;
    }

    public void setCantidad(Integer cantidad) {
        this.cantidad = cantidad;
    }
}
