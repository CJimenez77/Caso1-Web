package cl.jimenez.aguilera.caso1.exception;

public class StockInsuficienteException extends RuntimeException {

    private final String nombreProducto;
    private final Integer cantidadSolicitada;
    private final Integer stockDisponible;

    public StockInsuficienteException(String nombreProducto, Integer cantidadSolicitada, Integer stockDisponible) {
        super("Stock insuficiente para el producto: " + nombreProducto + " (Solicitado: " + cantidadSolicitada + ", Disponible: " + stockDisponible + ")");
        this.nombreProducto = nombreProducto;
        this.cantidadSolicitada = cantidadSolicitada;
        this.stockDisponible = stockDisponible;
    }

    public String getNombreProducto() {
        return nombreProducto;
    }

    public Integer getCantidadSolicitada() {
        return cantidadSolicitada;
    }

    public Integer getStockDisponible() {
        return stockDisponible;
    }
}
