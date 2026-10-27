package cl.jimenez.aguilera.caso1.dto;

import java.time.OffsetDateTime;
import java.util.Map;

public class RespuestaError {

    private String mensaje;
    private int codigoEstado;
    private OffsetDateTime marcaTiempo;
    private Map<String, Object> detalles;

    public RespuestaError() {
        this.marcaTiempo = OffsetDateTime.now();
    }

    public RespuestaError(String mensaje, int codigoEstado) {
        this.mensaje = mensaje;
        this.codigoEstado = codigoEstado;
        this.marcaTiempo = OffsetDateTime.now();
    }

    public RespuestaError(String mensaje, int codigoEstado, Map<String, Object> detalles) {
        this.mensaje = mensaje;
        this.codigoEstado = codigoEstado;
        this.detalles = detalles;
        this.marcaTiempo = OffsetDateTime.now();
    }

    public String getMensaje() {
        return mensaje;
    }

    public void setMensaje(String mensaje) {
        this.mensaje = mensaje;
    }

    public int getCodigoEstado() {
        return codigoEstado;
    }

    public void setCodigoEstado(int codigoEstado) {
        this.codigoEstado = codigoEstado;
    }

    public OffsetDateTime getMarcaTiempo() {
        return marcaTiempo;
    }

    public void setMarcaTiempo(OffsetDateTime marcaTiempo) {
        this.marcaTiempo = marcaTiempo;
    }

    public Map<String, Object> getDetalles() {
        return detalles;
    }

    public void setDetalles(Map<String, Object> detalles) {
        this.detalles = detalles;
    }
}
