package cl.jimenez.aguilera.caso1.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

public class AjustarStockDTO {

    @NotNull(message = "El nuevo stock disponible es obligatorio")
    @Min(value = 0, message = "El stock no puede ser negativo")
    private Integer nuevoStock;

    public AjustarStockDTO() {
    }

    public AjustarStockDTO(Integer nuevoStock) {
        this.nuevoStock = nuevoStock;
    }

    public Integer getNuevoStock() {
        return nuevoStock;
    }

    public void setNuevoStock(Integer nuevoStock) {
        this.nuevoStock = nuevoStock;
    }
}
